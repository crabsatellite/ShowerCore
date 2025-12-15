/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.seat;

import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class RegalChairBlock extends TallChairBlock {

    public static final VoxelShape UPPER_SHAPE = box(2, 0, 12, 14, 8, 14);
    public static final VoxelShape LOWER_SHAPE = Shapes.or(
            box(3, 0, 3, 5, 8, 5),
            box(11, 0, 3, 13, 8, 5),
            box(3, 0, 11, 5, 8, 13),
            box(11, 0, 11, 13, 8, 13),
            box(5, 4, 3.5D, 11, 5, 4.5D),
            box(5, 4, 11.5D, 11, 5, 12.5D),
            box(3.5D, 4, 5, 4.5D, 5, 11),
            box(11.5D, 4, 5, 12.5D, 5, 11),
            box(2, 8, 2, 14, 10, 14),
            box(2, 10, 12, 14, 16, 14),
            box(4, 13, 13, 12, 16, 13));

    public RegalChairBlock(Properties pProperties) {
        super(UPPER_SHAPE, LOWER_SHAPE, 12.0D / 16.0D, pProperties);
    }
}
