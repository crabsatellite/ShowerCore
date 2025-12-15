/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.material.Fluids;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

@Immutable
public class MultiblockHandler {

    public static final Direction ORIGIN_DIRECTION = Direction.NORTH;
    public static final Vec3i CENTER_INDEX = Vec3i.ZERO;

    protected static final String WIDTH = "width";
    protected static final String HEIGHT = "height";
    protected static final String DEPTH = "depth";

    /* The X index property */
    protected static final IntegerProperty WIDTH_1_2 = IntegerProperty.create(WIDTH, 1, 2);
    protected static final IntegerProperty WIDTH_0_2 = IntegerProperty.create(WIDTH, 0, 2);
    /* The Y index property */
    protected static final IntegerProperty HEIGHT_1_2 = IntegerProperty.create(HEIGHT, 1, 2);
    protected static final IntegerProperty HEIGHT_0_2 = IntegerProperty.create(HEIGHT, 0, 2);
    /* The Z index property */
    protected static final IntegerProperty DEPTH_1_2 = IntegerProperty.create(DEPTH, 1, 2);
    protected static final IntegerProperty DEPTH_0_2 = IntegerProperty.create(DEPTH, 0, 2);

    protected static final IntegerProperty[] WIDTH_BY_MAX_VALUE = new IntegerProperty[]{null, WIDTH_1_2, WIDTH_0_2};
    protected static final IntegerProperty[] HEIGHT_BY_MAX_VALUE = new IntegerProperty[]{null, HEIGHT_1_2, HEIGHT_0_2};
    protected static final IntegerProperty[] DEPTH_BY_MAX_VALUE = new IntegerProperty[]{null, DEPTH_1_2, DEPTH_0_2};

    public static final MultiblockHandler MULTIBLOCK_3X3X3 = new MultiblockHandler(3, 3, 3);
    public static final MultiblockHandler MULTIBLOCK_3X3X1 = new MultiblockHandler(3, 3, 1);
    public static final MultiblockHandler MULTIBLOCK_3X2X1 = new MultiblockHandler(3, 2, 1);
    public static final MultiblockHandler MULTIBLOCK_3X1X1 = new MultiblockHandler(3, 1, 1);
    public static final MultiblockHandler MULTIBLOCK_3X1X2 = new MultiblockHandler(3, 1, 2);
    public static final MultiblockHandler MULTIBLOCK_3X1X3 = new MultiblockHandler(3, 1, 3);
    public static final MultiblockHandler MULTIBLOCK_2X3X1 = new MultiblockHandler(2, 3, 1);
    public static final MultiblockHandler MULTIBLOCK_2X2X2 = new MultiblockHandler(2, 2, 2);
    public static final MultiblockHandler MULTIBLOCK_2X2X1 = new MultiblockHandler(2, 2, 1);
    public static final MultiblockHandler MULTIBLOCK_2X1X2 = new MultiblockHandler(2, 1, 2);
    public static final MultiblockHandler MULTIBLOCK_2X1X1 = new MultiblockHandler(2, 1, 1);
    public static final MultiblockHandler MULTIBLOCK_1X3X1 = new MultiblockHandler(1, 3, 1);
    public static final MultiblockHandler MULTIBLOCK_1X1X2 = new MultiblockHandler(1, 1, 2);

    protected final @Nullable IntegerProperty widthProperty;
    protected final @Nullable IntegerProperty heightProperty;
    protected final @Nullable IntegerProperty depthProperty;

    protected final Vec3i dimensions;
    protected final Vec3i minIndex;
    protected final Vec3i maxIndex;
    protected final Map<Direction, BoundingBox> bounds;

    public MultiblockHandler(final int width, final int height, final int depth) {
        // validate dimensions
        if (width < 1 || height < 1 || depth < 1) {
            throw new IllegalArgumentException(String.format("[MultiblockHandler] width, height, and depth must be greater than zero! Provided [{}, {}, {}]", width, height, depth));
        }
        this.dimensions = new Vec3i(width, height, depth);
        this.minIndex = new Vec3i(-(width - 1) / 2, -(height - 1) / 2, -(depth - 1) / 2);
        this.maxIndex = new Vec3i(width / 2, height / 2, depth / 2);
        this.widthProperty = getWidthProperty(width);
        this.heightProperty = getHeightProperty(height);
        this.depthProperty = getDepthProperty(depth);
        final BoundingBox boundingBox = BoundingBox.fromCorners(minIndex, maxIndex);
        this.bounds = createRotatedBoundingBoxMap(boundingBox, ORIGIN_DIRECTION);
    }

