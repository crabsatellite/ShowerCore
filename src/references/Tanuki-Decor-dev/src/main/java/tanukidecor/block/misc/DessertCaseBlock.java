/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.misc;

import net.minecraft.world.phys.shapes.VoxelShape;
import tanukidecor.block.RotatingMultiblock;
import tanukidecor.util.MultiblockHandler;

public class DessertCaseBlock extends RotatingMultiblock {

    public DessertCaseBlock(Properties pProperties) {
        super(MultiblockHandler.MULTIBLOCK_2X2X1, RotatingMultiblock.createMultiblockShapeBuilder(MultiblockHandler.MULTIBLOCK_2X2X1, SHAPE), pProperties);
    }

    //// SHAPE ////

    /**
     * Shape data for each block in the default horizontal direction, ordered by index {@code [height][width][depth]}
     **/
    public static final VoxelShape[][][] SHAPE = new VoxelShape[][][]{
            // height = 0
            {},
            // height = 1
            {
                    // width = 0
                    {},
                    // width = 1
                    {
                            box(0, 0, 1, 16, 16, 15)
                    },
                    // width = 2
                    {
                            box(0, 0, 1, 16, 16, 15)
                    }
            },
            // height = 2
            {
                    // width = 0
                    {},
                    // width = 1
                    {
                            box(0, 0, 2, 15, 15.4D, 14)
                    },
                    // width = 2
                    {
                            box(1, 0, 2, 16, 15.4D, 14)
                    }
            }
    };
}
