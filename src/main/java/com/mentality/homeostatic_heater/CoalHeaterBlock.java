package com.mentality.homeostatic_heater;

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.text.Text;
import net.minecraft.world.World;

/** Right-click coal, charcoal, or one lava bucket to refuel; empty-hand click toggles power. */
public final class CoalHeaterBlock extends BlockWithEntity implements BlockEntityProvider {
    public static final BooleanProperty LIT = Properties.LIT;
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;

    public CoalHeaterBlock(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState().with(LIT, false).with(FACING, Direction.NORTH));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(LIT, FACING);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        return getDefaultState().with(FACING, context.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new CoalHeaterBlockEntity(pos, state);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand,
                              BlockHitResult hit) {
        ItemStack held = player.getStackInHand(hand);
        boolean isCoal = held.isOf(Items.COAL) || held.isOf(Items.CHARCOAL);
        boolean isLava = held.isOf(Items.LAVA_BUCKET);
        boolean toggle = hand == Hand.MAIN_HAND && held.isEmpty();
        if (!isCoal && !isLava && !toggle) return ActionResult.PASS;

        if (!(world.getBlockEntity(pos) instanceof CoalHeaterBlockEntity heater)) return ActionResult.PASS;
        if (!world.isClient) {
            if (toggle) {
                heater.toggleEnabled();
                world.playSound(null, pos, SoundEvents.BLOCK_LEVER_CLICK, SoundCategory.BLOCKS, 0.6F, 1F);
            } else {
                boolean inserted = isLava ? heater.addLava() : heater.addCoal();
                if (inserted) {
                    if (!player.getAbilities().creativeMode) {
                        if (isLava) {
                            // Lava buckets are non-stackable. Return exactly one empty bucket.
                            player.setStackInHand(hand, new ItemStack(Items.BUCKET));
                        } else {
                            held.decrement(1);
                        }
                    }
                    world.playSound(null, pos, SoundEvents.ITEM_FLINTANDSTEEL_USE,
                            SoundCategory.BLOCKS, 0.8F, 0.9F + world.random.nextFloat() * 0.2F);
                } else {
                    player.sendMessage(Text.translatable(isLava
                            ? "message.homeostatic_heater.lava_full"
                            : "message.homeostatic_heater.full"), true);
                }
            }
        }
        return ActionResult.success(world.isClient);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state,
            BlockEntityType<T> type) {
        return world.isClient ? null : checkType(type, HomeostaticHeater.entityTypeFor(state), CoalHeaterBlockEntity::tick);
    }
}
