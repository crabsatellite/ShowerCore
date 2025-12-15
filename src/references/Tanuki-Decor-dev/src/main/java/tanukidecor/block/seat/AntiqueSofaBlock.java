/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.seat;

import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class AntiqueSofaBlock extends WideChairBlock {

    public static final VoxelShape SHAPE_EAST = Shapes.or(
            box(13, 1, 2, 15, 3, 4),
            box(13, 0, 1, 16, 1, 4),
            box(13, 1, 12, 15, 3, 14),
            box(13, 0, 12, 16, 1, 15),
            box(0, 3, 1, 16, 8, 15),
            box(0, 8, 12, 16, 16, 15),
            box(13, 8, 1, 16, 14, 15));
    public static final VoxelShape SHAPE_WEST = Shapes.or(
            box(1, 1, 2, 3, 3, 4),
            box(0, 0, 1, 3, 1, 4),
            box(1, 1, 12, 3, 3, 14),
            box(0, 0, 12, 3, 1, 15),
            box(0, 3, 1, 16, 8, 15),
            box(0, 8, 12, 16, 16, 15),
            box(0, 8, 1, 3, 14, 15));

    public AntiqueSofaBlock(Properties pProperties) {
        super(createShapeBuilder(SHAPE_EAST, SHAPE_WEST), 10.0D / 16.0D, pProperties);
    }
}
