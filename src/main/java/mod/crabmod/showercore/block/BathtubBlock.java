package mod.crabmod.showercore.block;

import javax.annotation.Nullable;
import mod.crabmod.showercore.entity.FaucetInteractionEntity;
import mod.crabmod.showercore.entity.SeatEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
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
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.RandomSource;
import com.crabmod.hotbath.registers.ParticleRegister;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.phys.Vec3;
import java.util.Optional;

public class BathtubBlock extends HorizontalDirectionalBlock {
  public static final EnumProperty<BedPart> PART = BlockStateProperties.BED_PART;
  public static final BooleanProperty RUNNING = BooleanProperty.create("running");
  public static final EnumProperty<LiquidType> LIQUID = EnumProperty.create("liquid", LiquidType.class);

  public enum LiquidType implements StringRepresentable {
      EMPTY("empty"),
      WATER("water"),
      HOT_WATER("hot_water"),
      HERBAL_BATH("herbal_bath"),
      HONEY_BATH("honey_bath"),
      MILK_BATH("milk_bath"),
      PEONY_BATH("peony_bath"),
      ROSE_BATH("rose_bath");

      private final String name;

      private LiquidType(String name) {
         this.name = name;
      }

      public String toString() {
         return this.name;
      }

      public String getSerializedName() {
         return this.name;
      }
   }

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
    this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(PART, BedPart.FOOT).setValue(LIQUID, LiquidType.EMPTY));
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

      // Spawn Faucet Entity
      Direction facing = state.getValue(FACING);
      double x = blockpos.getX();
      double y = blockpos.getY();
      double z = blockpos.getZ();
      
      switch (facing) {
          case NORTH: x += 0.5; y += 0.78; z += 0.125; break;
          case SOUTH: x += 0.5; y += 0.78; z += 0.875; break;
          case WEST: x += 0.125; y += 0.78; z += 0.5; break;
          case EAST: x += 0.875; y += 0.78; z += 0.5; break;
          default: break;
      }
      
      FaucetInteractionEntity faucet = new FaucetInteractionEntity(level, x, y, z);
      level.addFreshEntity(faucet);
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

  private boolean isHittingFaucet(BlockState state, BlockHitResult hit, BlockPos pos) {
       Direction facing = state.getValue(FACING);
       Vec3 loc = hit.getLocation().subtract(pos.getX(), pos.getY(), pos.getZ());
       
       double minX = 0.4375;
       double maxX = 0.5625;
       double minY = 0.6875;
       double maxY = 0.875;
       double minZ = 0.0;
       double maxZ = 0.25;

       switch (facing) {
           case NORTH:
               return loc.x >= minX && loc.x <= maxX && loc.y >= minY && loc.y <= maxY && loc.z >= minZ && loc.z <= maxZ;
           case SOUTH:
               return loc.x >= minX && loc.x <= maxX && loc.y >= minY && loc.y <= maxY && loc.z >= (1 - maxZ) && loc.z <= (1 - minZ);
           case WEST:
               return loc.x >= minZ && loc.x <= maxZ && loc.y >= minY && loc.y <= maxY && loc.z >= minX && loc.z <= maxX;
           case EAST:
               return loc.x >= (1 - maxZ) && loc.x <= (1 - minZ) && loc.y >= minY && loc.y <= maxY && loc.z >= minX && loc.z <= maxX;
           default:
               return false;
       }
   }

  @Override
  public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
      ItemStack itemstack = player.getItemInHand(hand);
      LiquidType currentLiquid = state.getValue(LIQUID);

      if (itemstack.isEmpty() && state.getValue(PART) == BedPart.HEAD) {
          if (isHittingFaucet(state, hit, pos)) {
              if (!level.isClientSide) {
                  boolean isRunning = state.getValue(RUNNING);
                  if (!isRunning && currentLiquid != LiquidType.EMPTY) {
                      level.setBlock(pos, state.setValue(RUNNING, true), 3);
                      level.playSound(null, pos, SoundEvents.WATER_AMBIENT, SoundSource.BLOCKS, 1.0F, 1.0F);
                  } else {
                      level.setBlock(pos, state.setValue(RUNNING, false), 3);
                  }
              }
              return InteractionResult.sidedSuccess(level.isClientSide);
          } else {
              if (!level.isClientSide) {
                  SeatEntity seat = new SeatEntity(level, pos.getX() + 0.5, pos.getY() + 0.1, pos.getZ() + 0.5);
                  level.addFreshEntity(seat);
                  player.startRiding(seat);
              }
              return InteractionResult.sidedSuccess(level.isClientSide);
          }
      }

      if (itemstack.isEmpty() && state.getValue(PART) == BedPart.FOOT) {
          if (!level.isClientSide) {
              Direction direction = state.getValue(FACING);
              BlockPos headPos = pos.relative(direction);
              SeatEntity seat = new SeatEntity(level, headPos.getX() + 0.5, headPos.getY() + 0.1, headPos.getZ() + 0.5);
              level.addFreshEntity(seat);
              player.startRiding(seat);
          }
          return InteractionResult.sidedSuccess(level.isClientSide);
      }

      if (currentLiquid == LiquidType.EMPTY) {
         if (itemstack.is(Items.WATER_BUCKET)) {
            if (!level.isClientSide) {
               updateBothParts(level, pos, state, LiquidType.WATER);
               if (!player.isCreative()) {
                  player.setItemInHand(hand, new ItemStack(Items.BUCKET));
               }
               level.playSound(null, pos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
         } else {
             ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(itemstack.getItem());
             if (itemId != null && itemId.getNamespace().equals("hotbath")) {
                 LiquidType newLiquid = null;
                 String path = itemId.getPath();
                 if (path.equals("hot_water_bucket")) newLiquid = LiquidType.HOT_WATER;
                 else if (path.equals("herbal_bath_bucket")) newLiquid = LiquidType.HERBAL_BATH;
                 else if (path.equals("honey_bath_bucket")) newLiquid = LiquidType.HONEY_BATH;
                 else if (path.equals("milk_bath_bucket")) newLiquid = LiquidType.MILK_BATH;
                 else if (path.equals("peony_bath_bucket")) newLiquid = LiquidType.PEONY_BATH;
                 else if (path.equals("rose_bath_bucket")) newLiquid = LiquidType.ROSE_BATH;

                 if (newLiquid != null) {
                     if (!level.isClientSide) {
                         updateBothParts(level, pos, state, newLiquid);
                         if (!player.isCreative()) {
                            player.setItemInHand(hand, new ItemStack(Items.BUCKET));
                         }
                         level.playSound(null, pos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
                     }
                     return InteractionResult.sidedSuccess(level.isClientSide);
                 }
             }
         }
      } else {
         if (itemstack.is(Items.BUCKET)) {
            if (!level.isClientSide) {
               Item filledBucket = Items.WATER_BUCKET;
               if (currentLiquid != LiquidType.WATER) {
                   String bucketName = "hot_water_bucket";
                   switch (currentLiquid) {
                       case HERBAL_BATH: bucketName = "herbal_bath_bucket"; break;
                       case HONEY_BATH: bucketName = "honey_bath_bucket"; break;
                       case MILK_BATH: bucketName = "milk_bath_bucket"; break;
                       case PEONY_BATH: bucketName = "peony_bath_bucket"; break;
                       case ROSE_BATH: bucketName = "rose_bath_bucket"; break;
                       default: break;
                   }
                   filledBucket = ForgeRegistries.ITEMS.getValue(new ResourceLocation("hotbath", bucketName));
               }
               
               updateBothParts(level, pos, state, LiquidType.EMPTY);

               if (!player.isCreative()) {
                   itemstack.shrink(1);
                   if (itemstack.isEmpty()) {
                       player.setItemInHand(hand, new ItemStack(filledBucket));
                   } else if (!player.getInventory().add(new ItemStack(filledBucket))) {
                       player.drop(new ItemStack(filledBucket), false);
                   }
               }
               level.playSound(null, pos, SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
         }
      }
      return InteractionResult.PASS;
   }

   private void updateBothParts(Level level, BlockPos pos, BlockState state, LiquidType newLiquid) {
       boolean running = state.getValue(RUNNING);
       if (newLiquid == LiquidType.EMPTY) {
           running = false;
       }
       level.setBlock(pos, state.setValue(LIQUID, newLiquid).setValue(RUNNING, running), 3);
       Direction direction = state.getValue(FACING);
       BedPart part = state.getValue(PART);
       BlockPos otherPos = part == BedPart.FOOT ? pos.relative(direction) : pos.relative(direction.getOpposite());
       BlockState otherState = level.getBlockState(otherPos);
       if (otherState.getBlock() == this && otherState.getValue(PART) != part) {
           level.setBlock(otherPos, otherState.setValue(LIQUID, newLiquid).setValue(RUNNING, running), 3);
       }
   }

  @Override
  public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
    if (!level.isClientSide) {
      BedPart bedpart = state.getValue(PART);
      if (bedpart == BedPart.FOOT) {
        BlockPos blockpos = pos.relative(getNeighbourDirection(bedpart, state.getValue(FACING)));
        BlockState blockstate = level.getBlockState(blockpos);
        if (blockstate.is(this) && blockstate.getValue(PART) == BedPart.HEAD) {
          level.setBlock(blockpos, Blocks.AIR.defaultBlockState(), 35);
          level.levelEvent(player, 2001, blockpos, Block.getId(blockstate));
        }
      } else {
          // Remove Faucet Entity if HEAD is broken
          level.getEntitiesOfClass(FaucetInteractionEntity.class, new net.minecraft.world.phys.AABB(pos)).forEach(Entity::discard);
      }
    }
    super.playerWillDestroy(level, pos, state, player);
  }

  @Override
  public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
    LiquidType liquid = state.getValue(LIQUID);
    if (liquid != LiquidType.EMPTY && liquid != LiquidType.WATER && random.nextInt(10) == 0) {
      double x = (double) pos.getX() + 0.5D + (random.nextDouble() - 0.5D) * 0.8D;
      double y = (double) pos.getY() + 0.9D;
      double z = (double) pos.getZ() + 0.5D + (random.nextDouble() - 0.5D) * 0.8D;
      level.addParticle((ParticleOptions) ParticleRegister.STEAM_PARTICLE.get(), x, y, z, 0.0D, 0.02D, 0.0D);
    }
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    builder.add(FACING, PART, LIQUID, RUNNING);
  }
}
