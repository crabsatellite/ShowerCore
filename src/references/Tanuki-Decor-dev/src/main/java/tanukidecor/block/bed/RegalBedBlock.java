/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.bed;

import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class RegalBedBlock extends DoubleBedBlock {

    public RegalBedBlock(Properties pProperties) {
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
                            box(0, 0, 0, 15, 8, 16),
                            Shapes.or(box(0, 0, 0, 15, 8, 15),
                                    box(14, 0, 14, 16, 16, 16),
                                    box(0, 8, 14.98D, 14, 16, 15))
                    },
                    // width = 2
                    {
                            Shapes.empty(),
                            box(1, 0, 0, 16, 8, 16),
                            Shapes.or(box(1, 0, 0, 16, 8, 15),
                                    box(0, 0, 14, 2, 16, 16),
                                    box(2, 8, 14.98D, 16, 16, 15))
                    }
            },
            // height = 1
            {},
            // height = 2
            {}
    };
}
