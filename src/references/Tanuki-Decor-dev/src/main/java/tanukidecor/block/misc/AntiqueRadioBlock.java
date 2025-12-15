/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.misc;

import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import tanukidecor.block.RotatingBlock;

public class AntiqueRadioBlock extends RotatingBlock {

    public static final VoxelShape SHAPE = Shapes.or(
            box(2, 0, 3, 14, 2, 12),
            box(3, 2, 4, 13, 12, 11),
            box(7, 3, 3, 9, 5, 4));

    public AntiqueRadioBlock(Properties pProperties) {
        super(pProperties, RotatingBlock.createShapeBuilder(SHAPE));
    }
}
