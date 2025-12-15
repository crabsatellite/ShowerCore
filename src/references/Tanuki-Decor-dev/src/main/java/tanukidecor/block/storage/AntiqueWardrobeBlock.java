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

public class AntiqueWardrobeBlock extends RotatingMultiblock implements EntityBlock {

    public AntiqueWardrobeBlock(Properties pProperties) {
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
            return TDRegistry.BlockEntityReg.ANTIQUE_WARDROBE.get().create(pPos, pState);
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
                            Shapes.or(box(0, 2, 1, 15, 16, 15),
                                    box(0, 0, 0, 16, 2, 16),
                                    box(0, 9, 0, 16, 10, 16))
                    },
                    // width = 2
                    {
                            Shapes.or(box(0, 0, 0, 16, 2, 16),
                                    box(1, 2, 1, 16, 16, 15),
                                    box(0, 9, 0, 16, 10, 16))
                    }
            },
            // height = 2
            {
                    // width = 0
                    {},
                    // width = 1
                    {
                            Shapes.or(box(0, 0, 1, 15, 14, 15),
                                    box(0, 14, 0, 16, 16, 16))
                    },
                    // width = 2
                    {
                            Shapes.or(box(1, 0, 1, 16, 14, 15),
                                    box(0, 14, 0, 16, 16, 16))
                    }
            }
    };
}
