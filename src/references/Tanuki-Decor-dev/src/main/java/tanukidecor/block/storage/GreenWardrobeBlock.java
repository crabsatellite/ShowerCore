/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.storage;

import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import tanukidecor.TDRegistry;

public class GreenWardrobeBlock extends TallStorageBlock {

    public static final VoxelShape UPPER_SHAPE = Shapes.or(
            box(0, 0, 1, 16, 14, 15),
            box(0, 14, 0, 16, 16, 16));
    public static final VoxelShape LOWER_SHAPE = Shapes.or(
            box(0, 2, 1, 16, 16, 15),
            box(0, 0, 1, 4, 2, 15),
            box(12, 0, 1, 16, 2, 15));

    public GreenWardrobeBlock(Properties pProperties) {
        super(UPPER_SHAPE, LOWER_SHAPE, TDRegistry.BlockEntityReg.GREEN_WARDROBE, pProperties);
    }
}
