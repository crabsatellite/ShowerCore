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

public class MushroomTableBlock extends RotatingMultiblock {

    public MushroomTableBlock(Properties pProperties) {
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
                            Shapes.or(box(0, 10, 4, 12, 16, 16),
                                    box(0, 0, 12, 4, 10, 16),
                                    box(12, 10, 10, 16, 16, 16),
                                    box(0, 10, 0, 6, 16, 4)),
                            Shapes.or(box(0, 10, 0, 12, 16, 12),
                                    box(0, 0, 0, 4, 10, 4),
                                    box(0, 10, 12, 6, 16, 16),
                                    box(12, 10, 0, 16, 16, 6)),
                    },
                    // width = 2
                    {
                            Shapes.empty(),
                            Shapes.or(box(4, 10, 4, 16, 16, 16),
                                    box(12, 0, 12, 16, 10, 16),
                                    box(10, 10, 0, 16, 16, 4),
                                    box(0, 10, 10, 4, 16, 16)),
                            Shapes.or(box(4, 10, 0, 16, 16, 12),
                                    box(12, 0, 0, 16, 10, 4),
                                    box(0, 10, 0, 4, 16, 6),
                                    box(10, 10, 12, 16, 16, 16))
                    }
            },
            // height = 1
            {},
            // height = 2
            {}
    };
}
