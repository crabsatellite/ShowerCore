/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.misc;

import net.minecraft.world.phys.shapes.VoxelShape;
import tanukidecor.block.RotatingTallBlock;

public class WoodenBlockBookshelfBlock extends RotatingTallBlock {

    public static final VoxelShape SHAPE_UPPER = box(0, 0, 4, 16, 8, 16);
    public static final VoxelShape SHAPE_LOWER = box(0, 0, 4, 16, 16, 16);

    public WoodenBlockBookshelfBlock(Properties pProperties) {
        super(pProperties, RotatingTallBlock.createShapeBuilder(SHAPE_UPPER, SHAPE_LOWER));
    }
}
