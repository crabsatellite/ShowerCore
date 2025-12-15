/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.clock;

import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import tanukidecor.TDRegistry;

public class SlateClockBlock extends ClockBlock {

    public static final VoxelShape SHAPE = Shapes.or(
            box(1, 0, 7, 15, 3, 15),
            box(2, 3, 9, 14, 9, 15),
            box(1, 9, 7, 15, 10, 15),
            box(2, 3, 8, 3, 9, 9),
            box(4, 3, 8, 5, 9, 9),
            box(11, 3, 8, 12, 9, 9),
            box(13, 3, 8, 14, 9, 9));

    public SlateClockBlock(Properties pProperties) {
        super(TDRegistry.SoundReg.MANTLE_CLOCK_TICK, TDRegistry.SoundReg.SLATE_CLOCK_CHIME,
                SHAPE, TDRegistry.BlockEntityReg.SLATE_CLOCK, pProperties);
    }
}
