package mod.crabmod.showercore.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.RandomSource;
import com.crabmod.hotbath.registers.ParticleRegister;

public class BathtubBlock extends HorizontalDirectionalBlock {
  public static final EnumProperty<BedPart> PART = BlockStateProperties.BED_PART;

  // Shapes for FOOT part (The part you place)
  protected static final VoxelShape FOOT_NORTH_SHAPE = Shapes.or(
      Block.box(0, 0, 0, 16, 4, 16),
      Block.box(0, 4, 0, 2, 12, 16), // West wall
      Block.box(14, 4, 0, 16, 12, 16), // East wall
      Block.box(0, 4, 14, 16, 12, 16) // South wall (Back of foot)
  );
  protected static final VoxelShape FOOT_SOUTH_SHAPE = Shapes.or(
      Block.box(0, 0, 0, 16, 4, 16),
      Block.box(0, 4, 0, 2, 12, 16),
      Block.box(14, 4, 0, 16, 12, 16),
      Block.box(0, 4, 0, 16, 12, 2) // North wall
  );
  protected static final VoxelShape FOOT_WEST_SHAPE = Shapes.or(
      Block.box(0, 0, 0, 16, 4, 16),
      Block.box(0, 4, 0, 16, 12, 2), // North wall
      Block.box(0, 4, 14, 16, 12, 16), // South wall
      Block.box(14, 4, 0, 16, 12, 16) // East wall
  );
  protected static final VoxelShape FOOT_EAST_SHAPE = Shapes.or(
      Block.box(0, 0, 0, 16, 4, 16),
      Block.box(0, 4, 0, 16, 12, 2),
      Block.box(0, 4, 14, 16, 12, 16),
      Block.box(0, 4, 0, 2, 12, 16) // West wall
  );

  // Shapes for HEAD part (The part attached)
  protected static final VoxelShape HEAD_NORTH_SHAPE = Shapes.or(
      Block.box(0, 0, 0, 16, 4, 16),
      Block.box(0, 4, 0, 2, 12, 16), // West wall
      Block.box(14, 4, 0, 16, 12, 16), // East wall
      Block.box(0, 4, 0, 16, 12, 2) // North wall (Front of head)
  );
  protected static final VoxelShape HEAD_SOUTH_SHAPE = Shapes.or(
      Block.box(0, 0, 0, 16, 4, 16),
      Block.box(0, 4, 0, 2, 12, 16),
      Block.box(14, 4, 0, 16, 12, 16),
      Block.box(0, 4, 14, 16, 12, 16) // South wall
  );
  protected static final VoxelShape HEAD_WEST_SHAPE = Shapes.or(
      Block.box(0, 0, 0, 16, 4, 16),
      Block.box(0, 4, 0, 16, 12, 2),
      Block.box(0, 4, 14, 16, 12, 16),
      Block.box(0, 4, 0, 2, 12, 16) // West wall
  );
  protected static final VoxelShape HEAD_EAST_SHAPE = Shapes.or(
      Block.box(0, 0, 0, 16, 4, 16),
      Block.box(0, 4, 0, 16, 12, 2),
      Block.box(0, 4, 14, 16, 12, 16),
      Block.box(14, 4, 0, 16, 12, 16) // East wall
  );

  public BathtubBlock(Properties properties) {
    super(properties);
    this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(PART, BedPart.FOOT));
  }

  @Override
  public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
    Direction direction = state.getValue(FACING);
    BedPart part = state.getValue(PART);

    if (part == BedPart.FOOT) {
      switch (direction) {
        case NORTH: return FOOT_NORTH_SHAPE;
        case SOUTH: return FOOT_SOUTH_SHAPE;
        case WEST: return FOOT_WEST_SHAPE;
        case EAST: return FOOT_EAST_SHAPE;
        default: return FOOT_NORTH_SHAPE;
      }
    } else {
      switch (direction) {
        case NORTH: return HEAD_NORTH_SHAPE;
        case SOUTH: return HEAD_SOUTH_SHAPE;
        case WEST: return HEAD_WEST_SHAPE;
        case EAST: return HEAD_EAST_SHAPE;
        default: return HEAD_NORTH_SHAPE;
      }
    }
  }

  @Nullable
  @Override
  public BlockState getStateForPlacement(BlockPlaceContext context) {
    Direction direction = context.getHorizontalDirection();
    BlockPos blockpos = context.getClickedPos();
    BlockPos blockpos1 = blockpos.relative(direction);
    Level level = context.getLevel();
    return level.getBlockState(blockpos1).canBeReplaced(context) && level.getWorldBorder().isWithinBounds(blockpos1)
        ? this.defaultBlockState().setValue(FACING, direction)
        : null;
  }

  @Override
  public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
    super.setPlacedBy(level, pos, state, placer, stack);
    if (!level.isClientSide) {
      BlockPos blockpos = pos.relative(state.getValue(FACING));
      level.setBlock(blockpos, state.setValue(PART, BedPart.HEAD), 3);
      level.blockUpdated(pos, Blocks.AIR);
      state.updateNeighbourShapes(level, pos, 3);
    }
  }

  @Override
  public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos currentPos, BlockPos neighborPos) {
    if (direction == getNeighbourDirection(state.getValue(PART), state.getValue(FACING))) {
      return neighborState.is(this) && neighborState.getValue(PART) != state.getValue(PART)
          ? state
          : Blocks.AIR.defaultBlockState();
    } else {
      return super.updateShape(state, direction, neighborState, level, currentPos, neighborPos);
    }
  }

  private static Direction getNeighbourDirection(BedPart part, Direction direction) {
    return part == BedPart.FOOT ? direction : direction.getOpposite();
  }

  @Override
  public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
    if (!level.isClientSide && player.isCreative()) {
      BedPart bedpart = state.getValue(PART);
      if (bedpart == BedPart.FOOT) {
        BlockPos blockpos = pos.relative(getNeighbourDirection(bedpart, state.getValue(FACING)));
        BlockState blockstate = level.getBlockState(blockpos);
        if (blockstate.is(this) && blockstate.getValue(PART) == BedPart.HEAD) {
          level.setBlock(blockpos, Blocks.AIR.defaultBlockState(), 35);
          level.levelEvent(player, 2001, blockpos, Block.getId(blockstate));
        }
      }
    }
    super.playerWillDestroy(level, pos, state, player);
  }

  @Override
  public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
    if (random.nextInt(10) == 0) {
      double x = (double) pos.getX() + 0.5D + (random.nextDouble() - 0.5D) * 0.8D;
      double y = (double) pos.getY() + 0.6D;
      double z = (double) pos.getZ() + 0.5D + (random.nextDouble() - 0.5D) * 0.8D;
      level.addParticle((ParticleOptions) ParticleRegister.STEAM_PARTICLE.get(), x, y, z, 0.0D, 0.02D, 0.0D);
    }
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    builder.add(FACING, PART);
  }
}
