/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.clock;

import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import tanukidecor.TDRegistry;

public class GingerbreadClockBlock extends TallClockBlock {

    public static final VoxelShape UPPER_SHAPE = box(4, 0, 7, 12, 3, 10);
    public static final VoxelShape LOWER_SHAPE = Shapes.or(
            box(2, 0, 6, 14, 3, 11),
            box(4, 3, 7, 12, 16, 10));

    public GingerbreadClockBlock(Properties pProperties) {
        super(TDRegistry.SoundReg.MEDIUM_CLOCK_TICK, TDRegistry.SoundReg.MEDIUM_CLOCK_CHIME2,
                UPPER_SHAPE, LOWER_SHAPE, TDRegistry.BlockEntityReg.GINGERBREAD_CLOCK,
                pProperties);
    }
}
