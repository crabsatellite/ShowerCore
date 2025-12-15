/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.clock;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import tanukidecor.TDRegistry;

public class AntiqueClockBlock extends TallClockBlock {

    public static final VoxelShape UPPER_SHAPE = Shapes.or(
            box(1, 0, 1, 15, 12, 15),
            box(0, 12, 0, 16, 16, 16));
    public static final VoxelShape LOWER_SHAPE = Shapes.or(
            box(0, 2, 0, 16, 6, 16),
            box(1, 6, 1, 15, 16, 15),
            Shapes.join(
                    box(0, 0, 0, 16, 2, 16),
                    Shapes.or(
                            box(4, 0, 0, 12, 2, 16),
                            box(0, 0, 4, 16, 2, 12),
                            box(2, 0, 2, 14, 2, 14)
                    ),
                    BooleanOp.ONLY_FIRST
            ));

    public AntiqueClockBlock(Properties pProperties) {
        super(TDRegistry.SoundReg.GRANDFATHER_CLOCK_TICK, TDRegistry.SoundReg.GRANDFATHER_CLOCK_CHIME,
                UPPER_SHAPE, LOWER_SHAPE, TDRegistry.BlockEntityReg.ANTIQUE_CLOCK, pProperties);
    }

    /// / CHIME PROVIDER ////

    @Override
    public int getTickSoundInterval(BlockState blockState) {
        return 40;
    }
}
