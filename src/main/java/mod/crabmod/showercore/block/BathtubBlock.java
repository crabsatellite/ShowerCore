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
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraft.util.StringRepresentable;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.phys.AABB;
import java.util.List;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import mod.crabmod.showercore.block.entity.BathtubBlockEntity;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.FluidStack;
import mod.crabmod.showercore.Config;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

public class BathtubBlock extends HorizontalDirectionalBlock implements EntityBlock {
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
      ROSE_BATH("rose_bath"),
      CUSTOM("custom");

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

  @Nullable
  @Override
  public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new BathtubBlockEntity(pos, state);
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



  @Override
  public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
      ItemStack itemstack = player.getItemInHand(hand);

      // Place Rubber Duck
      if (itemstack.getItem() == mod.crabmod.showercore.registers.ItemRegister.RUBBER_DUCK.get()) {
          return InteractionResult.PASS;
      }

      if (itemstack.isEmpty() && state.getValue(PART) == BedPart.HEAD) {
          if (!level.isClientSide) {
              // Check if HEAD is occupied
              List<SeatEntity> seats = level.getEntitiesOfClass(SeatEntity.class, new AABB(pos));
              if (!seats.isEmpty() && !seats.get(0).getPassengers().isEmpty()) {
                  Entity passenger = seats.get(0).getFirstPassenger();
                  if (passenger instanceof Player occupant) {
                      if (occupant != player) {
                          player.sendSystemMessage(Component.literal("Asking " + occupant.getName().getString() + " for permission..."));
                          
                          Component accept = Component.literal("[Accept]")
                              .withStyle(Style.EMPTY.withColor(net.minecraft.ChatFormatting.GREEN)
                              .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/showercore accept_bath " + player.getName().getString())));
                          
                          Component deny = Component.literal("[Reject]")
                              .withStyle(Style.EMPTY.withColor(net.minecraft.ChatFormatting.RED)
                              .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/showercore deny_bath " + player.getName().getString())));

                          occupant.sendSystemMessage(Component.literal("Hey! " + player.getName().getString() + " wants to squeeze into the tub with you. It's a bit tight, but... ( ͡° ͜ʖ ͡°) ")
                              .append(accept).append(" ").append(deny));
                      }
                  }
              } else {
                  SeatEntity seat = new SeatEntity(level, pos.getX() + 0.5, pos.getY() + 0.1, pos.getZ() + 0.5);
                  level.addFreshEntity(seat);
                  player.startRiding(seat);
              }
          }
          return InteractionResult.sidedSuccess(level.isClientSide);
      }

      if (itemstack.isEmpty() && state.getValue(PART) == BedPart.FOOT) {
          if (!level.isClientSide) {
              Direction direction = state.getValue(FACING);
              BlockPos headPos = pos.relative(direction);
              
              // Check if HEAD is occupied
              List<SeatEntity> seats = level.getEntitiesOfClass(SeatEntity.class, new AABB(headPos));
              if (!seats.isEmpty() && !seats.get(0).getPassengers().isEmpty()) {
                  Entity passenger = seats.get(0).getFirstPassenger();
                  if (passenger instanceof Player occupant) {
                      if (occupant != player) {
                          player.sendSystemMessage(Component.literal("Asking " + occupant.getName().getString() + " for permission..."));
                          
                          Component accept = Component.literal("[Accept]")
                              .withStyle(Style.EMPTY.withColor(net.minecraft.ChatFormatting.GREEN)
                              .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/showercore accept_bath " + player.getName().getString())));
                          
                          Component deny = Component.literal("[Reject]")
                              .withStyle(Style.EMPTY.withColor(net.minecraft.ChatFormatting.RED)
                              .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/showercore deny_bath " + player.getName().getString())));

                          occupant.sendSystemMessage(Component.literal("Hey! " + player.getName().getString() + " wants to squeeze into the tub with you. It's a bit tight, but... ( ͡° ͜ʖ ͡°) ")
                              .append(accept).append(" ").append(deny));
                      }
                  }
              } else {
                  SeatEntity seat = new SeatEntity(level, headPos.getX() + 0.5, headPos.getY() + 0.1, headPos.getZ() + 0.5);
                  level.addFreshEntity(seat);
                  player.startRiding(seat);
              }
          }
          return InteractionResult.sidedSuccess(level.isClientSide);
      }

      // Fluid interaction
      if (!itemstack.isEmpty()) {
          if (FluidUtil.getFluidHandler(itemstack).isPresent()) {
              BlockEntity be = level.getBlockEntity(pos);
              if (be instanceof BathtubBlockEntity bathtubBe) {
                  boolean success = FluidUtil.interactWithFluidHandler(player, hand, level, pos, null);
                  if (success) {
                      if (!level.isClientSide) {
                          FluidStack fluid = bathtubBe.getFluidTank().getFluid();
                          syncFluidToOtherPart(level, pos, state, fluid);
                          updateLiquidState(level, pos, state, fluid);
                      }
                      return InteractionResult.sidedSuccess(level.isClientSide);
                  }
              }
          }
      }
      return InteractionResult.PASS;
   }

   private void syncFluidToOtherPart(Level level, BlockPos pos, BlockState state, FluidStack fluid) {
       Direction direction = state.getValue(FACING);
       BedPart part = state.getValue(PART);
       BlockPos otherPos = part == BedPart.FOOT ? pos.relative(direction) : pos.relative(direction.getOpposite());
       BlockEntity otherBe = level.getBlockEntity(otherPos);
       if (otherBe instanceof BathtubBlockEntity bathtubBe) {
           bathtubBe.getFluidTank().setFluid(fluid.copy());
           BlockState otherState = level.getBlockState(otherPos);
           if (otherState.getBlock() == this) {
               updateLiquidState(level, otherPos, otherState, fluid);
           }
       }
   }

   private void updateLiquidState(Level level, BlockPos pos, BlockState state, FluidStack fluid) {
       LiquidType newLiquid = LiquidType.EMPTY;
       if (!fluid.isEmpty()) {
           String fluidName = ForgeRegistries.FLUIDS.getKey(fluid.getFluid()).getPath();
           if (fluidName.equals("water")) newLiquid = LiquidType.WATER;
           else if (fluidName.equals("hot_water_fluid") || fluidName.equals("hot_water_flowing")) newLiquid = LiquidType.HOT_WATER;
           else if (fluidName.contains("herbal")) newLiquid = LiquidType.HERBAL_BATH;
           else if (fluidName.contains("honey")) newLiquid = LiquidType.HONEY_BATH;
           else if (fluidName.contains("milk")) newLiquid = LiquidType.MILK_BATH;
           else if (fluidName.contains("peony")) newLiquid = LiquidType.PEONY_BATH;
           else if (fluidName.contains("rose")) newLiquid = LiquidType.ROSE_BATH;
           else newLiquid = LiquidType.CUSTOM;
       }
       
       boolean running = state.getValue(RUNNING);
       if (newLiquid == LiquidType.EMPTY && fluid.isEmpty()) {
           running = false;
       }
       level.setBlock(pos, state.setValue(LIQUID, newLiquid).setValue(RUNNING, running), 3);
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
    boolean produceSteam = false;

    if (liquid == LiquidType.CUSTOM) {
         BlockEntity be = level.getBlockEntity(pos);
         if (be instanceof BathtubBlockEntity bathtubBe) {
             FluidStack fluidStack = bathtubBe.getFluidTank().getFluid();
             if (!fluidStack.isEmpty()) {
                 Fluid fluid = fluidStack.getFluid();
                 if (Config.steamFluids.contains(fluid)) {
                     produceSteam = true;
                 }
             }
         }
    } else if (liquid != LiquidType.EMPTY && liquid != LiquidType.WATER) {
        produceSteam = true;
    }

    if (produceSteam && random.nextInt(10) == 0) {
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



  @Override
  public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
      if (state.getValue(LIQUID) == LiquidType.CUSTOM) {
          BlockEntity be = level.getBlockEntity(pos);
          if (be instanceof BathtubBlockEntity bathtubBe) {
              FluidStack fluidStack = bathtubBe.getFluidTank().getFluid();
              if (!fluidStack.isEmpty()) {
                  Fluid fluid = fluidStack.getFluid();
                  if (fluid == Fluids.LAVA || fluid == Fluids.FLOWING_LAVA) {
                      // Apply lava effects manually
                      entity.setSecondsOnFire(15);
                      entity.hurt(level.damageSources().lava(), 4.0F);
                  }
              }
          }
      }
      super.entityInside(state, level, pos, entity);
  }
}
