/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.misc;

import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import tanukidecor.block.RotatingMultiblock;
import tanukidecor.util.MultiblockHandler;
import tanukidecor.util.ShapeBuilder;
import tanukidecor.util.ShapeUtils;

public class StallTarpBlock extends RotatingMultiblock {

    public static final VoxelShape NORTH_SHAPE = Shapes.or(
            box(0, 4, 3, 16, 7, 10),
            box(0, 7, 10, 16, 10, 16));
    public static final VoxelShape SOUTH_SHAPE = Shapes.or(
            box(0, 10, 0, 16, 13, 8),
            box(0, 13, 8, 16, 16, 16));

    public StallTarpBlock(Properties pProperties) {
        super(MultiblockHandler.MULTIBLOCK_2X1X2, StallTarpBlock.createTarpShapeBuilder(NORTH_SHAPE, SOUTH_SHAPE), pProperties);
    }

    public static ShapeBuilder createTarpShapeBuilder(VoxelShape northShape, VoxelShape southShape) {
        return blockState -> {
            final Vec3i index = MultiblockHandler.MULTIBLOCK_2X1X2.getIndex(blockState);
            final Direction direction = blockState.getValue(FACING);
            final VoxelShape shape = index.getZ() == 0 ? northShape : southShape;
            return ShapeUtils.rotateShape(MultiblockHandler.ORIGIN_DIRECTION, direction, shape);
        };
    }
}
