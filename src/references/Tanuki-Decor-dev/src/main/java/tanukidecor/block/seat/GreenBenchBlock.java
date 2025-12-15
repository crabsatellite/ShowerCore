/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.seat;

import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class GreenBenchBlock extends WideChairBlock {

    public static final VoxelShape SHAPE_EAST = Shapes.or(
            box(11, 0, 2, 15, 1, 14),
            box(12, 1, 5, 14, 6, 11),
            box(0, 2, 7, 15, 5, 9),
            box(0, 6, 1, 16, 8, 15),
            box(0, 8, 13, 16, 16, 15));
    public static final VoxelShape SHAPE_WEST = Shapes.or(
            box(1, 0, 2, 5, 1, 14),
            box(2, 1, 5, 4, 6, 11),
            box(1, 2, 7, 16, 5, 9),
            box(0, 6, 1, 16, 8, 15),
            box(0, 8, 13, 16, 16, 15));

    public GreenBenchBlock(Properties pProperties) {
        super(createShapeBuilder(SHAPE_EAST, SHAPE_WEST), 10.0D / 16.0D, pProperties);
    }
}
