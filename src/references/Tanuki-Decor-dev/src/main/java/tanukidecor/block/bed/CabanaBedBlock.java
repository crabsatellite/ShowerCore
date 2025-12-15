/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.bed;

import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CabanaBedBlock extends DoubleBedBlock {

    public CabanaBedBlock(Properties pProperties) {
        super(SHAPE, pProperties);
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
                            Shapes.or(box(0, 0, 0, 16, 14, 3),
                                    box(0, 1, 3, 16, 5, 16),
                                    box(0, 5, 3, 15, 8, 16)),
                            Shapes.or(box(0, 0, 13, 16, 14, 16),
                                    box(0, 1, 0, 16, 5, 13),
                                    box(0, 5, 0, 15, 8, 13),
                                    box(1, 8, 8, 11, 9, 12))
                    },
                    // width = 2
                    {
                            Shapes.empty(),
                            Shapes.or(box(0, 0, 0, 16, 14, 3),
                                    box(0, 1, 3, 16, 5, 16),
                                    box(1, 5, 3, 16, 8, 16)),
                            Shapes.or(box(0, 0, 13, 16, 14, 16),
                                    box(0, 1, 0, 16, 5, 13),
                                    box(1, 5, 0, 16, 8, 13),
                                    box(5, 8, 8, 15, 9, 12))
                    }
            },
            // height = 1
            {},
            // height = 2
            {}
    };
}
