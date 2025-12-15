/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.light;

import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import tanukidecor.block.RotatingBlock;

public class RegalLampBlock extends RotatingBlock {

    public static final VoxelShape SHAPE = Shapes.or(
            box(7, 0, 7, 9, 1, 9),
            box(6.5D, 1, 6.5D, 9.5D, 13, 9.5D),
            box(3, 5, 3, 13, 16, 13));

    public RegalLampBlock(Properties pProperties) {
        super(pProperties, RotatingBlock.createShapeBuilder(SHAPE));
    }
}
