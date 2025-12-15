/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.misc;

import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import tanukidecor.block.RotatingWideBlock;

public class GreenCounterBlock extends RotatingWideBlock {

    public static final VoxelShape SHAPE_EAST = Shapes.or(
            box(11, 0, 3, 16, 1, 6),
            box(11, 0, 12, 16, 1, 15),
            box(0, 15, 1, 16, 16, 15),
            Shapes.join(
                    box(0, 1, 3, 16, 15, 15),
                    box(0, 8, 3, 14, 15, 14),
                    BooleanOp.ONLY_FIRST
            ));
    public static final VoxelShape SHAPE_WEST = Shapes.or(
            box(0, 0, 3, 5, 1, 6),
            box(0, 0, 12, 5, 1, 15),
            box(0, 15, 1, 16, 16, 15),
            Shapes.join(
                    box(0, 1, 3, 16, 15, 15),
                    box(2, 8, 3, 16, 15, 14),
                    BooleanOp.ONLY_FIRST
            ));

    public GreenCounterBlock(Properties pProperties) {
        super(pProperties, RotatingWideBlock.createShapeBuilder(SHAPE_EAST, SHAPE_WEST));
    }
}
