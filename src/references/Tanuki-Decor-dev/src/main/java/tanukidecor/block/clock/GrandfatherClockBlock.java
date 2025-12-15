/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.clock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import tanukidecor.TDRegistry;
import tanukidecor.block.RotatingMultiblock;
import tanukidecor.block.entity.ClockBlockEntity;
import tanukidecor.util.MultiblockHandler;
import tanukidecor.util.ShapeUtils;

import java.util.function.Supplier;

public class GrandfatherClockBlock extends RotatingMultiblock implements EntityBlock, IChimeProvider {

    protected final Supplier<SoundEvent> chimeSound;
    protected final Supplier<SoundEvent> tickSound;

    public GrandfatherClockBlock(Properties pProperties) {
        super(MultiblockHandler.MULTIBLOCK_1X3X1, GrandfatherClockBlock::buildShape, pProperties);
        this.tickSound = TDRegistry.SoundReg.GRANDFATHER_CLOCK_TICK;
        this.chimeSound = TDRegistry.SoundReg.GRANDFATHER_CLOCK_CHIME;
    }

    /// / CHIME PROVIDER ////

    @Nullable
    @Override
    public SoundEvent getChimeSound(BlockState blockState) {
        return this.chimeSound.get();
    }

    @Nullable
    @Override
    public SoundEvent getTickSound(BlockState blockState) {
        return this.tickSound.get();
    }

    @Override
    public int getTickSoundInterval(BlockState blockState) {
        return 40;
    }

    /// / BLOCK ENTITY ////

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        if (this.getMultiblockHandler().isCenterState(pState)) {
            return TDRegistry.BlockEntityReg.GRANDFATHER_CLOCK.get().create(pPos, pState);
        }
        return null;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return !pLevel.isClientSide() ? (BlockEntityTicker<T>) (BlockEntityTicker<ClockBlockEntity>) (ClockBlockEntity::tick) : null;
    }

    /// / SHAPE ////

    public static VoxelShape[] SHAPES = new VoxelShape[]{
            // bottom
            Shapes.or(box(0, 0, 1, 16, 4, 15),
                    box(1, 4, 2, 15, 12, 14),
                    box(2, 12, 3, 14, 16, 13)),
            // middle
            box(2, 0, 3, 14, 16, 13),
            // top
            Shapes.or(box(0, 0, 2, 16, 2, 15),
                    box(2, 2, 4, 14, 12, 13),
                    box(0, 12, 2, 6, 16, 15),
                    box(10, 12, 2, 16, 16, 15),
                    box(6, 12, 2, 10, 14, 15),
                    box(13, 2, 3, 15, 12, 5),
                    box(1, 2, 3, 3, 12, 5),
                    box(13, 2, 12, 15, 12, 14),
                    box(1, 2, 12, 3, 12, 14))
    };

    public static VoxelShape buildShape(final BlockState blockState) {
        final Vec3i index = MultiblockHandler.MULTIBLOCK_1X3X1.getIndex(blockState);
        final Vec3i dimensions = MultiblockHandler.MULTIBLOCK_1X3X1.getDimensions();
        final Direction facing = blockState.getValue(FACING);
        final VoxelShape shape = SHAPES[index.getY() + dimensions.getY() / 2];
        return ShapeUtils.rotateShape(MultiblockHandler.ORIGIN_DIRECTION, facing, shape);
    }
}
