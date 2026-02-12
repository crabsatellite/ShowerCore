package mod.crabmod.showercore.block;

import javax.annotation.Nullable;
import mod.crabmod.showercore.entity.FaucetInteractionEntity;
import mod.crabmod.showercore.entity.SeatEntity;
import net.minecraft.world.ItemInteractionResult;
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
import mod.crabmod.showercore.registers.ParticleRegister;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.StringRepresentable;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.phys.AABB;
import java.util.List;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import mod.crabmod.showercore.block.entity.BathtubBlockEntity;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidActionResult;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import mod.crabmod.showercore.Config;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import java.util.Collections;
import java.util.Optional;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Items;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import com.mojang.serialization.MapCodec;
import com.crabmod.hotbath.custom_fluid.CustomFluidAPI;
import com.crabmod.hotbath.custom_fluid.CustomFluidBucketItem;
import com.crabmod.hotbath.custom_fluid.CustomFluidDefinition;
import com.crabmod.hotbath.custom_fluid.DynamicFluidRegistry;
import com.crabmod.hotbath.custom_fluid.SplashCustomFluidBottleItem;

public class BathtubBlock extends HorizontalDirectionalBlock implements EntityBlock {
  public static final MapCodec<BathtubBlock> CODEC = simpleCodec(BathtubBlock::new);

