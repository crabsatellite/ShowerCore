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

public class RegalClockBlock extends TallClockBlock {

    public static final VoxelShape UPPER_SHAPE = Shapes.or(
            box(2, 0, 1, 14, 8, 15),
            box(1, 8, 1, 15, 12, 15),
            box(3, 12, 1, 13, 13, 15),
            box(6, 12, 1, 10, 16, 15));
    public static final VoxelShape LOWER_SHAPE = box(2, 0, 1, 14, 16, 15);

    public RegalClockBlock(Properties pProperties) {
        super(TDRegistry.SoundReg.GRANDFATHER_CLOCK_TICK, ClockBlock.NO_SOUND,
                UPPER_SHAPE, LOWER_SHAPE, TDRegistry.BlockEntityReg.REGAL_CLOCK, pProperties);
    }

    /// / CHIME PROVIDER ////

    @Override
    public int getTickSoundInterval(BlockState blockState) {
        return 40;
    }
}
