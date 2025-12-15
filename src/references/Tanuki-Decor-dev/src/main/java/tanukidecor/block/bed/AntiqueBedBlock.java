/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.bed;

import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class AntiqueBedBlock extends DoubleBedBlock {

    public AntiqueBedBlock(Properties pProperties) {
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
                            Shapes.or(box(0, 2, 3, 16, 8, 16),
                                    box(12, 0, 0, 16, 12, 3),
                                    box(0, 2, 1, 12, 12, 3),
                                    box(0, 12, 1, 6, 16, 3)),
                            Shapes.or(box(0, 2, 0, 16, 8, 13),
                                    box(12, 0, 13, 16, 16, 16),
                                    box(0, 2, 13, 12, 16, 15),
                                    box(0, 16, 13, 6, 20, 15),
                                    box(3, 8, 6, 13, 10, 12))
                    },
                    // width = 2
                    {
                            Shapes.empty(),
                            Shapes.or(box(0, 2, 3, 16, 8, 16),
                                    box(0, 0, 0, 4, 12, 3),
                                    box(4, 2, 1, 16, 12, 3),
                                    box(10, 12, 1, 16, 16, 3)),
                            Shapes.or(box(0, 2, 0, 16, 8, 13),
                                    box(0, 0, 13, 4, 16, 16),
                                    box(4, 2, 13, 16, 16, 15),
                                    box(10, 16, 13, 16, 20, 15),
                                    box(3, 8, 6, 13, 10, 12))
                    }
            },
            // height = 1
            {},
            // height = 2
            {}
    };
}
