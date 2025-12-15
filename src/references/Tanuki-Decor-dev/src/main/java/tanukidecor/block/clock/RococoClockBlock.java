/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.clock;

import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import tanukidecor.TDRegistry;

public class RococoClockBlock extends ClockBlock {

    public static final VoxelShape SHAPE = Shapes.or(
            box(2, 0, 2, 14, 3, 14),
            box(3, 3, 3, 13, 14, 13),
            box(4, 14, 4, 12, 16, 12),
            box(6, 16, 6, 10, 18, 10),
            box(6.5D, 18, 6.5D, 9.5D, 21, 9.5D),
            box(1.5D, 9, 1.5D, 4.5D, 13, 4.5D),
            box(1.5D, 4, 1.5D, 4.5D, 6, 4.5D),
            box(11.5D, 9, 1.5D, 14.5D, 13, 4.5D),
            box(11.5D, 4, 1.5D, 14.5D, 6, 4.5),
            box(1.5D, 9, 11.5D, 4.5D, 13, 14.5D),
            box(1.5D, 4, 11.5D, 4.5D, 6, 14.5D),
            box(11.5D, 9, 11.5D, 14.5D, 13, 14.5D),
            box(11.5D, 4, 11.5D, 14.5D, 6, 14.5D));

    public RococoClockBlock(Properties pProperties) {
        super(TDRegistry.SoundReg.MANTLE_CLOCK_TICK, TDRegistry.SoundReg.LANTERN_CLOCK_CHIME,
                SHAPE, TDRegistry.BlockEntityReg.ROCOCO_CLOCK, pProperties);
    }
}
