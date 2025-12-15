/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.clock;

import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import tanukidecor.TDRegistry;

public class MantleClockBlock extends ClockBlock {

    public static final VoxelShape SHAPE = Shapes.or(
            box(0, 0, 4, 4, 1, 12),
            box(12, 0, 4, 16, 1, 12),
            box(0, 1, 5, 16, 3, 11),
            box(4, 3, 5, 12, 11, 11));

    public MantleClockBlock(Properties pProperties) {
        super(TDRegistry.SoundReg.MANTLE_CLOCK_TICK, TDRegistry.SoundReg.MANTLE_CLOCK_CHIME,
                SHAPE, TDRegistry.BlockEntityReg.MANTLE_CLOCK, pProperties);
    }
}
