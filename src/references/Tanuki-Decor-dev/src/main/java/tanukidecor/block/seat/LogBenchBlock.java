/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.seat;

import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class LogBenchBlock extends WideChairBlock {

    public static final VoxelShape SHAPE_EAST = Shapes.or(
            box(0, 4, 2, 16, 12, 14),
            box(7, 0, 5, 13, 4, 11));
    public static final VoxelShape SHAPE_WEST = Shapes.or(
            box(0, 4, 2, 16, 12, 14),
            box(3, 0, 5, 9, 4, 11));

    public LogBenchBlock(Properties pProperties) {
        super(WideChairBlock.createShapeBuilder(SHAPE_EAST, SHAPE_WEST), 14.0D / 16.0D, pProperties);
    }
}
