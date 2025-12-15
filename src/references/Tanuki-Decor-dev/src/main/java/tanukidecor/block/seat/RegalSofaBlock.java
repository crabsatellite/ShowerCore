/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.seat;

import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class RegalSofaBlock extends WideChairBlock {

    public static final VoxelShape SHAPE_EAST = Shapes.or(
            box(0, 0, 0, 16, 8, 16),
            box(13, 8, 0, 16, 11, 16),
            box(0, 8, 13, 13, 16, 16));
    public static final VoxelShape SHAPE_WEST = Shapes.or(
            box(0, 0, 0, 16, 8, 16),
            box(0, 8, 0, 3, 11, 16),
            box(3, 8, 13, 16, 16, 16));

    public RegalSofaBlock(Properties pProperties) {
        super(createShapeBuilder(SHAPE_EAST, SHAPE_WEST), 10.0D / 16.0D, pProperties);
    }
}
