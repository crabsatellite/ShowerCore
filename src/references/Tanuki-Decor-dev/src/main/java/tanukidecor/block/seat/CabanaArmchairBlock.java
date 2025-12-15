/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.seat;

import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CabanaArmchairBlock extends ChairBlock {

    public static final VoxelShape SHAPE = Shapes.or(
            box(1, 0, 1, 3, 2, 3),
            box(13, 0, 1, 15, 2, 3),
            box(1, 0, 13, 3, 2, 15),
            box(13, 0, 13, 15, 2, 15),
            box(0, 2, 0, 16, 5, 16),
            box(0, 5, 13, 16, 16, 16),
            box(2, 5, 1, 14, 8, 13),
            box(0, 5, 0, 2, 10, 13),
            box(14, 5, 0, 16, 10, 13));

    public CabanaArmchairBlock(Properties pProperties) {
        super(SHAPE, 10.0D / 16.0D, pProperties);
    }
}
