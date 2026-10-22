package com.mentality.homeostatic_heater;

import net.minecraft.block.BlockState;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
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
    private int displayTicksPerCoal = 1600;
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
            if (heater.burnTicksLeft > 0) heater.tickCrackle(world, pos);
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

    private void updateLit(boolean lit) {
        if (world != null && getCachedState().get(CoalHeaterBlock.LIT) != lit) {
            world.setBlockState(pos, getCachedState().with(CoalHeaterBlock.LIT, lit), Block.NOTIFY_ALL);
            syncStatus();
        }
    }

    private void syncStatus() {
        if (world instanceof ServerWorld serverWorld) serverWorld.getChunkManager().markForUpdate(pos);
    }

    public long getRemainingTicksForDisplay() {
        return FuelDuration.totalTicks(burnTicksLeft, queuedCoal, displayTicksPerCoal);
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
        NbtCompound nbt = createNbt();
        nbt.putInt("displayTicksPerCoal", HomeostaticHeater.config().burnTicksPerCoal());
        nbt.putBoolean("displayAlwaysActive", HomeostaticHeater.config().alwaysActive());
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
        displayTicksPerCoal = Math.max(1, nbt.contains("displayTicksPerCoal")
                ? nbt.getInt("displayTicksPerCoal") : 1600);
        displayAlwaysActive = nbt.getBoolean("displayAlwaysActive");
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putInt("burnTicksLeft", burnTicksLeft);
        nbt.putInt("queuedCoal", queuedCoal);
    }
}
