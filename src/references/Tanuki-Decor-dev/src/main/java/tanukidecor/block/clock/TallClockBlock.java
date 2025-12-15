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
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import tanukidecor.block.RotatingTallBlock;
import tanukidecor.block.entity.ClockBlockEntity;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

public class TallClockBlock extends RotatingTallBlock implements EntityBlock, IChimeProvider {

    protected final Supplier<SoundEvent> tickSound;
    protected final Supplier<SoundEvent> chimeSound;
    protected final Supplier<BlockEntityType<ClockBlockEntity>> blockEntitySupplier;

    /**
     * Simple constructor for a clock that takes up two blocks
     *
     * @param tickSound   the tick sound supplier, use {@link ClockBlock#NO_SOUND} to skip
     * @param chimeSound  the chime sound supplier, use {@link ClockBlock#NO_SOUND} to skip
     * @param upperShape  the shape of the upper half in the default direction
     * @param lowerShape  the shape of the lower half in the default direction
     * @param blockEntity the block entity type supplier
     * @param pProperties the block properties
     */
    public TallClockBlock(@Nonnull Supplier<SoundEvent> tickSound, @Nonnull Supplier<SoundEvent> chimeSound,
                          VoxelShape upperShape, VoxelShape lowerShape,
                          @Nonnull Supplier<BlockEntityType<ClockBlockEntity>> blockEntity,
                          Properties pProperties) {
        super(pProperties, RotatingTallBlock.createShapeBuilder(upperShape, lowerShape));
        this.tickSound = tickSound;
        this.chimeSound = chimeSound;
        this.blockEntitySupplier = blockEntity;
    }

    /// / CHIME PROVIDER ////

    @Nullable
    @Override
    public SoundEvent getTickSound(BlockState blockState) {
        return this.tickSound.get();
    }

    @Nullable
    @Override
    public SoundEvent getChimeSound(BlockState blockState) {
        return this.chimeSound.get();
    }

    /// / BLOCK ENTITY ////

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        if (pState.getValue(HALF) == DoubleBlockHalf.UPPER) {
            return blockEntitySupplier.get().create(pPos, pState);
        }
        return null;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return !pLevel.isClientSide() ? (BlockEntityTicker<T>) (BlockEntityTicker<ClockBlockEntity>) (ClockBlockEntity::tick) : null;
    }
}
