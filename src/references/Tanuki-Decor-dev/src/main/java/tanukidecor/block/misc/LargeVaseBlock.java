/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import tanukidecor.TDRegistry;
import tanukidecor.block.TallBlock;
import tanukidecor.block.entity.SingleSlotBlockEntity;

public class LargeVaseBlock extends TallBlock implements EntityBlock {

    public static final VoxelShape LARGE_FANCY_SHAPE_LOWER = Shapes.or(
            box(4, 0, 4, 12, 2, 12),
            box(2, 2, 2, 14, 6, 14),
            box(0, 6, 0, 16, 16, 16));

    public static final VoxelShape LARGE_FANCY_SHAPE_UPPER = Shapes.or(
            box(2, 1, 2, 14, 3, 14),
            box(0, 0, 0, 16, 1, 16),
            box(5, 3, 5, 11, 6, 11),
            box(4, 6, 4, 12, 8, 12));


    public static final VoxelShape LARGE_STRIPED_SHAPE_LOWER = Shapes.or(
            box(4, 0, 4, 12, 5, 12),
            box(3, 5, 3, 13, 10, 13),
            box(2, 10, 2, 14, 12, 14),
            box(0, 12, 0, 16, 16, 16));

    public static final VoxelShape LARGE_STRIPED_SHAPE_UPPER = Shapes.or(
            box(2, 1, 2, 14, 3, 14),
            box(0, 0, 0, 16, 1, 16),
            box(5, 3, 5, 11, 6, 11),
            box(4, 6, 4, 12, 8, 12));


    public LargeVaseBlock(final VoxelShape upperShape, final VoxelShape lowerShape, Properties pProperties) {
        super(upperShape, lowerShape, pProperties);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack pStack, BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHitResult) {
        BlockPos pos = getDelegatePos(pState, pPos);
        return SingleSlotBlockEntity.useItemOn(pStack, pState, pLevel, pos, pPlayer, pHand, pHitResult);
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (!pState.is(pNewState.getBlock())) {
            SingleSlotBlockEntity.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
            super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
        }
    }

    /// / BLOCK ENTITY ////

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        if (pPos.equals(getDelegatePos(pState, pPos))) {
            return TDRegistry.BlockEntityReg.VASE.get().create(pPos, pState);
        }
        return TDRegistry.BlockEntityReg.STORAGE_DELEGATE.get().create(pPos, pState);
    }

    /// / REDSTONE ////

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(level.getBlockEntity(pos));
    }
}
