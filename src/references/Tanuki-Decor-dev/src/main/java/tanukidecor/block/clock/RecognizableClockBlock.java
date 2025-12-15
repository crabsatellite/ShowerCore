/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.clock;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import tanukidecor.TDRegistry;

public class RecognizableClockBlock extends TallClockBlock {

    public static final VoxelShape UPPER_SHAPE = Shapes.or(
            box(3, -2, 3, 13, 8, 13),
            box(4.5D, 8, 4.5D, 11.5D, 12, 11.5D),
            box(6.5D, 12, 6.5D, 9.5D, 16, 9.5D));
    public static final VoxelShape LOWER_SHAPE = Shapes.or(
            box(1, 0, 1, 15, 1, 15),
            box(3.5D, 1, 3.5D, 12.5D, 14, 12.5D),
            box(3, 1, 3, 5, 7, 5),
            box(11, 1, 3, 13, 7, 5),
            box(3, 1, 11, 5, 7, 13),
            box(11, 1, 11, 13, 7, 13));

    public RecognizableClockBlock(Properties pProperties) {
        super(TDRegistry.SoundReg.GRANDFATHER_CLOCK_TICK, TDRegistry.SoundReg.RECOGNIZABLE_CLOCK_CHIME,
                UPPER_SHAPE, LOWER_SHAPE, TDRegistry.BlockEntityReg.RECOGNIZABLE_CLOCK, pProperties);
    }

    /// / CHIME PROVIDER ////

    @Override
    public int getTickSoundInterval(BlockState blockState) {
        return 40;
    }
}
