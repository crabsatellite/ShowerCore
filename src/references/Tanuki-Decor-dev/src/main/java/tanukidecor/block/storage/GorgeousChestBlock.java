/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.storage;

import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import tanukidecor.TDRegistry;

public class GorgeousChestBlock extends WideStorageBlock {

    public static final VoxelShape SHAPE_EAST = Shapes.or(
            box(13, 0, 3, 15, 2, 5),
            box(13, 0, 11, 15, 2, 13),
            box(0, 2, 2, 16, 4, 14),
            box(0, 4, 2, 14, 14, 14),
            box(0, 14, 1, 16, 16, 15));
    public static final VoxelShape SHAPE_WEST = Shapes.or(
            box(1, 0, 3, 3, 2, 5),
            box(1, 0, 11, 3, 2, 13),
            box(0, 2, 2, 16, 4, 14),
            box(2, 4, 2, 16, 14, 14),
            box(0, 14, 1, 16, 16, 15));

    public GorgeousChestBlock(Properties pProperties) {
        super(TDRegistry.BlockEntityReg.GORGEOUS_CHEST, createShapeBuilder(SHAPE_EAST, SHAPE_WEST), pProperties);
    }
}
