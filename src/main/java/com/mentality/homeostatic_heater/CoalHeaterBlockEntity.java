package com.mentality.homeostatic_heater;

import net.minecraft.block.BlockState;
import net.minecraft.block.Block;
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

/** Persists fuel only in this block entity, including a migration from the old remainingTicks tag. */
public final class CoalHeaterBlockEntity extends BlockEntity {
    private static final int OLD_TICKS_PER_COAL = 1600;
    private int burnTicksLeft;
    private int queuedCoal;
    private int crackleCooldown;
    private long displayMinutes;
    private int displaySeconds;
    private boolean displayActive;
    private boolean displayAlwaysActive;

    public CoalHeaterBlockEntity(BlockPos pos, BlockState state) {
        super(HomeostaticHeater.entityTypeFor(state), pos, state);
    }

    public boolean addFuel() {
        HeaterConfig config = HomeostaticHeater.config();
        int loadedCoal = queuedCoal + (burnTicksLeft > 0 ? 1 : 0);
        if (loadedCoal >= config.maxCoal()) return false;
        if (burnTicksLeft == 0) {
            burnTicksLeft = config.burnTicksPerCoal();
        } else {
            queuedCoal++;
        }
        updateLit(true);
        markDirty();
        syncStatus();
        return true;
    }

    public static void tick(World world, BlockPos pos, BlockState state, CoalHeaterBlockEntity heater) {
        HeaterConfig config = HomeostaticHeater.config();
        if (config.alwaysActive()) {
            heater.updateLit(true);
            heater.tickCrackle(world, pos);
            heater.tickSmoke(world, pos);
            return;
        }
        if (heater.burnTicksLeft > 0) {
            heater.burnTicksLeft--;
            if (heater.burnTicksLeft == 0 && heater.queuedCoal > 0) {
                heater.queuedCoal--;
                heater.burnTicksLeft = config.burnTicksPerCoal();
            }
            heater.updateLit(heater.burnTicksLeft > 0);
            if (heater.burnTicksLeft == 0 || world.getTime() % 20 == 0) {
                heater.markDirty();
                heater.syncStatus();
            }
            if (heater.burnTicksLeft > 0) {
                heater.tickCrackle(world, pos);
                heater.tickSmoke(world, pos);
            }
        } else {
            heater.updateLit(false);
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

    private void updateLit(boolean lit) {
        if (world != null && getCachedState().get(CoalHeaterBlock.LIT) != lit) {
            world.setBlockState(pos, getCachedState().with(CoalHeaterBlock.LIT, lit), Block.NOTIFY_ALL);
            syncStatus();
        }
    }

    private void syncStatus() {
        if (world instanceof ServerWorld serverWorld) serverWorld.getChunkManager().markForUpdate(pos);
    }

    public FuelDuration getRemainingForDisplay() {
        return new FuelDuration(displayMinutes, displaySeconds);
    }

    public boolean isActiveForDisplay() {
        return displayActive;
    }

    public boolean isAlwaysActiveForDisplay() {
        return displayAlwaysActive;
    }

    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this, BlockEntity::toInitialChunkDataNbt);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        HeaterConfig config = HomeostaticHeater.config();
        HeaterDisplayStatus status = HeaterDisplayStatus.fromFuel(
                burnTicksLeft, queuedCoal, config.burnTicksPerCoal(), config.alwaysActive());
        NbtCompound nbt = new NbtCompound();
        nbt.putBoolean("displayActive", status.active());
        nbt.putBoolean("displayAlwaysActive", status.alwaysActive());
        nbt.putLong("displayMinutes", status.remaining().minutes());
        nbt.putInt("displaySeconds", status.remaining().seconds());
        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        burnTicksLeft = 0;
        queuedCoal = 0;
        if (nbt.contains("burnTicksLeft")) {
            burnTicksLeft = Math.max(0, nbt.getInt("burnTicksLeft"));
            queuedCoal = Math.max(0, nbt.getInt("queuedCoal"));
        } else {
            // 1.2.0 and earlier stored the entire buffer as remainingTicks.
            int oldTicks = Math.max(0, Math.min(OLD_TICKS_PER_COAL * 16, nbt.getInt("remainingTicks")));
            if (oldTicks > 0) {
                queuedCoal = (oldTicks - 1) / OLD_TICKS_PER_COAL;
                burnTicksLeft = oldTicks - queuedCoal * OLD_TICKS_PER_COAL;
            }
        }
        displayMinutes = Math.max(0, nbt.getLong("displayMinutes"));
        displaySeconds = Math.max(0, Math.min(59, nbt.getInt("displaySeconds")));
        displayActive = nbt.getBoolean("displayActive");
        displayAlwaysActive = nbt.getBoolean("displayAlwaysActive");
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putInt("burnTicksLeft", burnTicksLeft);
        nbt.putInt("queuedCoal", queuedCoal);
    }
}