  @Override
  protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
     return CODEC;
  }

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

  @Override
  public boolean hasDynamicLightEmission(BlockState state) {
      return true;
  }

  @Override
  public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
      if (state.getValue(LIQUID) != LiquidType.CUSTOM) {
          return 0;
      }
      BlockEntity be = level.getBlockEntity(pos);
      if (be instanceof BathtubBlockEntity bathtubBe) {
          return bathtubBe.getCustomFluidDefinition()
                  .map(CustomFluidDefinition::luminosity)
                  .orElse(0);
      }
      return 0;
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
  protected ItemInteractionResult useItemOn(ItemStack itemstack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {

      // Place Rubber Duck
      if (itemstack.getItem() == mod.crabmod.showercore.registers.ItemRegister.RUBBER_DUCK.get()) {
          return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
      }

      if (itemstack.isEmpty() && state.getValue(PART) == BedPart.HEAD) {
          if (!level.isClientSide) {
              Entity occupantToAsk = null;
              // Check if HEAD is occupied
              List<SeatEntity> seats = level.getEntitiesOfClass(SeatEntity.class, new AABB(pos));
              if (!seats.isEmpty() && !seats.get(0).getPassengers().isEmpty()) {
                  occupantToAsk = seats.get(0).getFirstPassenger();
              } else {
                  // HEAD is empty. Check FOOT.
                  Direction direction = state.getValue(FACING);
                  BlockPos footPos = pos.relative(direction.getOpposite());
                  List<SeatEntity> footSeats = level.getEntitiesOfClass(SeatEntity.class, new AABB(footPos));
                  
                  if (!footSeats.isEmpty() && !footSeats.get(0).getPassengers().isEmpty()) {
                      // Ask the passenger at FOOT
                      occupantToAsk = footSeats.get(0).getFirstPassenger();
                  }
              }

              if (occupantToAsk instanceof Player occupant) {
                  if (occupant != player) {
                      player.sendSystemMessage(Component.translatable("message.showercore.ask_permission", occupant.getName()));
                      
                      Component accept = Component.translatable("message.showercore.accept")
                          .withStyle(Style.EMPTY.withColor(net.minecraft.ChatFormatting.GREEN)
                          .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/showercore accept_bath " + player.getName().getString())));
                      
                      Component deny = Component.translatable("message.showercore.reject")
                          .withStyle(Style.EMPTY.withColor(net.minecraft.ChatFormatting.RED)
                          .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/showercore deny_bath " + player.getName().getString())));

                      occupant.sendSystemMessage(Component.translatable("message.showercore.request_join", player.getName())
                          .append(accept).append(" ").append(deny));
                  }
              } else {
                  SeatEntity seat = new SeatEntity(level, pos.getX() + 0.5, pos.getY() + 0.1, pos.getZ() + 0.5);
                  level.addFreshEntity(seat);
                  player.startRiding(seat);
              }
          }
          return ItemInteractionResult.sidedSuccess(level.isClientSide);
      }

      if (itemstack.isEmpty() && state.getValue(PART) == BedPart.FOOT) {
          if (!level.isClientSide) {
              Direction direction = state.getValue(FACING);
              BlockPos headPos = pos.relative(direction);
              Entity occupantToAsk = null;
              
              // Check if HEAD is occupied
              List<SeatEntity> seats = level.getEntitiesOfClass(SeatEntity.class, new AABB(headPos));
              if (!seats.isEmpty() && !seats.get(0).getPassengers().isEmpty()) {
                  occupantToAsk = seats.get(0).getFirstPassenger();
              } else {
                  // HEAD is empty. Check FOOT (current pos).
                  List<SeatEntity> footSeats = level.getEntitiesOfClass(SeatEntity.class, new AABB(pos));
                  
                  if (!footSeats.isEmpty() && !footSeats.get(0).getPassengers().isEmpty()) {
                      // Ask the passenger at FOOT
                      occupantToAsk = footSeats.get(0).getFirstPassenger();
                  }
              }

              if (occupantToAsk instanceof Player occupant) {
                  if (occupant != player) {
                      player.sendSystemMessage(Component.translatable("message.showercore.ask_permission", occupant.getName()));
                      
                      Component accept = Component.translatable("message.showercore.accept")
                          .withStyle(Style.EMPTY.withColor(net.minecraft.ChatFormatting.GREEN)
                          .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/showercore accept_bath " + player.getName().getString())));
                      
                      Component deny = Component.translatable("message.showercore.reject")
                          .withStyle(Style.EMPTY.withColor(net.minecraft.ChatFormatting.RED)
                          .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/showercore deny_bath " + player.getName().getString())));

                      occupant.sendSystemMessage(Component.translatable("message.showercore.request_join", player.getName())
                          .append(accept).append(" ").append(deny));
                  }
              } else {
                  SeatEntity seat = new SeatEntity(level, headPos.getX() + 0.5, headPos.getY() + 0.1, headPos.getZ() + 0.5);
                  level.addFreshEntity(seat);
                  player.startRiding(seat);
              }
          }
          return ItemInteractionResult.sidedSuccess(level.isClientSide);
      }

      // Fluid interaction
      if (!itemstack.isEmpty()) {
          BlockEntity be = level.getBlockEntity(pos);
          if (be instanceof BathtubBlockEntity bathtubBe) {
              IFluidHandler handler = bathtubBe.getFluidTank();

              // Handle hotBath custom fluid items (buckets/bottles) that use Data Components
              // These items store their fluid ID via CustomFluidDataComponents instead of
              // standard fluid handler capabilities, so FluidUtil cannot handle them.
              if (CustomFluidAPI.hasCustomFluid(itemstack)) {
                  Optional<CustomFluidDefinition> defOpt = CustomFluidAPI.getFluidFromItem(itemstack);
                  if (defOpt.isPresent()) {
                      CustomFluidDefinition definition = defOpt.get();
                      // Use the dynamic custom fluid as the FluidStack base
                      FluidStack customFluidStack = new FluidStack(
                              DynamicFluidRegistry.DYNAMIC_FLUID_STILL.get(), 1000);
                      int filled = handler.fill(customFluidStack, IFluidHandler.FluidAction.SIMULATE);
                      if (filled == 1000) {
                          if (!level.isClientSide) {
                              handler.fill(customFluidStack, IFluidHandler.FluidAction.EXECUTE);

                              // Store the custom fluid ID in the block entity
                              bathtubBe.setCustomFluidId(definition.id());

                              if (!player.isCreative()) {
                                  // Return the appropriate empty container
                                  if (itemstack.getItem() instanceof CustomFluidBucketItem) {
                                      player.setItemInHand(hand, new ItemStack(Items.BUCKET));
                                  } else if (itemstack.getItem() instanceof SplashCustomFluidBottleItem) {
                                      // Splash bottles are consumed without returning a container
                                      itemstack.shrink(1);
                                  } else {
                                      // Regular bottle items - shrink and give back glass bottle
                                      itemstack.shrink(1);
                                      ItemStack bottle = new ItemStack(Items.GLASS_BOTTLE);
                                      if (!player.getInventory().add(bottle)) {
                                          player.drop(bottle, false);
                                      }
                                  }
                              }

                              level.playSound(null, pos, SoundEvents.BUCKET_EMPTY,
                                      net.minecraft.sounds.SoundSource.BLOCKS, 1.0F, 1.0F);

                              FluidStack currentFluid = bathtubBe.getFluidTank().getFluid();
                              syncFluidToOtherPart(level, pos, state, currentFluid);
                              updateLiquidState(level, pos, state, currentFluid);
                          }
                          return ItemInteractionResult.sidedSuccess(level.isClientSide);
                      }
                  }
              }

              // Try standard FluidUtil interaction first
              boolean success = FluidUtil.interactWithFluidHandler(player, hand, handler);

              if (success) {
                  if (!level.isClientSide) {
                      FluidStack fluid = bathtubBe.getFluidTank().getFluid();
                      syncFluidToOtherPart(level, pos, state, fluid);
                      updateLiquidState(level, pos, state, fluid);
                  }
                  return ItemInteractionResult.sidedSuccess(level.isClientSide);
              }

              // Fallback for BucketItems that are missing FluidHandler capability (e.g. some modded buckets)
              if (itemstack.getItem() instanceof BucketItem bucketItem) {
                   Fluid bucketContent = bucketItem.content;
                   if (bucketContent != Fluids.EMPTY) {
                       FluidStack fluidStack = new FluidStack(bucketContent, 1000);
                       // Simulate filling
                       int filled = handler.fill(fluidStack, IFluidHandler.FluidAction.SIMULATE);
                       if (filled == 1000) {
                           if (!level.isClientSide) {
                               // Execute filling
                               handler.fill(fluidStack, IFluidHandler.FluidAction.EXECUTE);

                               if (!player.isCreative()) {
                                   player.setItemInHand(hand, new ItemStack(Items.BUCKET));
                               }

                               SoundEvent sound = bucketContent.getFluidType().getSound(player, level, pos, net.neoforged.neoforge.common.SoundActions.BUCKET_EMPTY);
                               if (sound == null) sound = SoundEvents.BUCKET_EMPTY;
                               level.playSound(null, pos, sound, net.minecraft.sounds.SoundSource.BLOCKS, 1.0F, 1.0F);

                               FluidStack currentFluid = bathtubBe.getFluidTank().getFluid();
                               syncFluidToOtherPart(level, pos, state, currentFluid);
                               updateLiquidState(level, pos, state, currentFluid);
                           }
                           return ItemInteractionResult.sidedSuccess(level.isClientSide);
                       }
                   }
              }
          }
      }
      return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
   }

   private void syncFluidToOtherPart(Level level, BlockPos pos, BlockState state, FluidStack fluid) {
       Direction direction = state.getValue(FACING);
       BedPart part = state.getValue(PART);
       BlockPos otherPos = part == BedPart.FOOT ? pos.relative(direction) : pos.relative(direction.getOpposite());
       BlockEntity otherBe = level.getBlockEntity(otherPos);
       if (otherBe instanceof BathtubBlockEntity otherBathtubBe) {
           otherBathtubBe.getFluidTank().setFluid(fluid.copy());
           // Sync custom fluid ID to the other part
           BlockEntity thisBe = level.getBlockEntity(pos);
           if (thisBe instanceof BathtubBlockEntity thisBathtubBe) {
               otherBathtubBe.setCustomFluidId(thisBathtubBe.getCustomFluidId());
           }
           BlockState otherState = level.getBlockState(otherPos);
           if (otherState.getBlock() == this) {
               updateLiquidState(level, otherPos, otherState, fluid);
           }
       }
   }

   private void updateLiquidState(Level level, BlockPos pos, BlockState state, FluidStack fluid) {
       LiquidType newLiquid = LiquidType.EMPTY;
       ResourceLocation detectedCustomFluidId = null;

       if (!fluid.isEmpty()) {
           ResourceLocation resourceLocation = BuiltInRegistries.FLUID.getKey(fluid.getFluid());
           String fluidPath = resourceLocation.getPath();
           String namespace = resourceLocation.getNamespace();

           if (fluidPath.equals("water")) {
               newLiquid = LiquidType.WATER;
           } else if (namespace.equals("hotbath")) {
               // Use namespace + exact path matching for built-in hotBath fluids
               // to avoid false positives from other mods with similar fluid names
               if (fluidPath.equals("hot_water_fluid") || fluidPath.equals("hot_water_flowing")) {
                   newLiquid = LiquidType.HOT_WATER;
               } else if (fluidPath.equals("herbal_bath_fluid") || fluidPath.equals("herbal_bath_flowing")) {
                   newLiquid = LiquidType.HERBAL_BATH;
               } else if (fluidPath.equals("honey_bath_fluid") || fluidPath.equals("honey_bath_flowing")) {
                   newLiquid = LiquidType.HONEY_BATH;
               } else if (fluidPath.equals("milk_bath_fluid") || fluidPath.equals("milk_bath_flowing")) {
                   newLiquid = LiquidType.MILK_BATH;
               } else if (fluidPath.equals("peony_bath_fluid") || fluidPath.equals("peony_bath_flowing")) {
                   newLiquid = LiquidType.PEONY_BATH;
               } else if (fluidPath.equals("rose_bath_fluid") || fluidPath.equals("rose_bath_flowing")) {
                   newLiquid = LiquidType.ROSE_BATH;
               } else {
                   // Check if this is the dynamic custom fluid (Data Components system in 1.21).
                   // In 1.21, all custom fluids share a single fluid type (hotbath:dynamic_custom_fluid)
                   // and the specific fluid identity is stored in the block entity via Data Components.
                   boolean isDynamicCustomFluid =
                           fluid.getFluid() == DynamicFluidRegistry.DYNAMIC_FLUID_STILL.get()
                        || fluid.getFluid() == DynamicFluidRegistry.DYNAMIC_FLUID_FLOWING.get();

                   if (isDynamicCustomFluid) {
                       // The custom fluid ID should already be stored in the block entity
                       // (set when the custom fluid bucket/bottle was used on the bathtub)
                       BlockEntity currentBe = level.getBlockEntity(pos);
                       if (currentBe instanceof BathtubBlockEntity currentBathtubBe
                               && currentBathtubBe.getCustomFluidId() != null) {
                           newLiquid = LiquidType.CUSTOM;
                           detectedCustomFluidId = currentBathtubBe.getCustomFluidId();
                       } else {
                           // Dynamic custom fluid without stored ID - fallback
                           newLiquid = LiquidType.HOT_WATER;
                       }
                   } else {
                       // Other hotBath namespace fluid (legacy per-fluid registrations)
                       for (CustomFluidDefinition def : CustomFluidAPI.getAllFluids()) {
                           if (resourceLocation.toString().contains(def.id().getPath())) {
                               newLiquid = LiquidType.CUSTOM;
                               detectedCustomFluidId = def.id();
                               break;
                           }
                       }
                       // Fallback to HOT_WATER if no custom fluid matched but still hotbath namespace
                       if (newLiquid != LiquidType.CUSTOM) {
                           newLiquid = LiquidType.HOT_WATER;
                       }
                   }
               }
           } else {
               // Non-hotbath mod fluid: check if it matches any registered custom fluid
               for (CustomFluidDefinition def : CustomFluidAPI.getAllFluids()) {
                   if (resourceLocation.toString().contains(def.id().getPath())
                           || def.id().toString().contains(fluidPath)) {
                       newLiquid = LiquidType.CUSTOM;
                       detectedCustomFluidId = def.id();
                       break;
                   }
               }
               // If no custom fluid matched, still mark as CUSTOM (generic modded fluid)
               if (newLiquid != LiquidType.CUSTOM) {
                   newLiquid = LiquidType.CUSTOM;
               }
           }
       }

       // Store the custom fluid ID in the block entity
       BlockEntity be = level.getBlockEntity(pos);
       if (be instanceof BathtubBlockEntity bathtubBe) {
           if (newLiquid == LiquidType.CUSTOM) {
               bathtubBe.setCustomFluidId(detectedCustomFluidId);
           } else {
               bathtubBe.setCustomFluidId(null);
           }
       }

       boolean running = state.getValue(RUNNING);
       if (newLiquid == LiquidType.EMPTY && fluid.isEmpty()) {
           running = false;
       }
       level.setBlock(pos, state.setValue(LIQUID, newLiquid).setValue(RUNNING, running), 3);
   }



  @Override
  public List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
    Entity entity = params.getOptionalParameter(LootContextParams.THIS_ENTITY);
    if (entity instanceof Player player && player.isCreative()) {
        return Collections.emptyList();
    }
    BlockEntity blockEntity = params.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
    if (blockEntity instanceof BathtubBlockEntity bathtubEntity) {
      ItemStack stack = new ItemStack(this);
      CompoundTag tag = bathtubEntity.saveWithoutMetadata(bathtubEntity.getLevel().registryAccess());
      BlockItem.setBlockEntityData(stack, blockEntity.getType(), tag);
      return Collections.singletonList(stack);
    }
    return super.getDrops(state, params);
  }

  @Override
  public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
    if (!level.isClientSide) {
      BedPart bedpart = state.getValue(PART);
      
      if (player.isCreative()) {
          Direction neighborDir = getNeighbourDirection(bedpart, state.getValue(FACING));
          BlockPos neighborPos = pos.relative(neighborDir);
          BlockState neighborState = level.getBlockState(neighborPos);
          if (neighborState.is(this) && neighborState.getValue(PART) != bedpart) {
              level.setBlock(neighborPos, Blocks.AIR.defaultBlockState(), 35);
              level.levelEvent(player, 2001, neighborPos, Block.getId(neighborState));
          }
      } else {
          if (bedpart == BedPart.FOOT) {
            BlockPos blockpos = pos.relative(getNeighbourDirection(bedpart, state.getValue(FACING)));
            BlockState blockstate = level.getBlockState(blockpos);
            if (blockstate.is(this) && blockstate.getValue(PART) == BedPart.HEAD) {
              level.setBlock(blockpos, Blocks.AIR.defaultBlockState(), 35);
              level.levelEvent(player, 2001, blockpos, Block.getId(blockstate));
            }
          }
      }

      if (bedpart == BedPart.HEAD) {
          // Remove Faucet Entity if HEAD is broken
          level.getEntitiesOfClass(FaucetInteractionEntity.class, new net.minecraft.world.phys.AABB(pos)).forEach(Entity::discard);
      }
    }
    return super.playerWillDestroy(level, pos, state, player);
  }

  @Override
  public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
    LiquidType liquid = state.getValue(LIQUID);
    boolean produceSteam = false;

    if (liquid == LiquidType.CUSTOM) {
         BlockEntity be = level.getBlockEntity(pos);
         if (be instanceof BathtubBlockEntity bathtubBe) {
             // Check custom fluid definition for steam flag
             Optional<CustomFluidDefinition> defOpt = bathtubBe.getCustomFluidDefinition();
             if (defOpt.isPresent()) {
                 CustomFluidDefinition def = defOpt.get();
                 if (def.isHot() && def.showSteam()) {
                     produceSteam = true;
                 }
             } else {
                 // No custom fluid definition found; fall back to config steam fluids list
                 FluidStack fluidStack = bathtubBe.getFluidTank().getFluid();
                 if (!fluidStack.isEmpty()) {
                     Fluid fluid = fluidStack.getFluid();
                     if (Config.steamFluids.contains(fluid)) {
                         produceSteam = true;
                     }
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
      level.addParticle((ParticleOptions) com.crabmod.hotbath.registers.ParticleRegister.STEAM_PARTICLE.get(), x, y, z, 0.0D, 0.02D, 0.0D);
    }

    // Bubble particles for custom fluids with showBubbles enabled
    if (liquid == LiquidType.CUSTOM) {
        BlockEntity bubbleBe = level.getBlockEntity(pos);
        if (bubbleBe instanceof BathtubBlockEntity bubbleBathtubBe) {
            Optional<CustomFluidDefinition> bubbleDefOpt = bubbleBathtubBe.getCustomFluidDefinition();
            if (bubbleDefOpt.isPresent() && bubbleDefOpt.get().showBubbles()) {
                if (random.nextInt(15) == 0) {
                    double bx = (double) pos.getX() + 0.5D + (random.nextDouble() - 0.5D) * 0.6D;
                    double by = (double) pos.getY() + 0.7D;
                    double bz = (double) pos.getZ() + 0.5D + (random.nextDouble() - 0.5D) * 0.6D;
                    level.addParticle(
                        (ParticleOptions) com.crabmod.hotbath.registers.ParticleRegister.HOT_WATER_BUBBLE.get(),
                        bx, by, bz, 0.0D, 0.02D, 0.0D);
                }
            }
        }
    }
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    builder.add(FACING, PART, LIQUID, RUNNING);
  }



  @Override
  public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
      LiquidType liquid = state.getValue(LIQUID);

      if (liquid == LiquidType.CUSTOM) {
          BlockEntity be = level.getBlockEntity(pos);
          if (be instanceof BathtubBlockEntity bathtubBe) {
              // Check for custom fluid definition and apply its effects
              Optional<CustomFluidDefinition> defOpt = bathtubBe.getCustomFluidDefinition();
              if (defOpt.isPresent()) {
                  ResourceLocation fluidId = bathtubBe.getCustomFluidId();
                  if (fluidId != null && entity instanceof ServerPlayer serverPlayer && !level.isClientSide) {
                      // Respect triggerTimeSeconds from the custom fluid definition.
                      // Track how long the player has been in this bath and only apply
                      // effects after the configured trigger delay (matching HotBath behavior).
                      CustomFluidDefinition def = defOpt.get();
                      int triggerTimeTicks = def.triggerTimeSeconds() * 20;

                      CompoundTag playerData = serverPlayer.getPersistentData();
                      long currentTick = level.getGameTime();
                      long lastInsideTick = playerData.getLong("showercore.custom_bath_last_tick");

                      int stayedTicks;
                      if (currentTick - lastInsideTick > 5) {
                          // Re-entry or first entry — reset timer
                          stayedTicks = 1;
                      } else {
                          stayedTicks = playerData.getInt("showercore.custom_bath_stayed") + 1;
                      }

                      playerData.putLong("showercore.custom_bath_last_tick", currentTick);
                      playerData.putInt("showercore.custom_bath_stayed", stayedTicks);

                      if (stayedTicks >= triggerTimeTicks) {
                          CustomFluidAPI.applyFluidEffects(serverPlayer, fluidId);
                      }
                  }
              } else {
                  // No custom fluid definition; handle special cases like lava
                  FluidStack fluidStack = bathtubBe.getFluidTank().getFluid();
                  if (!fluidStack.isEmpty()) {
                      Fluid fluid = fluidStack.getFluid();
                      if (fluid == Fluids.LAVA || fluid == Fluids.FLOWING_LAVA) {
                          entity.setRemainingFireTicks(300);
                          entity.hurt(level.damageSources().lava(), 4.0F);
                      }
                  }
              }
          }
      }

      // === Hot bathtub interactions (server-side only) ===
      if (!level.isClientSide && liquid != LiquidType.EMPTY && liquid != LiquidType.WATER) {
          boolean isHot = (liquid != LiquidType.CUSTOM) ||
                  mod.crabmod.showercore.utils.CoreUtils.isCustomFluidHotAt(level, pos);

          if (isHot) {
              // Dirtiness cleaning is handled by DirtinessHandlerMixin injecting into
              // hotBath's isInHotBathFluid() - no direct call needed here to avoid double cleaning.

              // Twilight Forest ice mobs take damage in hot bathtub (1 damage/sec)
              if (entity.tickCount % 20 == 0
                      && mod.crabmod.showercore.compat.ShowerCoreCompat.isTwilightForestIceMob(entity)) {
                  entity.hurt(level.damageSources().magic(), 1.0F);
              }

              // Alex's Caves GummyBear melts in hot bathtub (0.5 damage/sec)
              if (entity.tickCount % 20 == 0
                      && mod.crabmod.showercore.compat.ShowerCoreCompat.isGummyBear(entity)) {
                  entity.hurt(level.damageSources().magic(), 0.5F);
              }
          }
      }

      super.entityInside(state, level, pos, entity);
  }
}
