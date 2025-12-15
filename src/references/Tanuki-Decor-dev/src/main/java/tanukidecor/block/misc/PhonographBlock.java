/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.JukeboxPlayable;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import tanukidecor.TDRegistry;
import tanukidecor.block.RotatingTallBlock;
import tanukidecor.block.entity.PhonographBlockEntity;

public class PhonographBlock extends RotatingTallBlock implements EntityBlock {

    public static final BooleanProperty HAS_RECORD = BlockStateProperties.HAS_RECORD;

    public static final VoxelShape SHAPE_UPPER = Shapes.or(
            box(7, -2, 13, 9, 0, 15),
            box(7, -1, 10, 9, 1, 13),
            box(5, -1, 3, 11, 4, 10));
    public static final VoxelShape SHAPE_LOWER = Shapes.or(
            box(1, 0, 1, 15, 2, 15),
            box(2, 2, 2, 14, 6, 14),
            box(1, 6, 1, 15, 7, 15),
            box(7, 7, 13, 9, 11, 15),
            box(6.5D, 11, 12.5D, 9.5D, 13, 15.5D),
            box(7, 13, 13, 9, 14, 15));


    public PhonographBlock(Properties pProperties) {
        super(pProperties, RotatingTallBlock.createShapeBuilder(SHAPE_UPPER, SHAPE_LOWER));
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(HALF, DoubleBlockHalf.LOWER)
                .setValue(FACING, Direction.NORTH)
                .setValue(HAS_RECORD, false)
                .setValue(WATERLOGGED, false));
    }

    /// / METHODS ////

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder.add(HAS_RECORD));
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, BlockHitResult pHitResult) {
        BlockPos pos = getDelegatePos(pState, pPos);
        return PhonographBlockEntity.use(pState, pLevel, pos, pPlayer, InteractionHand.MAIN_HAND, pHitResult);
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (!pState.is(pNewState.getBlock())) {
            PhonographBlockEntity.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
            super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
        }
    }

    /// / DELEGATE PROVIDER ////

    @Override
    public BlockPos getDelegatePos(BlockState blockState, BlockPos blockPos) {
        return blockState.getValue(HALF) == DoubleBlockHalf.LOWER ? blockPos : blockPos.below();
    }

    /// / BLOCK ENTITY ////

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        if (pPos.equals(getDelegatePos(pState, pPos))) {
            return TDRegistry.BlockEntityReg.PHONOGRAPH.get().create(pPos, pState);
        }
        return TDRegistry.BlockEntityReg.STORAGE_DELEGATE.get().create(pPos, pState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        if (pBlockEntityType == TDRegistry.BlockEntityReg.PHONOGRAPH.get() && pState.getValue(HAS_RECORD)) {
            return (BlockEntityTicker<T>) (BlockEntityTicker<PhonographBlockEntity>) (PhonographBlockEntity::tick);
        }
        return null;
    }

    /// / REDSTONE ////

    @Override
    public boolean isSignalSource(BlockState pState) {
        return true;
    }

    @Override
    public int getSignal(BlockState pBlockState, BlockGetter pBlockAccess, BlockPos pPos, Direction pSide) {
        if (pBlockAccess.getBlockEntity(getDelegatePos(pBlockState, pPos)) instanceof PhonographBlockEntity blockEntity) {
            if (blockEntity.isRecordPlaying()) {
                return 15;
            }
        }
        return 0;
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        if (level.getBlockEntity(getDelegatePos(state, pos)) instanceof PhonographBlockEntity blockEntity) {
            ItemStack stack = blockEntity.getFirstItem();
            JukeboxPlayable playable = stack.get(DataComponents.JUKEBOX_PLAYABLE);
            if (playable != null) {
                // Return signal strength based on whether a record is present
                return 15;
            }
        }
        return 0;
    }
}
