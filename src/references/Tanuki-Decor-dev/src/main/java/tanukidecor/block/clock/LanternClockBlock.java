/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.clock;

import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import tanukidecor.TDRegistry;

public class LanternClockBlock extends ClockBlock {

    public static final VoxelShape SHAPE = Shapes.or(
            box(3.5D, 0, 3.5D, 5.5D, 1, 5.5D),
            box(10.5D, 0, 3.5D, 12.5D, 1, 5.5D),
            box(3.5D, 0, 10.5D, 5.5D, 1, 12.5D),
            box(10.5D, 0, 10.5D, 12.5D, 1, 12.5D),
            box(4, 1, 4, 12, 14, 12),
            box(4.5D, 13, 4.5D, 11.5D, 16, 11.5D),
            box(7.5D, 16, 7.5D, 8.5D, 19, 8.5D));

    public LanternClockBlock(Properties pProperties) {
        super(TDRegistry.SoundReg.FOLIOT_CLOCK_TICK, TDRegistry.SoundReg.LANTERN_CLOCK_CHIME,
                SHAPE, TDRegistry.BlockEntityReg.LANTERN_CLOCK, pProperties);
    }
}
