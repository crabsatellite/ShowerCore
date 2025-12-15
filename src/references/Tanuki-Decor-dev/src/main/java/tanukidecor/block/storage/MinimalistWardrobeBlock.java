/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.storage;

import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import tanukidecor.TDRegistry;

public class MinimalistWardrobeBlock extends TallStorageBlock {

    public static final VoxelShape UPPER_SHAPE = box(0, 0, 1, 16, 16, 15);
    public static final VoxelShape LOWER_SHAPE = Shapes.or(
            box(0, 4, 1, 16, 16, 15),
            box(1, 0, 2, 3, 4, 4),
            box(13, 0, 2, 15, 4, 4),
            box(1, 0, 12, 3, 4, 14),
            box(13, 0, 12, 15, 4, 14));

    public MinimalistWardrobeBlock(Properties pProperties) {
        super(UPPER_SHAPE, LOWER_SHAPE, TDRegistry.BlockEntityReg.MINIMALIST_WARDROBE, pProperties);
    }
}
