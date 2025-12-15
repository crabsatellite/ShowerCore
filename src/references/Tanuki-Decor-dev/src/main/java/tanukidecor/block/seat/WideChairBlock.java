/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.seat;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import tanukidecor.block.RotatingWideBlock;
import tanukidecor.util.ShapeBuilder;

public class WideChairBlock extends RotatingWideBlock implements ISeatProvider {

    private final double seatYOffset;

    /**
     * @param shapeBuilder the shape builder function
     * @param seatYOffset  the y offset of the seat in block units, generally 2 pixels above the seat part of the model
     * @param pProperties  the block properties
     */
    public WideChairBlock(final ShapeBuilder shapeBuilder, final double seatYOffset, Properties pProperties) {
        super(pProperties.randomTicks(), shapeBuilder);
        this.seatYOffset = seatYOffset;
    }

    /// / SEAT PROVIDER ////

    @Override
    public double getSeatYOffset(BlockState blockState, Level level, BlockPos blockPos) {
        return this.seatYOffset;
    }

    @Override
    public Direction getSeatDirection(BlockState blockState, Level level, BlockPos blockPos) {
        return blockState.getValue(FACING);
    }

    /// / METHODS ////

    @Override
    public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        despawnSeat(pState, pLevel, pPos, false);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, BlockHitResult pHitResult) {
        if (pLevel.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        if (!pPlayer.isShiftKeyDown() && startSitting(pLevel.getBlockState(pPos), pLevel, pPos, pPlayer)) {
            return InteractionResult.SUCCESS;
        }
        return super.useWithoutItem(pState, pLevel, pPos, pPlayer, pHitResult);
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        despawnSeat(pState, pLevel, pPos, true);
        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
    }
}
