/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.misc;

import net.minecraft.world.phys.shapes.VoxelShape;
import tanukidecor.block.RotatingBlock;

public class WoodenBlockToysBlock extends RotatingBlock {

    public static final VoxelShape SHAPE = box(2, 0, 2, 14, 9, 14);

    public WoodenBlockToysBlock(Properties pProperties) {
        super(pProperties, b -> SHAPE);
    }
}