    //// GETTERS ////

    /**
     * @return the width integer property, if any
     */
    public @Nullable IntegerProperty getWidthProperty() {
        return widthProperty;
    }

    /**
     * @return the height integer property, if any
     */
    public @Nullable IntegerProperty getHeightProperty() {
        return heightProperty;
    }

    /**
     * @return the depth integer property, if any
     */
    public @Nullable IntegerProperty getDepthProperty() {
        return depthProperty;
    }

    /**
     * @return a copy of the number of blocks in each axis
     */
    public Vec3i getDimensions() {
        return new Vec3i(this.dimensions.getX(), this.dimensions.getY(), this.dimensions.getZ());
    }

    /**
     * @return a copy of the index values at the minimum point of the multiblock in each axis
     */
    public Vec3i getMinIndex() {
        return new Vec3i(this.minIndex.getX(), this.minIndex.getY(), this.minIndex.getZ());
    }

    /**
     * @return a copy of the index values at the maximum point of the multiblock in each axis
     */
    public Vec3i getMaxIndex() {
        return new Vec3i(this.maxIndex.getX(), this.maxIndex.getY(), this.maxIndex.getZ());
    }

    /**
     * @param direction the facing direction
     * @return the bounding box for the given direction
     */
    public BoundingBox getBounds(final Direction direction) {
        return bounds.get(direction);
    }

    //// HELPER METHODS ////

    /**
     * @param pos        the block position
     * @param blockState the block state of a multiblock part
     * @param direction  the facing direction
     * @return the block position of the center of the multiblock
     * @see #getIndex(BlockState)
     * @see #getCenterPos(BlockPos, BlockState, Direction)
     */
    public BlockPos getCenterPos(final BlockPos pos, final BlockState blockState, final Direction direction) {
        return getCenterPos(pos, getIndex(blockState), direction);
    }

    /**
     * @param center    the center position
     * @param direction the facing direction
     * @return the minimum position in the multiblock
     */
    public BlockPos getMin(final BlockPos center, final Direction direction) {
        return center.offset(MultiblockHandler.indexToOffset(minIndex, direction));
    }

    /**
     * @param center    the center position
     * @param direction the facing direction
     * @return the maximum position in the multiblock
     */
    public BlockPos getMax(final BlockPos center, final Direction direction) {
        return center.offset(MultiblockHandler.indexToOffset(maxIndex, direction));
    }

    /**
     * @param center the block position of the center of the multiblock
     * @param facing the facing direction
     * @return an iterable containing block positions for all blocks in th multiblock
     */
    public Iterable<BlockPos> getPositions(final BlockPos center, final Direction facing) {
        final BlockPos min = getMin(center, facing);
        final BlockPos max = getMax(center, facing);
        return BlockPos.betweenClosed(min, max);
    }

    /// / PROPERTY HELPER METHODS ////

    public static @Nullable IntegerProperty getWidthProperty(final int maxWidth) {
        final int index = Mth.clamp(maxWidth - 1, 0, WIDTH_BY_MAX_VALUE.length - 1);
        return WIDTH_BY_MAX_VALUE[index];
    }

    public static @Nullable IntegerProperty getHeightProperty(final int maxHeight) {
        final int index = Mth.clamp(maxHeight - 1, 0, HEIGHT_BY_MAX_VALUE.length - 1);
        return HEIGHT_BY_MAX_VALUE[index];
    }

    public static @Nullable IntegerProperty getDepthProperty(final int maxDepth) {
        final int index = Mth.clamp(maxDepth - 1, 0, DEPTH_BY_MAX_VALUE.length - 1);
        return DEPTH_BY_MAX_VALUE[index];
    }

