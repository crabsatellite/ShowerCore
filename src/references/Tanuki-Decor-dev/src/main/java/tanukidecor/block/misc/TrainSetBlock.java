/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.misc;

import com.google.common.collect.Iterables;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import tanukidecor.TDRegistry;
import tanukidecor.block.RotatingMultiblock;
import tanukidecor.block.entity.TrainSetBlockEntity;
import tanukidecor.util.MultiblockHandler;

import java.util.function.Consumer;


public class TrainSetBlock extends RotatingMultiblock implements EntityBlock {

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    private static final Vec3i DEFAULT_INDEX = new Vec3i(0, 0, -1);

    private static final MultiblockHandler HOLLOW_MULTIBLOCK_HANDLER = new MultiblockHandler(3, 1, 3) {
        @Override
        public Iterable<BlockPos> getPositions(final BlockPos center, final Direction facing) {
            return Iterables.filter(super.getPositions(center, facing), p -> !center.equals(p));
        }

        @Override
        public void iterateIndices(final Consumer<Vec3i> consumer) {
            // iterate index values in each axis
            final BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
            for (int x = minIndex.getX(); x <= maxIndex.getX(); x++) {
                for (int y = minIndex.getY(); y <= maxIndex.getY(); y++) {
                    for (int z = minIndex.getZ(); z <= maxIndex.getZ(); z++) {
                        if (x == 0 && y == 0 && z == 0) continue;
                        consumer.accept(mutable.set(x, y, z));
                    }
                }
            }
        }

        @Override
        public BlockState getStateForPlacement(final BlockPlaceContext context, final BlockState blockState, final Direction facing) {
            final Level level = context.getLevel();
            // determine center
            final BlockPos center = getCenterPos(context.getClickedPos(), blockState, facing);
            // validate blocks can be placed
            if (!allPositions(center, facing, p -> level.isInWorldBounds(p) && level.getBlockState(p).canBeReplaced(context))) {
                return null;
            }
            // place block
            return getIndexedState(blockState, DEFAULT_INDEX);
        }

        @Override
        public void preventCreativeDropFromCenterPart(final Level level, final BlockPos pos, final BlockState blockState, final Direction facing, final Player player) {
            final BlockPos origin = getCenterPos(pos, blockState, facing).offset(MultiblockHandler.indexToOffset(DEFAULT_INDEX, facing));
            final BlockState originState = level.getBlockState(origin);
            if (originState.is(blockState.getBlock()) && getIndex(originState).equals(DEFAULT_INDEX)) {
                level.setBlock(origin, originState.getFluidState().createLegacyBlock(), Block.UPDATE_SUPPRESS_DROPS | Block.UPDATE_ALL);
            }
        }
    };

    public TrainSetBlock(Properties pProperties) {
        super(HOLLOW_MULTIBLOCK_HANDLER, RotatingMultiblock.createMultiblockShapeBuilder(HOLLOW_MULTIBLOCK_HANDLER, SHAPE), pProperties);
        this.registerDefaultState(this.multiblockHandler.getIndexedState(this.stateDefinition.any(), DEFAULT_INDEX)
                .setValue(WATERLOGGED, false)
                .setValue(FACING, Direction.NORTH));
    }

    /// / METHODS ////

    @Override
    public void neighborChanged(BlockState pState, Level pLevel, BlockPos pPos, Block pBlock, BlockPos pFromPos, boolean pIsMoving) {
        if (!pLevel.isClientSide() && pFromPos.getY() == pPos.getY() - 1) {
            // Locate the block entity
            Direction facing = pState.getValue(TrainSetBlock.FACING);
            MultiblockHandler multiblockHandler = this.getMultiblockHandler();
            BlockPos centerPos = multiblockHandler.getCenterPos(pPos, pState, facing);
            BlockPos entityPos = centerPos.offset(DEFAULT_INDEX);
            // Silence the block entity when there is wool underneath any of its blocks
            if (pLevel.getBlockEntity(entityPos) instanceof TrainSetBlockEntity blockEntity) {
                boolean silent = multiblockHandler.anyPositions(centerPos, facing, b -> pLevel.getBlockState(b.below()).is(BlockTags.OCCLUDES_VIBRATION_SIGNALS));
                blockEntity.setSilent(silent);
            }
        }
        super.neighborChanged(pState, pLevel, pPos, pBlock, pFromPos, pIsMoving);
    }

    /// / BLOCK ENTITY ////

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        if (getMultiblockHandler().getIndex(pState).equals(DEFAULT_INDEX)) {
            return TDRegistry.BlockEntityReg.TRAIN_SET.get().create(pPos, pState);
        }
        return null;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return !pLevel.isClientSide() ? (BlockEntityTicker<T>) (BlockEntityTicker<TrainSetBlockEntity>) (TrainSetBlockEntity::tick) : null;
    }

    //// SHAPE ////

    /**
     * Shape data for each block in the default horizontal direction, ordered by index {@code [height][width][depth]}
     **/
    public static final VoxelShape[][][] SHAPE = new VoxelShape[][][]{
            // height = 0
            {
                    // width = 0
                    {
                            Shapes.or(box(0, 0, 5, 5, 2, 16),
                                    box(5, 0, 10, 10, 2, 16)),
                            box(5, 0, 0, 10, 2, 16),
                            Shapes.or(box(0, 0, 0, 5, 2, 10),
                                    box(5, 0, 0, 10, 2, 5))
                    },
                    // width = 1
                    {
                            box(0, 0, 5, 16, 2, 10),
                            Shapes.empty(),
                            box(0, 0, 5, 16, 2, 10)
                    },
                    // width = 2
                    {
                            Shapes.or(box(10, 0, 5, 16, 2, 16),
                                    box(5, 0, 11, 11, 2, 16)),
                            box(5, 0, 0, 10, 2, 16),
                            Shapes.or(box(5, 0, 0, 16, 2, 5),
                                    box(11, 0, 5, 16, 2, 10))
                    }
            },
            // height = 1
            {},
            // height = 2
            {}
    };
}
