/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import tanukidecor.block.storage.IDelegateProvider;
import tanukidecor.util.MultiblockHandler;
import tanukidecor.util.ShapeBuilder;
import tanukidecor.util.ShapeUtils;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Handles waterloggable, horizontally directional, variable size multiblocks
 */
public class RotatingMultiblock extends Block implements SimpleWaterloggedBlock, IDelegateProvider {

    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    protected static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    /**
     * ThreadLocal to pass multiblockHandler to createBlockStateDefinition during construction
     */
    private static final ThreadLocal<MultiblockHandler> CONSTRUCTING_HANDLER = new ThreadLocal<>();

    protected final MultiblockHandler multiblockHandler;

    protected final Map<BlockState, VoxelShape> blockShapes = new HashMap<>();
    protected final Map<BlockState, VoxelShape> multiblockShapes = new HashMap<>();

    private final ShapeBuilder shapeBuilder;

    protected RotatingMultiblock(MultiblockHandler multiblockHandler,
                                 ShapeBuilder shapeBuilder,
                                 Properties pProperties) {
        super(withHandler(multiblockHandler, pProperties).dynamicShape());
        this.multiblockHandler = multiblockHandler;
        this.shapeBuilder = shapeBuilder;
        // Clean up ThreadLocal
        CONSTRUCTING_HANDLER.remove();
        // Note: state definition now has all properties including multiblock ones
        // Set the default state with center position values
        BlockState defaultState = this.stateDefinition.any();
        // Explicitly set the base properties to ensure they have the correct defaults
        if (defaultState.hasProperty(WATERLOGGED)) {
            defaultState = defaultState.setValue(WATERLOGGED, false);
        }
        if (defaultState.hasProperty(FACING)) {
            defaultState = defaultState.setValue(FACING, Direction.NORTH);
        }
        // Apply multiblock center properties
        this.registerDefaultState(this.multiblockHandler.getCenterState(defaultState));
        // calculate voxel shapes for all possible states
        this.precalculateShapes();
    }

    private static Properties withHandler(MultiblockHandler handler, Properties properties) {
        CONSTRUCTING_HANDLER.set(handler);
        return properties;
    }

    public MultiblockHandler getMultiblockHandler() {
        return multiblockHandler;
    }

    /// / DELEGATE PROVIDER ////

    @Override
    public BlockPos getDelegatePos(BlockState blockState, BlockPos blockPos) {
        return multiblockHandler.getCenterPos(blockPos, blockState, blockState.getValue(FACING));
    }

    //// STATE PROPERTIES ////

