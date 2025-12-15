/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.clock;

import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import tanukidecor.TDRegistry;

public class CrystalClockBlock extends ClockBlock {

    public static final VoxelShape SHAPE = Shapes.or(
            box(3, 0, 3, 13, 2, 13),
            box(4, 2, 4, 12, 14, 12),
            box(3, 14, 3, 13, 16, 13));

    public CrystalClockBlock(Properties pProperties) {
        super(TDRegistry.SoundReg.MANTLE_CLOCK_TICK, TDRegistry.SoundReg.LANTERN_CLOCK_CHIME,
                SHAPE, TDRegistry.BlockEntityReg.CRYSTAL_CLOCK, pProperties);
    }
}
