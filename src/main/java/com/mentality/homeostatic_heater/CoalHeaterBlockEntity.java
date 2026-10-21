package com.mentality.homeostatic_heater;

import net.minecraft.block.BlockState;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/** Persists only remaining burn time, so adding/removing the mod never rewrites unrelated world data. */
public final class CoalHeaterBlockEntity extends BlockEntity {
    private static final int TICKS_PER_COAL = 1600; // 80 seconds, same burn time as vanilla coal.
    private static final int MAX_FUEL_TICKS = TICKS_PER_COAL * 16;
    private int remainingTicks;

    public CoalHeaterBlockEntity(BlockPos pos, BlockState state) {
        super(HomeostaticHeater.entityTypeFor(state), pos, state);
    }

    public boolean addFuel() {
        if (remainingTicks > MAX_FUEL_TICKS - TICKS_PER_COAL) return false;
        remainingTicks += TICKS_PER_COAL;
        updateLit(true);
        markDirty();
        return true;
    }

    public static void tick(World world, BlockPos pos, BlockState state, CoalHeaterBlockEntity heater) {
        if (heater.remainingTicks <= 0) return;
        heater.remainingTicks--;
        if (heater.remainingTicks == 0) heater.updateLit(false);
        if (heater.remainingTicks % 20 == 0) heater.markDirty();
    }

    private void updateLit(boolean lit) {
        if (world != null && getCachedState().get(CoalHeaterBlock.LIT) != lit) {
            world.setBlockState(pos, getCachedState().with(CoalHeaterBlock.LIT, lit), Block.NOTIFY_ALL);
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        remainingTicks = Math.max(0, Math.min(MAX_FUEL_TICKS, nbt.getInt("remainingTicks")));
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putInt("remainingTicks", remainingTicks);
    }
}
