package com.mentality.homeostatic_heater;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/** Persistent, server-authoritative heater with support for the legacy coal-only saves. */
public final class CoalHeaterBlockEntity extends BlockEntity {
    private static final int OLD_TICKS_PER_COAL = 1600;
    private final HeaterFuelState fuel = new HeaterFuelState();
    private int crackleCooldown;
    private long displayMinutes;
    private int displaySeconds;
    private boolean displayActive;
    private boolean displayAlwaysActive;
    private boolean displayEnabled = true;

    public CoalHeaterBlockEntity(BlockPos pos, BlockState state) {
        super(HomeostaticHeater.entityTypeFor(state), pos, state);
    }

    public boolean addCoal() {
        if (!fuel.addCoal(HomeostaticHeater.config())) return false;
        changed();
        return true;
    }

    public boolean addLava() {
        if (!fuel.addLava(HomeostaticHeater.config())) return false;
        changed();
        return true;
    }

    public void toggleEnabled() {
        fuel.toggleEnabled();
        changed();
    }

    private void changed() {
        syncLit();
        markDirty();
        syncStatus();
    }

    public static void tick(World world, BlockPos pos, BlockState state, CoalHeaterBlockEntity heater) {
        HeaterConfig config = HomeostaticHeater.config();
        int before = heater.fuel.burnTicksLeft();
        heater.fuel.tick(config);
        boolean changedState = heater.syncLit();
        if (before != heater.fuel.burnTicksLeft()
                && (changedState || world.getTime() % 20 == 0)) {
            heater.markDirty();
            heater.syncStatus();
        } else if (changedState) {
            heater.markDirty();
            heater.syncStatus();
        }
        if (heater.fuel.isLit(config)) {
            heater.tickCrackle(world, pos);
            heater.tickSmoke(world, pos);
        } else {
            heater.crackleCooldown = 0;
        }
    }

    private void tickCrackle(World world, BlockPos pos) {
        if (crackleCooldown <= 0) {
            crackleCooldown = 80 + world.random.nextInt(101);
        } else if (--crackleCooldown == 0) {
            world.playSound(null, pos, SoundEvents.BLOCK_CAMPFIRE_CRACKLE,
                    SoundCategory.BLOCKS, 0.35F, 0.9F + world.random.nextFloat() * 0.2F);
            crackleCooldown = 80 + world.random.nextInt(101);
        }
    }

    private void tickSmoke(World world, BlockPos pos) {
        if (world instanceof ServerWorld serverWorld && world.random.nextInt(70) == 0) {
            serverWorld.spawnParticles(ParticleTypes.SMOKE,
                    pos.getX() + 0.5, pos.getY() + 1.03, pos.getZ() + 0.5,
                    1, 0.08, 0.01, 0.08, 0.005);
        }
    }

    /** Homeostatic 1.20.1 gates radiation using the vanilla LIT property. */
    private boolean syncLit() {
        if (world == null) return false;
        BlockState state = world.getBlockState(pos);
        if (!(state.getBlock() instanceof CoalHeaterBlock)) return false;
        boolean lit = fuel.isLit(HomeostaticHeater.config());
        if (state.get(CoalHeaterBlock.LIT) == lit) return false;
        world.setBlockState(pos, state.with(CoalHeaterBlock.LIT, lit), Block.NOTIFY_ALL);
        return true;
    }

    private void syncStatus() {
        if (world instanceof ServerWorld serverWorld) serverWorld.getChunkManager().markForUpdate(pos);
    }

    public FuelDuration getRemainingForDisplay() {
        return new FuelDuration(displayMinutes, displaySeconds);
    }
    public boolean isActiveForDisplay() { return displayActive; }
    public boolean isAlwaysActiveForDisplay() { return displayAlwaysActive; }
    public boolean isEnabledForDisplay() { return displayEnabled; }

    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this, BlockEntity::toInitialChunkDataNbt);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        HeaterDisplayStatus status = HeaterDisplayStatus.fromFuel(fuel, HomeostaticHeater.config());
        NbtCompound nbt = new NbtCompound();
        nbt.putBoolean("displayActive", status.active());
        nbt.putBoolean("displayAlwaysActive", status.alwaysActive());
        nbt.putBoolean("displayEnabled", status.enabled());
        nbt.putLong("displayMinutes", status.remaining().minutes());
        nbt.putInt("displaySeconds", status.remaining().seconds());
        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        int burning = 0;
        int queued = 0;
        boolean isLava = false;
        boolean lavaWaiting = false;
        if (nbt.contains("burnTicksLeft")) {
            burning = Math.max(0, nbt.getInt("burnTicksLeft"));
            queued = Math.max(0, nbt.getInt("queuedCoal"));
            isLava = nbt.getBoolean("burningLava");
            lavaWaiting = nbt.getBoolean("queuedLava");
        } else if (nbt.contains("remainingTicks")) {
            int oldTicks = Math.max(0, Math.min(OLD_TICKS_PER_COAL * 16, nbt.getInt("remainingTicks")));
            if (oldTicks > 0) {
                queued = (oldTicks - 1) / OLD_TICKS_PER_COAL;
                burning = oldTicks - queued * OLD_TICKS_PER_COAL;
            }
        }
        // Older versions did not have an 'enabled' tag: all existing heaters stay on.
        fuel.restore(burning, queued, isLava, lavaWaiting,
                !nbt.contains("enabled") || nbt.getBoolean("enabled"));
        displayMinutes = Math.max(0L, nbt.getLong("displayMinutes"));
        displaySeconds = Math.max(0, Math.min(59, nbt.getInt("displaySeconds")));
        displayActive = nbt.getBoolean("displayActive");
        displayAlwaysActive = nbt.getBoolean("displayAlwaysActive");
        displayEnabled = !nbt.contains("displayEnabled") || nbt.getBoolean("displayEnabled");
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putInt("burnTicksLeft", fuel.burnTicksLeft());
        nbt.putInt("queuedCoal", fuel.queuedCoal());
        nbt.putBoolean("burningLava", fuel.burningLava());
        nbt.putBoolean("queuedLava", fuel.queuedLava());
        nbt.putBoolean("enabled", fuel.enabled());
    }
}
