/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.bed;

import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SweetsBedBlock extends DoubleBedBlock {

    public SweetsBedBlock(Properties pProperties) {
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
                            box(0, 0, 0, 16, 8, 16),
                            Shapes.or(box(0, 0, 0, 16, 8, 14),
                                    box(4, 8, 7, 10, 11, 13),
                                    box(0, 0, 14, 16, 16, 16))
                    },
                    // width = 2
                    {
                            Shapes.empty(),
                            box(0, 0, 0, 16, 8, 16),
                            Shapes.or(box(0, 0, 0, 16, 8, 14),
                                    box(6, 8, 7, 12, 11, 13),
                                    box(0, 0, 14, 16, 16, 16))
                    }
            },
            // height = 1
            {},
            // height = 2
            {}
    };
}
