/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import tanukidecor.TDRegistry;
import tanukidecor.block.RotatingBlock;
import tanukidecor.block.clock.IChimeProvider;
import tanukidecor.block.entity.NewtonsCradleBlockEntity;

import java.util.function.Supplier;

public class NewtonsCradleBlock extends RotatingBlock implements EntityBlock, IChimeProvider {

    public static final BooleanProperty ENABLED = BlockStateProperties.ENABLED;

    protected final Supplier<SoundEvent> tickSound;

    public static final VoxelShape SHAPE = box(2, 0, 4, 14, 8, 12);

    public NewtonsCradleBlock(Properties pProperties) {
        super(pProperties, RotatingBlock.createShapeBuilder(SHAPE));
        this.tickSound = TDRegistry.SoundReg.METRONOME_TICK;
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(ENABLED, true)
                .setValue(WATERLOGGED, false));
    }

    /// / METHODS ////

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder.add(ENABLED));
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, BlockHitResult pHitResult) {
        if (pLevel.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        if (!pPlayer.isShiftKeyDown()) {
            pLevel.setBlock(pPos, pState.cycle(ENABLED), Block.UPDATE_ALL);
            pLevel.updateNeighbourForOutputSignal(pPos, pState.getBlock());
            return InteractionResult.SUCCESS;
        }
        return super.useWithoutItem(pState, pLevel, pPos, pPlayer, pHitResult);
    }

    @Override
    public void neighborChanged(BlockState pState, Level pLevel, BlockPos pPos, Block pBlock, BlockPos pFromPos, boolean pIsMoving) {
        if (!pLevel.isClientSide() && pFromPos.getY() == pPos.getY() - 1 && pLevel.getBlockEntity(pPos) instanceof NewtonsCradleBlockEntity blockEntity) {
            blockEntity.setSilent(pLevel.getBlockState(pPos.below()).is(BlockTags.OCCLUDES_VIBRATION_SIGNALS));
        }
        super.neighborChanged(pState, pLevel, pPos, pBlock, pFromPos, pIsMoving);
    }

    /// / CHIME PROVIDER ////

    @Nullable
    @Override
    public SoundEvent getTickSound(BlockState blockState) {
        if (blockState.getValue(ENABLED)) {
            return tickSound.get();
        }
        return null;
    }

    @Override
    public int getTickSoundInterval(BlockState blockState) {
        return blockState.getValue(ENABLED) ? 10 : 0;
    }

    @Override
    public float getTickVolume(BlockState blockState, RandomSource random, long dayTime) {
        return 0.125F;
    }

    @Override
    public float getTickPitch(BlockState blockState, RandomSource random, long dayTime) {
        return 1.3F + random.nextFloat() * 0.15F;
    }

    @Override
    public boolean isTimeToChime(BlockState blockState, long dayTime) {
        return false;
    }

    /// / BLOCK ENTITY ////

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return TDRegistry.BlockEntityReg.NEWTONS_CRADLE.get().create(pPos, pState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return (BlockEntityTicker<T>) (BlockEntityTicker<NewtonsCradleBlockEntity>) (NewtonsCradleBlockEntity::tick);
    }

    /// / REDSTONE ////

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        return state.getValue(ENABLED) ? 15 : 0;
    }
}
