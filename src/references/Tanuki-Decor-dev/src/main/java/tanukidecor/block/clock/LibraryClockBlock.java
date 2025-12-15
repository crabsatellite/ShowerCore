/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.clock;

import net.minecraft.core.BlockPos;
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

import java.util.function.Supplier;

public class LibraryClockBlock extends RotatingMultiblock implements EntityBlock, IChimeProvider {

    protected final Supplier<SoundEvent> chimeSound;
    protected final Supplier<SoundEvent> tickSound;

    public LibraryClockBlock(Properties pProperties) {
        super(MultiblockHandler.MULTIBLOCK_2X3X1, createMultiblockShapeBuilder(MultiblockHandler.MULTIBLOCK_2X3X1, LIBRARY_CLOCK_SHAPE), pProperties);
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
            return TDRegistry.BlockEntityReg.LIBRARY_CLOCK.get().create(pPos, pState);
        }
        return null;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return !pLevel.isClientSide() ? (BlockEntityTicker<T>) (BlockEntityTicker<ClockBlockEntity>) (ClockBlockEntity::tick) : null;
    }

    //// SHAPE ////

    /**
     * Shape data for each block in the default horizontal direction, ordered by index {@code [height][width][depth]}
     **/
    public static final VoxelShape[][][] LIBRARY_CLOCK_SHAPE = new VoxelShape[][][]{
            // height = 0
            {
                    // width = 0
                    {},
                    // width = 1
                    {
                            Shapes.or(box(0, 0, 2, 16, 8, 14),
                                    box(7, 8, 3, 15, 16, 13),
                                    box(0, 8, 2, 7, 16, 13))
                    },
                    // width = 2
                    {
                            Shapes.or(box(0, 0, 2, 16, 8, 14),
                                    box(1, 8, 3, 9, 16, 13),
                                    box(9, 8, 2, 16, 16, 13))
                    }
            },
            // height = 1
            {
                    // width = 0
                    {},
                    // width = 1
                    {
                            Shapes.or(box(7, 0, 3, 15, 13, 13),
                                    box(0, 0, 2, 7, 13, 13),
                                    box(0, 13, 2, 16, 16, 14))
                    },
                    // width = 2
                    {
                            Shapes.or(box(1, 0, 3, 9, 13, 13),
                                    box(9, 0, 2, 16, 13, 13),
                                    box(0, 13, 2, 16, 16, 14))
                    }
            },
            // height = 2
            {
                    // width = 0
                    {},
                    // width = 1
                    {
                            Shapes.or(box(0, 0, 2, 8, 3, 14),
                                    box(0, 3, 3, 7, 14, 13),
                                    box(0, 14, 2, 5, 16, 14))
                    },
                    // width = 2
                    {
                            Shapes.or(box(8, 0, 2, 16, 3, 14),
                                    box(9, 3, 3, 16, 14, 13),
                                    box(11, 14, 2, 16, 16, 14))
                    }
            }
    };
}
