/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.seat;

import net.minecraft.world.phys.shapes.VoxelShape;

public class RoughLogBenchBlock extends WideChairBlock {

    public static final VoxelShape SHAPE_EAST = box(0, 0, 2, 16, 12, 14);
    public static final VoxelShape SHAPE_WEST = box(0, 0, 2, 16, 12, 14);

    public RoughLogBenchBlock(Properties pProperties) {
        super(WideChairBlock.createShapeBuilder(SHAPE_EAST, SHAPE_WEST), 14.0D / 16.0D, pProperties);
    }
}
