/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.storage;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import tanukidecor.TDRegistry;
import tanukidecor.block.RotatingMultiblock;
import tanukidecor.block.entity.StorageBlockEntity;
import tanukidecor.util.MultiblockHandler;

public class AntiqueMirrorBlock extends RotatingMultiblock implements EntityBlock {

    public AntiqueMirrorBlock(Properties pProperties) {
        super(MultiblockHandler.MULTIBLOCK_2X2X1, RotatingMultiblock.createMultiblockShapeBuilder(MultiblockHandler.MULTIBLOCK_2X2X1, SHAPE), pProperties);
    }

    /// / CONTAINER ////
    @Override
    protected InteractionResult useWithoutItem(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, BlockHitResult pHitResult) {
        return StorageBlockEntity.useWithoutItem(pState, pLevel, pPos, pPlayer, pHitResult, SoundEvents.BARREL_OPEN);
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (!pState.is(pNewState.getBlock())) {
            StorageBlockEntity.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
            super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
        }
    }

    /// / BLOCK ENTITY ////

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        if (pPos.equals(getDelegatePos(pState, pPos))) {
            return TDRegistry.BlockEntityReg.ANTIQUE_MIRROR.get().create(pPos, pState);
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
                            AntiqueDeskBlock.SHAPE_EAST
                    },
                    // width = 2
                    {
                            AntiqueDeskBlock.SHAPE_WEST
                    }
            },
            // height = 2
            {
                    // width = 0
                    {},
                    // width = 1
                    {
                            Shapes.or(box(6, 0, 7, 14, 5, 15),
                                    box(10, 5, 14, 14, 7, 15),
                                    box(6, 5, 14, 10, 11, 15),
                                    box(5, 9, 14, 6, 10, 15),
                                    box(0, 0, 13, 6, 2, 15),
                                    box(0, 3, 14, 5, 16, 15))
                    },
                    // width = 2
                    {
                            Shapes.or(box(2, 0, 7, 10, 5, 15),
                                    box(2, 5, 14, 6, 7, 15),
                                    box(6, 5, 14, 10, 11, 15),
                                    box(10, 9, 14, 11, 10, 15),
                                    box(10, 0, 13, 16, 2, 15),
                                    box(11, 3, 14, 16, 16, 15))
                    }
            }
    };
}
