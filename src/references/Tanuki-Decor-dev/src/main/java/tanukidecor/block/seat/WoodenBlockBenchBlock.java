/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.seat;

import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class WoodenBlockBenchBlock extends WideChairBlock {

    public static final VoxelShape SHAPE_EAST = Shapes.or(
            box(13, 0, 2, 16, 12, 14),
            box(0, 5, 3, 13, 7, 13));
    public static final VoxelShape SHAPE_WEST = Shapes.or(
            box(0, 0, 2, 3, 12, 14),
            box(3, 5, 3, 16, 7, 13));

    public WoodenBlockBenchBlock(Properties pProperties) {
        super(createShapeBuilder(SHAPE_EAST, SHAPE_WEST), 9.0D / 16.0D, pProperties);
    }
}
