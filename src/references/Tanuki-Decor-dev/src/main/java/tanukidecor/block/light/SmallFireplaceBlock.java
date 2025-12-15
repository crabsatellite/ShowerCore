/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.light;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import tanukidecor.block.RotatingWideBlock;
import tanukidecor.block.Side;

public class SmallFireplaceBlock extends RotatingWideBlock {

    public static final VoxelShape SHAPE_EAST = Shapes.or(
            box(0, 2, 2, 14, 14, 16),
            box(0, 0, 0, 16, 2, 16),
            box(0, 14, 0, 16, 16, 16));
    public static final VoxelShape SHAPE_WEST = Shapes.or(
            box(2, 2, 2, 16, 14, 16),
            box(0, 0, 0, 16, 2, 16),
            box(0, 14, 0, 16, 16, 16));

    public SmallFireplaceBlock(Properties pProperties) {
        super(pProperties, createShapeBuilder(SHAPE_EAST, SHAPE_WEST));
    }

    /// / ANIMATE ////

    @Override
    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRandom) {
        if (pState.getValue(WATERLOGGED) || pState.getValue(SIDE) != Side.LEFT) {
            return;
        }
        // play sound
        if (pRandom.nextInt(10) == 0) {
            pLevel.playLocalSound(pPos.getX() + 0.5D, pPos.getY() + 0.5D, pPos.getZ() + 0.5D, SoundEvents.CAMPFIRE_CRACKLE, SoundSource.BLOCKS, 0.5F + pRandom.nextFloat(), pRandom.nextFloat() * 0.7F + 0.6F, false);
        }
        // particles
        final Direction direction = pState.getValue(FACING);
        final double dx = (5.0D + pRandom.nextInt(7)) / 16.0D;
        final double dy = 4.5D / 16.0D;
        final double dz = 8.0D / 16.0D;
        final Vec3 pos = Vec3.atBottomCenterOf(pPos)
                .add(dz * direction.getStepX(), dy, dz * direction.getStepZ())
                .add(dx * direction.getStepZ(), 0, -dx * direction.getStepX());
        // smoke particle
        pLevel.addParticle(ParticleTypes.SMOKE, pos.x(), pos.y(), pos.z(), 0.0D, 0.0D, 0.0D);
        // fire particle
        if (pRandom.nextInt(5) == 0) {
            pLevel.addParticle(ParticleTypes.LAVA, pos.x(), pos.y(), pos.z(), 0.0D, 0.0D, 0.0D);
        }
    }
}