    /**
     * @param pBuilder the state definition builder
     */
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(WATERLOGGED).add(FACING);
        // Use ThreadLocal to get the handler during construction, or use the instance field after construction
        MultiblockHandler handler = this.multiblockHandler != null ? this.multiblockHandler : CONSTRUCTING_HANDLER.get();
        if (handler != null) {
            handler.createBlockStateDefinition(pBuilder);
        }
    }

    protected void createMultiblockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(WATERLOGGED).add(FACING);
        if (this.multiblockHandler != null) {
            this.multiblockHandler.createBlockStateDefinition(pBuilder);
        }
    }

    /// / PLACEMENT ////

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        /*
         * The BlockPlaceContext is adjusted in the block item to contain the center position
         */
        final Direction direction = pContext.getHorizontalDirection().getOpposite();
        // create base block state
        final boolean waterlogged = pContext.getLevel().getFluidState(pContext.getClickedPos()).getType() == Fluids.WATER;
        final BlockState blockState = this.defaultBlockState()
                .setValue(FACING, direction)
                .setValue(WATERLOGGED, waterlogged);
        // defer to multiblock handler
        return multiblockHandler.getStateForPlacement(pContext, blockState, direction);
    }

    @Override
    public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
        // update waterlogged
        if (pState.getValue(WATERLOGGED)) {
            pLevel.scheduleTick(pCurrentPos, Fluids.WATER, Fluids.WATER.getTickDelay(pLevel));
        }
        // validate block can stay
        if (!multiblockHandler.canSurvive(pState, pLevel, pCurrentPos, pState.getValue(FACING))) {
            return getFluidState(pState).createLegacyBlock();
        }
        return super.updateShape(pState, pFacing, pFacingState, pLevel, pCurrentPos, pFacingPos);
    }

    @Override
    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, LivingEntity pPlacer, ItemStack pStack) {
        super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
        multiblockHandler.onBlockPlaced(pLevel, pPos, pState, pState.getValue(FACING));
    }

    @Override
    public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        // assume if the block at the given position is not this one, this is a preemptive check
        if (!pLevel.getBlockState(pPos).is(this)) {
            return true;
        }
        // validate the multiblock is intact
        return multiblockHandler.canSurvive(pState, pLevel, pPos, pState.getValue(FACING));
    }

    @Override
    public BlockState playerWillDestroy(Level pLevel, BlockPos pPos, BlockState pState, Player pPlayer) {
        if (!pLevel.isClientSide() && pPlayer.isCreative()) {
            multiblockHandler.preventCreativeDropFromCenterPart(pLevel, pPos, pState, pState.getValue(FACING), pPlayer);
        }
        return super.playerWillDestroy(pLevel, pPos, pState, pPlayer);
    }

    @Override
    public PushReaction getPistonPushReaction(BlockState pState) {
        return PushReaction.BLOCK;
    }

    // TODO custom #rotation and #mirror implementations

    public void removeAll(final Level level, final BlockPos centerPos) {
        final BlockPos.MutableBlockPos mutablePos = centerPos.mutable();
        getMultiblockHandler().iterateIndices(index -> {
            level.removeBlock(mutablePos.setWithOffset(centerPos, index), false);
        });
    }

    /// / FLUID ////

    @Override
    public FluidState getFluidState(BlockState pState) {
        return pState.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(pState);
    }

    /// / SHAPE ////

    @Override
    public VoxelShape getCollisionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return this.hasCollision ? getBlockShape(pState) : Shapes.empty();
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return getMultiblockShape(pState);
    }

    protected void precalculateShapes() {
        blockShapes.clear();
        multiblockShapes.clear();
        // calculate centered visual shapes
        final Map<Direction, VoxelShape> centeredVisualShapes = new EnumMap<>(Direction.class);
        centeredVisualShapes.putAll(ShapeUtils.rotateShapes(MultiblockHandler.ORIGIN_DIRECTION, createMultiblockShape()));
        // iterate all block states
        for (BlockState blockState : this.stateDefinition.getPossibleStates()) {
            // cache the individual shape
            blockShapes.put(blockState, this.shapeBuilder.apply(blockState));
            // move the centered shape for the given rotation to the correct offset
            Direction direction = blockState.getValue(FACING);
            Vec3i index = multiblockHandler.getIndex(blockState);
            Vec3i offset = MultiblockHandler.indexToOffset(index, direction);
            VoxelShape shape = centeredVisualShapes.get(blockState.getValue(FACING))
                    .move(-offset.getX(), -offset.getY(), -offset.getZ());
            // cache the offset visual shape
            multiblockShapes.put(blockState, shape);
        }
    }

    /**
     * @return a newly created multiblock {@link VoxelShape}, centered around the center block
     */
    protected VoxelShape createMultiblockShape() {
        final BlockState blockState = multiblockHandler.getCenterState(defaultBlockState());
        final AtomicReference<VoxelShape> shape = new AtomicReference<>(Shapes.empty());
        multiblockHandler.iterateIndices(index -> {
            BlockState b = multiblockHandler.getIndexedState(blockState, index);
            shape.set(ShapeUtils.orUnoptimized(shape.get(), blockShapes.computeIfAbsent(b, this.shapeBuilder)
                    .move(-index.getX(), index.getY(), index.getZ())
            ));
        });
        return shape.get().optimize();
    }

    /**
     * @param blockState the block state
     * @return the cached shape for the given block state
     */
    public VoxelShape getBlockShape(final BlockState blockState) {
        return blockShapes.get(blockState);
    }

    /**
     * @param blockState the block state
     * @return the cached multiblock shape for the given block state
     */
    public VoxelShape getMultiblockShape(final BlockState blockState) {
        return multiblockShapes.get(blockState);
    }

    //// SHAPE HELPER METHODS ////

    /**
     * @param handler  the multiblock handler
     * @param template the array of voxel shapes ordered by {@code [height][width][depth]}
     * @return a shape builder for the given handler that uses the {@link #FACING} property to rotate shapes
     */
    public static ShapeBuilder createMultiblockShapeBuilder(final MultiblockHandler handler, final VoxelShape[][][] template) {
        return blockState -> {
            final Vec3i index = handler.getIndex(blockState);
            final Vec3i dimensions = handler.getDimensions();
            final Direction facing = blockState.getValue(FACING);
            int heightIndex = (index.getY() + dimensions.getY() / 2);
            int widthIndex = (index.getX() + dimensions.getX() / 2);
            int depthIndex = (index.getZ() + dimensions.getZ() / 2);
            final VoxelShape shape = template
                    [heightIndex]
                    [widthIndex]
                    [depthIndex];
            return ShapeUtils.rotateShape(MultiblockHandler.ORIGIN_DIRECTION, facing, shape);
        };
    }
}
