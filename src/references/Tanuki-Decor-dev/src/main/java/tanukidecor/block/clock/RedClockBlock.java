/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.clock;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import tanukidecor.TDRegistry;

public class RedClockBlock extends ClockBlock {

    public static final VoxelShape SHAPE = box(2, 0, 5, 14, 13, 11);

    public RedClockBlock(Properties pProperties) {
        super(TDRegistry.SoundReg.ALARM_CLOCK_TICK, TDRegistry.SoundReg.ALARM_CLOCK_CHIME,
                SHAPE, TDRegistry.BlockEntityReg.RED_CLOCK, pProperties);
    }

    /// / CHIME PROVIDER ////

    @Override
    public boolean isTimeToChime(BlockState blockState, long dayTime) {
        return dayTime == DAWN;
    }
}
