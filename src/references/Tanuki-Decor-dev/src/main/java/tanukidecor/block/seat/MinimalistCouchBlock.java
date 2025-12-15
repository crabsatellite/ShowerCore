/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.seat;

import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class MinimalistCouchBlock extends WideChairBlock {

    public static final VoxelShape SHAPE_EAST = Shapes.or(
            box(0, 3, 0, 12, 8, 16),
            box(12, 3, 0, 16, 12, 16),
            box(0, 8, 12, 12, 16, 16),
            box(13, 0, 1, 15, 3, 3),
            box(13, 0, 13, 15, 3, 15));
    public static final VoxelShape SHAPE_WEST = Shapes.or(
            box(4, 3, 0, 16, 8, 16),
            box(0, 3, 0, 4, 12, 16),
            box(4, 8, 12, 16, 16, 16),
            box(1, 0, 1, 3, 3, 3),
            box(1, 0, 13, 3, 3, 15));

    public MinimalistCouchBlock(Properties pProperties) {
        super(createShapeBuilder(SHAPE_EAST, SHAPE_WEST), 10.0D / 16.0D, pProperties);
    }
}
