/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.seat;

import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import tanukidecor.block.RotatingBlock;

public class SweetsSofaBlock extends WideChairBlock {

    public static final VoxelShape SHAPE = Shapes.or(
            box(0, 0, 2, 16, 8, 14),
            box(0, 8, 9, 16, 16, 16));

    public SweetsSofaBlock(Properties pProperties) {
        super(RotatingBlock.createShapeBuilder(SHAPE), 10.0D / 16.0D, pProperties);
    }
}
