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

public class AntiqueTableBlock extends RotatingMultiblock {

    public AntiqueTableBlock(Properties pProperties) {
        super(MultiblockHandler.MULTIBLOCK_3X1X2, createMultiblockShapeBuilder(MultiblockHandler.MULTIBLOCK_3X1X2, SHAPE), pProperties);
    }

    //// SHAPE ////

    /**
     * Shape data for each block in the default horizontal direction, ordered by index {@code [height][width][depth]}
     **/
    public static final VoxelShape[][][] SHAPE = new VoxelShape[][][]{
            // height = 0
            {
                    // width = 0
                    {
                            Shapes.empty(),
                            Shapes.or(box(8, 0, 4, 10, 6, 12),
                                    box(0, 2, 15, 12, 6, 16),
                                    box(8, 0, 12, 10, 14, 16),
                                    box(0, 14, 0, 16, 16, 16)),
                            Shapes.or(box(8, 0, 4, 10, 6, 12),
                                    box(8, 0, 0, 10, 14, 4),
                                    box(0, 14, 0, 16, 16, 16),
                                    box(0, 2, 0, 12, 6, 1))
                    },
                    // width = 1
                    {
                            Shapes.empty(),
                            Shapes.or(box(0, 14, 0, 16, 16, 16),
                                    box(0, 2, 15, 16, 6, 16)),
                            Shapes.or(box(0, 2, 0, 16, 6, 1),
                                    box(0, 14, 0, 16, 16, 16))
                    },
                    // width = 2
                    {
                            Shapes.empty(),
                            Shapes.or(box(6, 0, 4, 8, 6, 12),
                                    box(4, 2, 15, 16, 6, 16),
                                    box(6, 0, 12, 8, 14, 16),
                                    box(0, 14, 0, 16, 16, 16)),
                            Shapes.or(box(6, 0, 4, 8, 6, 12),
                                    box(0, 14, 0, 16, 16, 16),
                                    box(6, 0, 0, 8, 14, 4),
                                    box(4, 2, 0, 16, 6, 1))
                    }
            },
            // height = 1
            {},
            // height = 2
            {}
    };
}
