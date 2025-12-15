/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.seat;

import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BlueBenchBlock extends WideChairBlock {

    public static final VoxelShape SHAPE_EAST = Shapes.or(
            box(12, 0, 2, 14, 6, 4),
            box(12, 0, 12, 14, 6, 14),
            box(0, 6, 1, 15, 8, 15),
            box(0, 8, 13, 15, 16, 15));
    public static final VoxelShape SHAPE_WEST = Shapes.or(
            box(2, 0, 2, 4, 6, 4),
            box(2, 0, 12, 4, 6, 14),
            box(1, 6, 1, 16, 8, 15),
            box(1, 8, 13, 16, 16, 15));

    public BlueBenchBlock(Properties pProperties) {
        super(createShapeBuilder(SHAPE_EAST, SHAPE_WEST), 10.0D / 16.0D, pProperties);
    }
}
