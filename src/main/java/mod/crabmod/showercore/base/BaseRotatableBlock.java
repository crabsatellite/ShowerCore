package mod.crabmod.showercore.base;

import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class BaseRotatableBlock extends Block {
  public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
  private final Map<Direction, VoxelShape> shapeCache;

  public BaseRotatableBlock(Properties properties) {
    super(properties);
    this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    this.shapeCache = createShapeMap(getBaseShape());
  }

  @Override
  public BlockState getStateForPlacement(BlockPlaceContext context) {
    return this.defaultBlockState()
        .setValue(FACING, context.getHorizontalDirection().getOpposite());
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    builder.add(FACING);
  }

  @Override
  public VoxelShape getShape(
      BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
    Direction direction = state.getValue(FACING);
    return shapeCache.getOrDefault(direction, shapeCache.get(Direction.NORTH));
  }

  // Method for subclasses to provide their base (NORTH) shape
  protected abstract VoxelShape getBaseShape();

  // Abstract method for creating shape map; to be implemented by subclasses
  protected abstract Map<Direction, VoxelShape> createShapeMap(VoxelShape baseShape);
}
