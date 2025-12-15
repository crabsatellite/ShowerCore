/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.storage;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import tanukidecor.TDRegistry;
import tanukidecor.block.RotatingWideBlock;
import tanukidecor.block.entity.StorageBlockEntity;
import tanukidecor.util.ShapeBuilder;

import java.util.function.Supplier;

public class WideStorageBlock extends RotatingWideBlock implements EntityBlock {

    private final Supplier<BlockEntityType<StorageBlockEntity>> blockEntitySupplier;
    private final SoundEvent openSound;

    public WideStorageBlock(final Supplier<BlockEntityType<StorageBlockEntity>> blockEntity, final ShapeBuilder shapeBuilder, Properties pProperties) {
        this(blockEntity, shapeBuilder, SoundEvents.BARREL_OPEN, pProperties);
    }

    public WideStorageBlock(final Supplier<BlockEntityType<StorageBlockEntity>> blockEntity, final ShapeBuilder shapeBuilder,
                            final SoundEvent openSound, Properties pProperties) {
        super(pProperties, shapeBuilder);
        this.blockEntitySupplier = blockEntity;
        this.openSound = openSound;
    }

    /// / CONTAINER ////

    @Override
    protected InteractionResult useWithoutItem(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, BlockHitResult pHitResult) {
        return StorageBlockEntity.useWithoutItem(pState, pLevel, pPos, pPlayer, pHitResult, this.openSound);
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
            return this.blockEntitySupplier.get().create(pPos, pState);
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
