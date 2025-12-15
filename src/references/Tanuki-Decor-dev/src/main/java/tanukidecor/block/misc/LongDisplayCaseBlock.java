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
import org.joml.Vector3f;
import tanukidecor.TDRegistry;
import tanukidecor.block.RotatingMultiblock;
import tanukidecor.block.entity.SingleSlotBlockEntity;
import tanukidecor.util.MultiblockHandler;

public class LongDisplayCaseBlock extends RotatingMultiblock implements EntityBlock, IDisplayProvider {

    public LongDisplayCaseBlock(Properties pProperties) {
        super(MultiblockHandler.MULTIBLOCK_2X2X1, createMultiblockShapeBuilder(MultiblockHandler.MULTIBLOCK_2X2X1, SHAPE), pProperties);
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

    /// / DISPLAY PROVIDER ////

    @Override
    public Vector3f getDisplayRotation(Level level, BlockState blockState, BlockPos blockPos, ItemStack itemStack, int renderPass, float partialTick) {
        return new Vector3f(0, blockState.getValue(FACING).getOpposite().toYRot(), 0);
    }

    /// / DELEGATE PROVIDER ////

    @Override
    public BlockPos getDelegatePos(BlockState blockState, BlockPos blockPos) {
        if (getMultiblockHandler().getIndex(blockState).getY() == getMultiblockHandler().getMaxIndex().getY()) {
            return blockPos;
        }
        return blockPos.above();
    }

    /// / BLOCK ENTITY ////

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        if (pPos.equals(getDelegatePos(pState, pPos))) {
            return TDRegistry.BlockEntityReg.DISPLAY_CASE.get().create(pPos, pState);
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

    //// SHAPE ////

    /**
     * Shape data for each block in the default horizontal direction, ordered by index {@code [height][width][depth]}
     **/
    public static final VoxelShape[][][] SHAPE = new VoxelShape[][][]{
            // height = 0
            {},
            // height = 1
            {
                    // width = 0
                    {},
                    // width = 1
                    {
                            Shapes.or(box(0, 6, 0, 16, 8, 16),
                                    box(0, 8, 1, 15, 14, 15),
                                    box(0, 14, 0, 16, 16, 16),
                                    box(13, 0, 13, 15, 6, 15),
                                    box(13, 0, 1, 15, 6, 3))
                    },
                    // width = 2
                    {
                            Shapes.or(box(0, 6, 0, 16, 8, 16),
                                    box(1, 8, 1, 16, 14, 15),
                                    box(0, 14, 0, 16, 16, 16),
                                    box(1, 0, 1, 3, 6, 3),
                                    box(1, 0, 13, 3, 6, 15))
                    }
            },
            // height = 2
            {
                    // width = 0
                    {},
                    // width = 1
                    {
                            box(0, 0, 0.01D, 15.99D, 15.99D, 15.99D)
                    },
                    // width = 2
                    {
                            box(0.01D, 0, 0.01D, 16, 15.99D, 15.99D)
                    }
            }
    };
}
