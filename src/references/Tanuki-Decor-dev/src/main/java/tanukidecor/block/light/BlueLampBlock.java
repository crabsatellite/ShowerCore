/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.light;

import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import tanukidecor.block.RotatingBlock;

public class BlueLampBlock extends RotatingBlock {

    public static final VoxelShape SHAPE = Shapes.or(
            box(6, 0, 6, 10, 1, 10),
            box(7, 1, 7, 9, 10, 9),
            box(5, 6, 5, 11, 12, 11));

    public BlueLampBlock(Properties pProperties) {
        super(pProperties, RotatingBlock.createShapeBuilder(SHAPE));
    }
}
