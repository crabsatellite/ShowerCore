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

public class GreenTableBlock extends RotatingMultiblock {

    public GreenTableBlock(Properties pProperties) {
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
                            Shapes.or(box(0, 14, 0, 16, 16, 16),
                                    box(11, 2, 6, 13, 14, 16),
                                    box(10, 0, 4, 14, 2, 16),
                                    box(0, 6, 13, 14, 8, 16)),
                            Shapes.or(box(0, 14, 0, 16, 16, 16),
                                    box(11, 2, 0, 13, 14, 10),
                                    box(10, 0, 0, 14, 2, 12),
                                    box(0, 6, 0, 14, 8, 3)),
                    },
                    // width = 2
                    {
                            Shapes.empty(),
                            Shapes.or(box(0, 14, 0, 16, 16, 16),
                                    box(3, 2, 6, 5, 14, 16),
                                    box(2, 0, 4, 6, 2, 16),
                                    box(2, 6, 13, 16, 8, 16)),
                            Shapes.or(box(0, 14, 0, 16, 16, 16),
                                    box(3, 2, 0, 5, 14, 10),
                                    box(2, 0, 0, 6, 2, 12),
                                    box(2, 6, 0, 16, 8, 3))
                    }
            },
            // height = 1
            {},
            // height = 2
            {}
    };
}
