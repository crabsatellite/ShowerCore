/*
 * Copyright (c) 2024 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.misc;

import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import tanukidecor.TDRegistry;
import tanukidecor.block.TallBlock;

import java.util.HashMap;
import java.util.Map;

public class RocketLampBlock extends TallBlock implements EntityBlock {

    public static final VoxelShape SHAPE_UPPER = Shapes.or(
            box(4, 0, 4, 12, 8, 12),
            box(3.5D, 6, 3.5D, 12.5D, 10, 12.5D),
            box(5, 10, 5, 11, 12, 11),
            box(6, 12, 6, 10, 14, 10));
    public static final VoxelShape SHAPE_LOWER = Shapes.or(
            box(3, 0, 3, 13, 3, 13),
            box(5, 3, 5, 11, 6, 11),
            box(3.5D, 6, 3.5D, 12.5D, 10, 12.5D),
            box(4, 8, 4, 12, 16, 12));

    protected final String color;

    private static final Map<String, RocketLampBlock> COLORS = new HashMap<>();

    public RocketLampBlock(final String color, Properties pProperties) {
        super(SHAPE_UPPER, SHAPE_LOWER, pProperties);
        this.color = color;
        COLORS.put(color, this);
    }

    public String getColor() {
        return color;
    }

    public static Map<String, RocketLampBlock> getColors() {
        return ImmutableMap.copyOf(COLORS);
    }

    /// / BLOCK ENTITY ////

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        if (getDelegatePos(pState, pPos).equals(pPos)) {
            return TDRegistry.BlockEntityReg.ROCKET_LAMP.get().create(pPos, pState);
        }
        return null;
    }
}
