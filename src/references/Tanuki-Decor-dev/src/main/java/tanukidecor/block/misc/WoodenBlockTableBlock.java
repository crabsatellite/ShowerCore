/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.misc;

import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import tanukidecor.block.RotatingMultiblock;
import tanukidecor.util.MultiblockHandler;

public class WoodenBlockTableBlock extends RotatingMultiblock {

    public WoodenBlockTableBlock(Properties pProperties) {
        super(MultiblockHandler.MULTIBLOCK_2X1X2, createMultiblockShapeBuilder(MultiblockHandler.MULTIBLOCK_2X1X2, SHAPE), pProperties);
    }

    //// SHAPE ////

    /**
     * Shape data for each block in the default horizontal direction, ordered by index {@code [height][width][depth]}
     **/
    public static final VoxelShape[][][] SHAPE = new VoxelShape[][][]{
            // height = 0
            {
                    // width = 0
                    {},
                    // width = 1
                    {
                            Shapes.empty(),
                            Shapes.or(box(9, 0, 1, 15, 13, 7),
                                    box(9, 8, 7, 15, 13, 16),
                                    box(0, 13, 0, 16, 16, 16)),
                            Shapes.or(box(9, 0, 9, 15, 13, 15),
                                    box(9, 8, 0, 15, 13, 9),
                                    box(0, 13, 0, 16, 16, 16)),
                    },
                    // width = 2
                    {
                            Shapes.empty(),
                            Shapes.or(box(1, 0, 1, 7, 13, 7),
                                    box(1, 8, 7, 7, 13, 16),
                                    box(0, 13, 0, 16, 16, 16)),
                            Shapes.or(box(1, 0, 9, 7, 13, 15),
                                    box(1, 8, 0, 7, 13, 9),
                                    box(0, 13, 0, 16, 16, 16))
                    }
            },
            // height = 1
            {},
            // height = 2
            {}
    };
}
