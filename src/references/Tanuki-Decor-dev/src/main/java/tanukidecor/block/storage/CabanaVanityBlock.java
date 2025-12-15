/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.storage;

import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import tanukidecor.TDRegistry;

public class CabanaVanityBlock extends TallStorageBlock {

    public static final VoxelShape UPPER_SHAPE = box(0, 0, 14, 16, 16, 16);
    public static final VoxelShape LOWER_SHAPE = Shapes.or(
            box(2, 3, 2, 4, 8, 4),
            box(1.5D, 0, 1.5D, 4.5D, 3, 4.5D),
            box(12, 3, 2, 14, 8, 4),
            box(11.5D, 0, 1.5, 14.5D, 3, 4.5D),
            box(2, 3, 12, 4, 8, 14),
            box(1.5D, 0, 11.5D, 4.5D, 3, 14.5D),
            box(12, 3, 12, 14, 8, 14),
            box(11.5D, 0, 11.5D, 14.5D, 3, 14.5D),
            box(0, 8, 0, 16, 16, 16));

    public CabanaVanityBlock(Properties pProperties) {
        super(UPPER_SHAPE, LOWER_SHAPE, TDRegistry.BlockEntityReg.CABANA_VANITY, pProperties);
    }
}
