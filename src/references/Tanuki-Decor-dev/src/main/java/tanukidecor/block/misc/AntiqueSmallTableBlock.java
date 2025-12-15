/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.misc;

import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import tanukidecor.block.RotatingBlock;

public class AntiqueSmallTableBlock extends RotatingBlock {

    public static final VoxelShape SHAPE = Shapes.or(
            box(7, 2, 7, 9, 14, 9),
            box(6, 5, 6, 10, 6, 10),
            box(2, 2, 7, 7, 5, 9),
            box(2, 0, 7, 5, 2, 9),
            box(9, 2, 7, 14, 5, 9),
            box(11, 0, 7, 14, 2, 9),
            box(7, 2, 2, 9, 5, 7),
            box(7, 0, 2, 9, 2, 5),
            box(7, 2, 9, 9, 5, 14),
            box(0, 14, 0, 16, 16, 16));

    public AntiqueSmallTableBlock(Properties pProperties) {
        super(pProperties, RotatingBlock.createShapeBuilder(SHAPE));
    }
}
