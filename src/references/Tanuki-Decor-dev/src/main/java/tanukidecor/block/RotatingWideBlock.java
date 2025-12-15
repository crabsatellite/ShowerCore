/**
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 **/

package tanukidecor.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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

public class RotatingWideBlock extends Block implements SimpleWaterloggedBlock, IDelegateProvider {

    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final EnumProperty<Side> SIDE = EnumProperty.create("side", Side.class);
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    protected final ShapeBuilder shapeBuilder;
    protected final Map<BlockState, VoxelShape> blockShapes = new HashMap<>();
    protected final Map<BlockState, VoxelShape> multiblockShapes = new HashMap<>();

    public RotatingWideBlock(Properties pProperties, ShapeBuilder shapeBuilder) {
        super(pProperties);
        this.shapeBuilder = shapeBuilder;
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(WATERLOGGED, false)
                .setValue(FACING, Direction.NORTH)
                .setValue(SIDE, Side.LEFT));
        precalculateShapes();
    }

    /// / SHAPE ////

    protected void precalculateShapes() {
        blockShapes.clear();
        multiblockShapes.clear();
        // create double block shape
        final Map<Direction, VoxelShape> doubleBlockShapes = new EnumMap<>(Direction.class);
        doubleBlockShapes.putAll(ShapeUtils.rotateShapes(MultiblockHandler.ORIGIN_DIRECTION, createDoubleBlockShape()));
        // create shapes for all possible block states
        for (BlockState blockState : this.stateDefinition.getPossibleStates()) {
            // cache the individual shape
            blockShapes.put(blockState, this.shapeBuilder.apply(blockState));
            // calculate multiblock shape
            Direction facing = blockState.getValue(FACING);
            Direction direction = facing.getClockWise();
            double offset = blockState.getValue(SIDE) == Side.LEFT ? 0 : 1;
            VoxelShape shape = doubleBlockShapes.get(facing).move(offset * direction.getStepX(), 0, offset * direction.getStepZ());
            multiblockShapes.put(blockState, shape);
        }
    }

    /**
     * @return a newly created multiblock {@link VoxelShape}, centered around the center block
     */
    protected VoxelShape createDoubleBlockShape() {
        BlockState leftState = defaultBlockState().setValue(SIDE, Side.LEFT);
        BlockState rightState = defaultBlockState().setValue(SIDE, Side.RIGHT);
        return Shapes.or(
                blockShapes.computeIfAbsent(leftState, this.shapeBuilder),
                blockShapes.computeIfAbsent(rightState, this.shapeBuilder).move(-1, 0, 0));

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

    @Override
    public VoxelShape getCollisionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return this.hasCollision ? getBlockShape(pState) : Shapes.empty();
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return getMultiblockShape(pState);
    }

    /// / DELEGATE PROVIDER ////

    @Override
    public BlockPos getDelegatePos(BlockState blockState, BlockPos blockPos) {
        return getLeftSide(blockState, blockPos);
    }

    /// / METHODS ////

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        BlockPos blockpos = pContext.getClickedPos();
        Level level = pContext.getLevel();
        FluidState fluidstate = pContext.getLevel().getFluidState(pContext.getClickedPos());
        boolean waterlogged = fluidstate.getType() == Fluids.WATER;
        Direction direction = pContext.getHorizontalDirection().getOpposite();
        BlockPos sidePos = blockpos.relative(direction.getCounterClockWise());
        if (level.getBlockState(sidePos).canBeReplaced(pContext)) {
            return this.defaultBlockState()
                    .setValue(FACING, direction)
                    .setValue(WATERLOGGED, waterlogged)
                    .setValue(SIDE, Side.LEFT);
        } else {
            return null;
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(WATERLOGGED).add(SIDE).add(FACING);
    }

    @Override
    public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
        // update fluid
        if (pState.getValue(WATERLOGGED)) {
            pLevel.scheduleTick(pCurrentPos, Fluids.WATER, Fluids.WATER.getTickDelay(pLevel));
        }
        final Side side = pState.getValue(SIDE);
        final BlockPos oppositePos = getOppositeSide(pState, pCurrentPos);
        final BlockState oppositeState = pLevel.getBlockState(oppositePos);
        // update half
        if (!oppositeState.is(this) || oppositeState.getValue(SIDE) == side || !pState.canSurvive(pLevel, pCurrentPos)) {
            return pState.getFluidState().createLegacyBlock();
        }
        return super.updateShape(pState, pFacing, pFacingState, pLevel, pCurrentPos, pFacingPos);
    }

    @Override
    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, LivingEntity pPlacer, ItemStack pStack) {
        BlockPos sidePos = getRightSide(pState, pPos);
        boolean waterlogged = pLevel.getFluidState(sidePos).getType() == Fluids.WATER;
        pLevel.setBlock(sidePos, pState.setValue(SIDE, Side.RIGHT).setValue(WATERLOGGED, waterlogged), Block.UPDATE_ALL);
    }

    @Override
    public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        // assume if the block at the given position is not this one, this is a preemptive check
        if (!pLevel.getBlockState(pPos).is(this)) {
            return true;
        }
        return pState.getValue(SIDE) == Side.LEFT || pLevel.getBlockState(getOppositeSide(pState, pPos)).is(this);
    }

    @Override
    public BlockState playerWillDestroy(Level pLevel, BlockPos pPos, BlockState pState, Player pPlayer) {
        if (!pLevel.isClientSide && pPlayer.isCreative()) {
            preventCreativeDropFromLeftPart(pLevel, pPos, pState, pPlayer);
        }
        return super.playerWillDestroy(pLevel, pPos, pState, pPlayer);
    }

    @Override
    public PushReaction getPistonPushReaction(BlockState pState) {
        return PushReaction.BLOCK;
    }

    // TODO custom #rotation and #mirror implementations

    /// / FLUID ////

    @Override
    public FluidState getFluidState(BlockState pState) {
        return pState.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(pState);
    }

    //// HELPER METHODS ////

    /**
     * @param shapeEast the shape for the east half
     * @param shapeWest the shape for the west half
     * @return a shape builder hardcoded to support {@link #SIDE} and {@link #FACING} properties
     */
    public static ShapeBuilder createShapeBuilder(final VoxelShape shapeEast, final VoxelShape shapeWest) {
        return blockState -> {
            final Direction facing = blockState.getValue(FACING);
            final Side side = blockState.getValue(SIDE);
            final VoxelShape shape = side == Side.LEFT ? shapeEast : shapeWest;
            return ShapeUtils.rotateShape(MultiblockHandler.ORIGIN_DIRECTION, facing, shape);
        };
    }

    /**
     * Removes the left block without allowing loot drops. This prevents the item from dropping, intended for use in creative mode.
     *
     * @param level      the level
     * @param pos        the block position
     * @param blockState the block state
     * @param player     the player
     */
    public void preventCreativeDropFromLeftPart(Level level, BlockPos pos, BlockState blockState, Player player) {
        final BlockPos origin = getLeftSide(blockState, pos);
        final BlockState originState = level.getBlockState(origin);
        if (originState.is(blockState.getBlock()) && originState.getValue(SIDE) == Side.LEFT) {
            level.setBlock(origin, originState.getFluidState().createLegacyBlock(), Block.UPDATE_SUPPRESS_DROPS | Block.UPDATE_ALL);
        }
    }

    public static BlockPos getLeftSide(final BlockState blockState, final BlockPos pos) {
        if (blockState.getValue(SIDE) == Side.LEFT) {
            return pos;
        }
        Direction facing = blockState.getValue(FACING);
        return pos.relative(facing.getClockWise());
    }

    public static BlockPos getRightSide(final BlockState blockState, final BlockPos pos) {
        if (blockState.getValue(SIDE) == Side.RIGHT) {
            return pos;
        }
        Direction facing = blockState.getValue(FACING);
        return pos.relative(facing.getCounterClockWise());
    }

    public static BlockPos getOppositeSide(final BlockState blockState, final BlockPos pos) {
        Direction facing = blockState.getValue(FACING);
        Direction direction = blockState.getValue(SIDE) == Side.LEFT ? facing.getCounterClockWise() : facing.getClockWise();
        return pos.relative(direction);
    }

}