    /**
     * @param blockState a block state
     * @return true if the index for the given block state is the center
     */
    public boolean isCenterState(final BlockState blockState) {
        return getIndex(blockState).equals(CENTER_INDEX);
    }

    public StateDefinition.Builder<Block, BlockState> createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        if (widthProperty != null) {
            builder.add(widthProperty);
        }
        if (heightProperty != null) {
            builder.add(heightProperty);
        }
        if (depthProperty != null) {
            builder.add(depthProperty);
        }
        return builder;
    }

    /**
     * @param blockState the base block state
     * @return a block state with values for the center of the multiblock
     * @see #getIndexedState(BlockState, Vec3i)
     */
    public BlockState getCenterState(BlockState blockState) {
        return getIndexedState(blockState, CENTER_INDEX);
    }

    /**
     * @param blockState the block state of a multiblock part
     * @return the [width, height, depth] values of the block state, each in the range [-1,1]
     */
    public Vec3i getIndex(final BlockState blockState) {
        final int width = widthProperty != null ? (blockState.getValue(widthProperty) - dimensions.getX() / 2) : 0;
        final int height = heightProperty != null ? (blockState.getValue(heightProperty) - dimensions.getY() / 2) : 0;
        final int depth = depthProperty != null ? (blockState.getValue(depthProperty) - dimensions.getZ() / 2) : 0;
        return new Vec3i(width, height, depth);
    }

    /**
     * @param blockState the base block state
     * @param index      the desired index
     * @return a block state with values for the block at the given index of the multiblock
     */
    public BlockState getIndexedState(final BlockState blockState, final Vec3i index) {
        BlockState mutableBlockState = blockState;
        if (widthProperty != null) {
            mutableBlockState = mutableBlockState.setValue(widthProperty, index.getX() + dimensions.getX() / 2);
        }
        if (heightProperty != null) {
            mutableBlockState = mutableBlockState.setValue(heightProperty, index.getY() + dimensions.getY() / 2);
        }
        if (depthProperty != null) {
            mutableBlockState = mutableBlockState.setValue(depthProperty, index.getZ() + dimensions.getZ() / 2);
        }
        return mutableBlockState;
    }


    //// PLACEMENT HELPER METHODS ////

    /**
     * Places all blocks in the multiblock
     *
     * @param level      the level
     * @param pos        the block position
     * @param blockState the default block state
     * @param direction  the facing direction
     */
    public void onBlockPlaced(Level level, BlockPos pos, BlockState blockState, Direction direction) {
        // determine center
        final BlockPos center = getCenterPos(pos, blockState, direction);
        // place multiblock
        iterateIndices(index -> {
            // skip center block
            if (index.equals(CENTER_INDEX)) return;
            // calculate block position
            BlockPos p = center.offset(indexToOffset(index, direction));
            // determine block to place
            boolean waterlogged = level.getFluidState(p).getType() == Fluids.WATER;
            final BlockState state = getIndexedState(blockState.setValue(BlockStateProperties.WATERLOGGED, waterlogged), index);
            // place block
            level.setBlock(p, state, Block.UPDATE_ALL);
        });
    }

    /**
     * Checks and validates if the multiblock can be placed with the given context.
     *
     * @param context    the block place context containing the center position of the multiblock
     * @param blockState the base block state
     * @param facing     the facing direction
     * @return the block state for this position, can be null
     */
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context, BlockState blockState, Direction facing) {
        final Level level = context.getLevel();
        // determine center
        final BlockPos center = context.getClickedPos();
        // validate blocks can be placed
        if (!allPositions(center, facing, p -> level.isInWorldBounds(p) && level.getBlockState(p).canBeReplaced(context))) {
            return null;
        }
        // place block
        return getCenterState(blockState);
    }

    /**
     * @param blockState the block state
     * @param level      the level
     * @param pos        the block position
     * @param facing     the facing direction
     * @return true if all positions in the multiblock area have the same block as the given blockstate
     */
    public boolean canSurvive(final BlockState blockState, final LevelReader level, final BlockPos pos, final Direction facing) {
        return allPositions(getCenterPos(pos, blockState, facing), facing, p -> level.getBlockState(p).is(blockState.getBlock()));
    }

    /**
     * Removes the center block without allowing loot drops. This prevents the item from dropping, intended for use in creative mode.
     *
     * @param level      the level
     * @param pos        the block position
     * @param blockState the block state
     * @param facing     the facing direction
     * @param player     the player
     */
    public void preventCreativeDropFromCenterPart(Level level, BlockPos pos, BlockState blockState, Direction facing, Player player) {
        final BlockPos origin = getCenterPos(pos, blockState, facing);
        final BlockState originState = level.getBlockState(origin);
        if (originState.is(blockState.getBlock()) && getIndex(originState).equals(CENTER_INDEX)) {
            level.setBlock(origin, originState.getFluidState().createLegacyBlock(), Block.UPDATE_SUPPRESS_DROPS | Block.UPDATE_ALL);
            //pLevel.levelEvent(pPlayer, LevelEvent.PARTICLES_DESTROY_BLOCK, center, Block.getId(blockState));
        }
    }


    //// POSITION HELPER METHODS ////

    /**
     * @param center    the block position of the center of the multiblock
     * @param facing    the facing direction
     * @param predicate a predicate to test
     * @return true if all positions passed.
     */
    public boolean allPositions(final BlockPos center, final Direction facing, final Predicate<BlockPos> predicate) {
        for (BlockPos p : getPositions(center, facing)) {
            if (!predicate.test(p)) {
                return false;
            }
        }
        return true;
    }

    /**
     * @param center    the block position of the center of the multiblock
     * @param facing    the facing direction
     * @param predicate a predicate to test
     * @return true if any position passed.
     */
    public boolean anyPositions(final BlockPos center, final Direction facing, final Predicate<BlockPos> predicate) {
        for (BlockPos p : getPositions(center, facing)) {
            if (predicate.test(p)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Iterates all index values in each axis
     *
     * @param consumer the consumer to handle each index value
     */
    public void iterateIndices(final Consumer<Vec3i> consumer) {
        // iterate index values in each axis
        final BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        for (int x = minIndex.getX(); x <= maxIndex.getX(); x++) {
            for (int y = minIndex.getY(); y <= maxIndex.getY(); y++) {
                for (int z = minIndex.getZ(); z <= maxIndex.getZ(); z++) {
                    consumer.accept(mutable.set(x, y, z));
                }
            }
        }
    }

    /**
     * @param boundingBox
     * @param from
     * @return a map containing bounding boxes for each horizontal direction
     */
    protected static Map<Direction, BoundingBox> createRotatedBoundingBoxMap(final BoundingBox boundingBox, final Direction from) {
        final Map<Direction, BoundingBox> map = new EnumMap<>(Direction.class);
        map.put(from, boundingBox);
        BoundingBox box = boundingBox;
        for (int i = 0; i < 3; i++) {
            Direction direction = Direction.from2DDataValue(from.get2DDataValue() + i + 1);
            box = new BoundingBox(1 - box.maxZ(), box.minY(), box.minX(), 1 - box.minZ(), box.maxY(), box.maxX());
            map.put(direction, box);
        }
        return map;
    }

    /**
     * @param pos       the block position
     * @param index     the index of the block at this position
     * @param direction the direction
     * @return the block position of a block with the given index
     */
    public static BlockPos getCenterPos(final BlockPos pos, final Vec3i index, final Direction direction) {
        // calculate block position offset using the given index
        final Vec3i offset = indexToOffset(index, direction);
        // calculate the center block position
        return pos.subtract(offset);
    }

    /**
     * @param index     the relative position index
     * @param direction the direction to rotate
     * @return the absolute offset calculated from the given index and direction
     */
    public static Vec3i indexToOffset(final Vec3i index, final Direction direction) {
        switch (direction) {
            default:
            case NORTH:
                return new Vec3i(-index.getX(), index.getY(), index.getZ());
            case EAST:
                return new Vec3i(-index.getZ(), index.getY(), -index.getX());
            case SOUTH:
                return new Vec3i(index.getX(), index.getY(), -index.getZ());
            case WEST:
                return new Vec3i(index.getZ(), index.getY(), index.getX());
        }
    }
}
