/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.misc;

import net.minecraft.world.phys.shapes.VoxelShape;
import tanukidecor.block.RotatingTallBlock;

public class HolidayTreeBlock extends RotatingTallBlock {

    public static final VoxelShape SHAPE_UPPER = box(4.5D, 0, 4.5D, 11.5D, 16, 11.5D);
    public static final VoxelShape SHAPE_LOWER = box(2.5D, 0, 2.5D, 13.5D, 16, 13.5D);

    public HolidayTreeBlock(Properties pProperties) {
        super(pProperties, RotatingTallBlock.createShapeBuilder(SHAPE_UPPER, SHAPE_LOWER));
    }
}
