/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.seat;

import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class GorgeousSofaBlock extends WideChairBlock {

    public static final VoxelShape SHAPE_EAST = Shapes.or(
            box(12, 0, 0, 14, 3, 2),
            box(12, 0, 12, 14, 3, 14),
            Shapes.join(
                    Shapes.or(
                            box(0, 3, 0, 14, 13, 14),
                            box(0, 13, 0, 16, 16, 16)),
                    box(0, 10, 0, 11, 16, 11),
                    BooleanOp.ONLY_FIRST
            ));
    public static final VoxelShape SHAPE_WEST = Shapes.or(
            box(2, 0, 0, 4, 3, 2),
            box(2, 0, 12, 4, 3, 14),
            Shapes.join(
                    Shapes.or(
                            box(2, 3, 0, 16, 13, 14),
                            box(0, 13, 0, 16, 16, 16)),
                    box(5, 10, 0, 16, 16, 11),
                    BooleanOp.ONLY_FIRST
            ));

    public GorgeousSofaBlock(Properties pProperties) {
        super(createShapeBuilder(SHAPE_EAST, SHAPE_WEST), 12.0D / 16.0D, pProperties);
    }
}
