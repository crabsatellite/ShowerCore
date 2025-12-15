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

public class AlarmClockBlock extends ClockBlock {

    public static final VoxelShape SHAPE = Shapes.or(
            box(3, 2, 5, 13, 12, 11),
            box(6.5D, 12, 8, 9.5D, 15, 8),
            box(2, 0, 7, 4, 4, 9),
            box(12, 0, 7, 14, 4, 9));

    public AlarmClockBlock(Properties pProperties) {
        super(TDRegistry.SoundReg.ALARM_CLOCK_TICK, TDRegistry.SoundReg.ALARM_CLOCK_CHIME,
                SHAPE, TDRegistry.BlockEntityReg.ALARM_CLOCK, pProperties);
    }

    /// / CHIME PROVIDER ////

    @Override
    public boolean isTimeToChime(BlockState blockState, long dayTime) {
        return dayTime == DAWN;
    }
}
