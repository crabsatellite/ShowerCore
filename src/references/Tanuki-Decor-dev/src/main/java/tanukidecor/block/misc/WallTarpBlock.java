/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.misc;

import net.minecraft.world.phys.shapes.VoxelShape;
import tanukidecor.block.RotatingMultiblock;
import tanukidecor.util.MultiblockHandler;

public class WallTarpBlock extends RotatingMultiblock {

    public static final VoxelShape SHAPE = box(0, 14, 0, 16, 16, 16);

    public WallTarpBlock(Properties pProperties) {
        super(MultiblockHandler.MULTIBLOCK_2X1X2, b -> SHAPE, pProperties);
    }
}
