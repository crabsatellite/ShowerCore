/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.storage;

import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import tanukidecor.TDRegistry;

public class RegalVanityBlock extends TallStorageBlock {

    public static final VoxelShape UPPER_SHAPE = Shapes.or(
            box(0, 0, 9, 5, 3, 14),
            box(11, 0, 9, 16, 3, 14),
            box(1, 3, 10.5D, 3, 11, 12.5D),
            box(13, 3, 10.5D, 15, 11, 12.5D),
            box(3, 4, 11, 13, 15, 12));
    public static final VoxelShape LOWER_SHAPE = Shapes.or(
            box(0, 0, 0, 2, 8, 2),
            box(14, 0, 0, 16, 8, 2),
            box(0, 0, 14, 2, 8, 16),
            box(14, 0, 14, 16, 8, 16),
            box(0, 6, 0, 16, 16, 16));

    public RegalVanityBlock(Properties pProperties) {
        super(UPPER_SHAPE, LOWER_SHAPE, TDRegistry.BlockEntityReg.REGAL_VANITY, pProperties);
    }
}
