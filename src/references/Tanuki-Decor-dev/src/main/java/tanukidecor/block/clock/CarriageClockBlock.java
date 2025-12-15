/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.clock;

import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import tanukidecor.TDRegistry;

public class CarriageClockBlock extends ClockBlock {

    public static final VoxelShape SHAPE = Shapes.or(
            box(3, 0, 4, 13, 2, 12),
            box(4, 2, 5, 12, 12, 11),
            box(3, 12, 4, 13, 13, 12));

    public CarriageClockBlock(Properties pProperties) {
        super(TDRegistry.SoundReg.POCKET_WATCH_TICK, TDRegistry.SoundReg.SLATE_CLOCK_CHIME,
                SHAPE, TDRegistry.BlockEntityReg.CARRIAGE_CLOCK, pProperties);
    }
}
