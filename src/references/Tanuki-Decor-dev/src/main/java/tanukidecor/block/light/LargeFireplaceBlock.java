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
import tanukidecor.block.RotatingMultiblock;
import tanukidecor.util.MultiblockHandler;

public class LargeFireplaceBlock extends RotatingMultiblock {

    public LargeFireplaceBlock(Properties pProperties) {
        super(MultiblockHandler.MULTIBLOCK_3X2X1, createMultiblockShapeBuilder(MultiblockHandler.MULTIBLOCK_3X2X1, SHAPE), pProperties);
    }

    /// / ANIMATE ////

    @Override
    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRandom) {
        if (pState.getValue(WATERLOGGED) || !getMultiblockHandler().isCenterState(pState)) {
            return;
        }
        // play sound
        if (pRandom.nextInt(10) == 0) {
            pLevel.playLocalSound(pPos.getX() + 0.5D, pPos.getY() + 0.5D, pPos.getZ() + 0.5D, SoundEvents.CAMPFIRE_CRACKLE, SoundSource.BLOCKS, 0.5F + pRandom.nextFloat(), pRandom.nextFloat() * 0.7F + 0.6F, false);
        }
        // particles
        final Direction direction = pState.getValue(FACING);
        final double dx = (pRandom.nextInt(19) - 10.0D) / 16.0D;
        final double dy = 7.0D / 16.0D;
        final Vec3 pos = Vec3.atBottomCenterOf(pPos)
                .add(dx * direction.getStepZ(), dy, dx * direction.getStepX());
        // smoke particle
        pLevel.addParticle(ParticleTypes.SMOKE, pos.x(), pos.y(), pos.z(), 0.0D, 0.0D, 0.0D);
        // fire particle
        if (pRandom.nextInt(5) == 0) {
            pLevel.addParticle(ParticleTypes.LAVA, pos.x(), pos.y(), pos.z(), 0.0D, 0.0D, 0.0D);
        }
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
                    {
                            Shapes.or(box(0, 0, 0, 16, 4, 16),
                                    box(4, 4, 4, 14, 16, 16),
                                    box(0, 4, 14, 4, 16, 16))
                    },
                    // width = 1
                    {
                            Shapes.or(box(0, 0, 0, 16, 4, 16),
                                    box(0, 4, 14, 16, 16, 16))
                    },
                    // width = 2
                    {
                            Shapes.or(box(0, 0, 0, 16, 4, 16),
                                    box(2, 4, 4, 12, 16, 16),
                                    box(12, 4, 14, 16, 16, 16))
                    }
            },
            // height = 2
            {
                    // width = 0
                    {
                            Shapes.or(box(0, 0, 4, 14, 11, 16),
                                    box(0, 11, 0, 16, 16, 16))
                    },
                    // width = 1
                    {
                            Shapes.or(box(0, 0, 4, 16, 11, 16),
                                    box(0, 11, 0, 16, 16, 16))
                    },
                    // width = 2
                    {
                            Shapes.or(box(2, 0, 4, 16, 11, 16),
                                    box(0, 11, 0, 16, 16, 16))
                    }
            }
    };
}
