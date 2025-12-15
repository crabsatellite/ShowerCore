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
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import tanukidecor.block.RotatingBlock;
import tanukidecor.block.entity.ClockBlockEntity;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

public class ClockBlock extends RotatingBlock implements EntityBlock, IChimeProvider {

    public static final Supplier<SoundEvent> NO_SOUND = () -> null;

    protected final Supplier<SoundEvent> tickSound;
    protected final Supplier<SoundEvent> chimeSound;
    protected final Supplier<BlockEntityType<ClockBlockEntity>> blockEntitySupplier;

    /**
     * Simple constructor for a clock that takes up one block
     *
     * @param tickSound   the tick sound supplier, use {@link ClockBlock#NO_SOUND} to skip
     * @param chimeSound  the chime sound supplier, use {@link ClockBlock#NO_SOUND} to skip
     * @param shape       the clock shape in the default direction
     * @param blockEntity the block entity type supplier
     * @param pProperties the block properties
     */
    public ClockBlock(@Nonnull Supplier<SoundEvent> tickSound, @Nonnull Supplier<SoundEvent> chimeSound,
                      VoxelShape shape, @Nonnull Supplier<BlockEntityType<ClockBlockEntity>> blockEntity,
                      Properties pProperties) {
        super(pProperties, RotatingBlock.createShapeBuilder(shape));
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
        return blockEntitySupplier.get().create(pPos, pState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return !pLevel.isClientSide() ? (BlockEntityTicker<T>) (BlockEntityTicker<ClockBlockEntity>) (ClockBlockEntity::tick) : null;
    }
}
