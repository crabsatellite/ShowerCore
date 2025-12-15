/*
 * Copyright (c) 2024 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
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
import tanukidecor.block.TallBlock;
import tanukidecor.block.entity.PlasmaBallBlockEntity;

public class PlasmaBallBlock extends TallBlock implements EntityBlock {

    public static final BooleanProperty ENABLED = BlockStateProperties.ENABLED;

    public static final VoxelShape SHAPE_UPPER = Shapes.block();
    public static final VoxelShape SHAPE_LOWER = Shapes.or(
            box(0, 0, 0, 16, 2, 16),
            box(2, 2, 4, 14, 10, 12),
            box(4, 2, 2, 12, 10, 14),
            box(4, 10, 4, 12, 16, 12));

    public PlasmaBallBlock(Properties pProperties) {
        super(SHAPE_UPPER, SHAPE_LOWER, pProperties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(WATERLOGGED, false)
                .setValue(HALF, DoubleBlockHalf.LOWER)
                .setValue(ENABLED, true));
        precalculateShapes();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder.add(ENABLED));
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, BlockHitResult pHitResult) {
        if (pPlayer.isShiftKeyDown()) {
            return super.useWithoutItem(pState, pLevel, pPos, pPlayer, pHitResult);
        }
        if (!pLevel.isClientSide()) {
            // update state
            BlockPos delegatePos = getDelegatePos(pState, pPos);
            BlockState delegateState = pLevel.getBlockState(delegatePos);
            pLevel.setBlock(delegatePos, delegateState.setValue(ENABLED, !delegateState.getValue(ENABLED)), Block.UPDATE_ALL);
            // play sound
            pLevel.playSound(pPlayer, pPos, SoundEvents.GLASS_HIT, SoundSource.BLOCKS, 1.0F, 0.95F + pPlayer.getRandom().nextFloat() * 0.1F);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRandom) {

    }

    /// / BLOCK ENTITY ////

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        if (getDelegatePos(pState, pPos).equals(pPos)) {
            return TDRegistry.BlockEntityReg.PLASMA_BALL.get().create(pPos, pState);
        }
        return null;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return pLevel.isClientSide() ? (BlockEntityTicker<T>) (BlockEntityTicker<PlasmaBallBlockEntity>) (PlasmaBallBlockEntity::tick) : null;
    }
}
