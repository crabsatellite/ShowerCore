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

public class BlueTableBlock extends RotatingMultiblock {

    public BlueTableBlock(Properties pProperties) {
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
                            Shapes.or(box(11, 0, 2, 14, 13, 5),
                                    box(0, 13, 0, 16, 16, 16)),
                            Shapes.or(box(11, 0, 11, 14, 13, 14),
                                    box(0, 13, 0, 16, 16, 16)),
                    },
                    // width = 2
                    {
                            Shapes.empty(),
                            Shapes.or(box(2, 0, 2, 5, 13, 5),
                                    box(0, 13, 0, 16, 16, 16)),
                            Shapes.or(box(2, 0, 11, 5, 13, 14),
                                    box(0, 13, 0, 16, 16, 16))
                    }
            },
            // height = 1
            {},
            // height = 2
            {}
    };
}
