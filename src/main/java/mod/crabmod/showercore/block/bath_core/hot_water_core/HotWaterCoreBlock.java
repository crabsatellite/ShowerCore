package mod.crabmod.showercore.block.bath_core.hot_water_core;

import com.mojang.serialization.MapCodec;

import mod.crabmod.showercore.registers.BlockEntitiesRegister;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import java.util.Collections;
import java.util.List;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;

public class HotWaterCoreBlock extends BaseEntityBlock {
  public static final MapCodec<HotWaterCoreBlock> CODEC = simpleCodec(HotWaterCoreBlock::new);

  @Override
  protected MapCodec<? extends BaseEntityBlock> codec() {
     return CODEC;
  }

  public static final VoxelShape SHAPE = Block.box(5.0D, 5.0D, 5.0D, 11.0D, 11.0D, 11.0D);

  public HotWaterCoreBlock(Properties pProperties) {
    super(pProperties);
  }

  @Override
  public VoxelShape getShape(
      BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
    return SHAPE;
  }

  @Override
  public RenderShape getRenderShape(BlockState pState) {
    return RenderShape.MODEL;
  }

  @Override
  public List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
    BlockEntity blockEntity = params.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
    if (blockEntity instanceof HotWaterCoreBlockEntity coreEntity) {
      ItemStack stack = new ItemStack(this);
      CompoundTag tag = coreEntity.saveWithoutMetadata(coreEntity.getLevel().registryAccess());
      BlockItem.setBlockEntityData(stack, blockEntity.getType(), tag);
      return Collections.singletonList(stack);
    }
    return super.getDrops(state, params);
  }

  @Nullable
  @Override
  public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
    return new HotWaterCoreBlockEntity(pPos, pState);
  }

  @Nullable
  @Override
  public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
      Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
    return createTickerHelper(
        pBlockEntityType,
        BlockEntitiesRegister.HOT_WATER_CORE_BLOCK_ENTITY.get(),
        pLevel.isClientSide
            ? HotWaterCoreBlockEntity::clientTick
            : HotWaterCoreBlockEntity::serverTick);
  }
}

