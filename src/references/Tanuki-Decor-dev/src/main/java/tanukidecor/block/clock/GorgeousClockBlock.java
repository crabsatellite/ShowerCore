/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.clock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.VoxelShape;
import tanukidecor.TDRegistry;

public class GorgeousClockBlock extends TallClockBlock {

    public static final VoxelShape UPPER_SHAPE = box(2, 0, 9, 14, 12, 16);
    public static final VoxelShape LOWER_SHAPE = box(4, 4, 11, 12, 16, 16);

    public GorgeousClockBlock(Properties pProperties) {
        super(TDRegistry.SoundReg.MEDIUM_CLOCK_TICK, TDRegistry.SoundReg.MEDIUM_CLOCK_CHIME,
                UPPER_SHAPE, LOWER_SHAPE, TDRegistry.BlockEntityReg.GORGEOUS_CLOCK, pProperties);
    }

    /// / PLACEMENT ////

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        BlockPos blockpos = pContext.getClickedPos();
        Level level = pContext.getLevel();
        FluidState fluidstate = pContext.getLevel().getFluidState(pContext.getClickedPos());
        boolean waterlogged = fluidstate.getType() == Fluids.WATER;
        if (pContext.getClickedFace().getAxis() != Direction.Axis.Y
                && blockpos.getY() > level.getMinBuildHeight()
                && level.getBlockState(blockpos.below()).canBeReplaced(pContext)) {
            return this.defaultBlockState()
                    .setValue(FACING, pContext.getClickedFace())
                    .setValue(WATERLOGGED, waterlogged)
                    .setValue(HALF, DoubleBlockHalf.UPPER);
        } else {
            return null;
        }
    }

    @Override
    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, LivingEntity pPlacer, ItemStack pStack) {
        boolean waterlogged = pLevel.getFluidState(pPos.below()).getType() == Fluids.WATER;
        pLevel.setBlock(pPos.below(), pState.setValue(HALF, DoubleBlockHalf.LOWER).setValue(WATERLOGGED, waterlogged), Block.UPDATE_ALL);
    }

    @Override
    public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        final Direction facing = pState.getValue(FACING);
        final BlockPos supportingPos = pPos.relative(facing.getOpposite());
        return (pState.getValue(HALF) == DoubleBlockHalf.UPPER && pLevel.getBlockState(supportingPos).isFaceSturdy(pLevel, supportingPos, facing))
                || pLevel.getBlockState(pPos.above()).is(this);
    }
}
