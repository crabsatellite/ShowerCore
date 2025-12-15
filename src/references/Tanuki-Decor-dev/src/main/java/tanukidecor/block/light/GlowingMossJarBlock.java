/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.light;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import tanukidecor.block.RotatingBlock;

public class GlowingMossJarBlock extends RotatingBlock {

    public static final VoxelShape SHAPE = Shapes.or(
            box(4, 0, 4, 12, 10, 12),
            box(5, 10, 5, 11, 13, 11));

    public GlowingMossJarBlock(Properties pProperties) {
        super(pProperties, RotatingBlock.createShapeBuilder(SHAPE));
    }

    @Override
    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRandom) {
        //if(pRandom.nextInt(4) == 0) {
        final Vec3 pos = Vec3.atCenterOf(pPos)
                .add(0.3125D * (pRandom.nextDouble() - 0.5D) * 0.5D,
                        0.625D * (pRandom.nextDouble() - 0.5D) * 0.5D,
                        0.3125D * (pRandom.nextDouble() - 0.5D) * 0.5D);
        pLevel.addParticle(ParticleTypes.FALLING_SPORE_BLOSSOM, pos.x(), pos.y(), pos.z(), 0, 0, 0);
        //}
    }
}
