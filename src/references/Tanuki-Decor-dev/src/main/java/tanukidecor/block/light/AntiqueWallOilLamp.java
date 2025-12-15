/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.light;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import tanukidecor.block.RotatingBlock;

public class AntiqueWallOilLamp extends RotatingBlock {

    public static final VoxelShape SHAPE = Shapes.or(
            box(6, 1, 15, 10, 9, 16),
            box(7.99D, 0, 11, 8.01D, 7, 15),
            box(7, 0, 9, 9, 2, 11),
            box(6, 2, 8, 10, 4, 12),
            box(6.5D, 4, 8.5D, 9.5D, 5, 11.5D),
            box(6, 5, 8, 10, 8, 12),
            Shapes.join(
                    box(4, 8, 6, 12, 14, 14),
                    box(6, 8, 8, 10, 14, 12),
                    BooleanOp.ONLY_FIRST));

    public AntiqueWallOilLamp(Properties pProperties) {
        super(pProperties, RotatingBlock.createShapeBuilder(SHAPE));
    }

    /// / PLACEMENT ////

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return getStateForWallPlacement(pContext);
    }

    @Override
    public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        return canSurviveOnWall(pState, pLevel, pPos);
    }

    /// / ANIMATION ////

    @Override
    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRandom) {
        if (pState.getValue(WATERLOGGED)) {
            return;
        }
        final Direction direction = pState.getValue(FACING);
        final Vec3 offset = new Vec3(-2.0D / 16.0D * direction.getStepX(), 11 / 16.0D, -2.0D / 16.0D * direction.getStepZ());
        final Vec3 pos = Vec3.atBottomCenterOf(pPos).add(offset);
        pLevel.addParticle(ParticleTypes.SMOKE, pos.x(), pos.y(), pos.z(), 0.0D, 0.0D, 0.0D);
    }
}
