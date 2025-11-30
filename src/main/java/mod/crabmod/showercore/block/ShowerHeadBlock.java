// ShowerHeadBlock.java
package mod.crabmod.showercore.block;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import mod.crabmod.showercore.base.RotatableBlock;
import mod.crabmod.showercore.entity.ShowerHeadContainerEntity;
import mod.crabmod.showercore.utils.BathEffectUtils;
import mod.crabmod.showercore.utils.CoreUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import mod.crabmod.showercore.registers.BlockEntitiesRegister;

public class ShowerHeadBlock extends RotatableBlock implements EntityBlock {

  public ShowerHeadBlock(Properties properties) {
    super(properties);
  }

  @javax.annotation.Nullable
  @Override
  public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
    return type == BlockEntitiesRegister.SHOWER_HEAD_CONTAINER.get() ? (lvl, pos, st, be) -> ShowerHeadContainerEntity.tick(lvl, pos, st, (ShowerHeadContainerEntity) be) : null;
  }

  @Override
  protected VoxelShape getBaseShape() {
    return Shapes.or(box(2.75, 0, 5.5, 13, 31, 16));
  }

  @Override
  public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return new ShowerHeadContainerEntity(pos, state);
  }

  @Override
  public InteractionResult use(
      BlockState state,
      Level level,
      BlockPos pos,
      Player player,
      InteractionHand hand,
      BlockHitResult hit) {

    net.minecraft.world.item.ItemStack heldItem = player.getItemInHand(hand);

    BlockEntity blockEntity = level.getBlockEntity(pos);
    if (!(blockEntity instanceof ShowerHeadContainerEntity showerEntity)) {
      return InteractionResult.PASS;
    }

    if (CoreUtils.isCoreItem(heldItem)) {
      if (!level.isClientSide) {
        net.minecraft.world.item.ItemStack existingItem = showerEntity.getItem(0);
        if (!existingItem.isEmpty()) {
          net.minecraft.world.level.block.Block.popResource(level, pos, existingItem.copy());
        }

        net.minecraft.world.item.ItemStack copy = heldItem.copy();
        copy.setCount(1);
        showerEntity.setItem(0, copy);
        showerEntity.setChanged();
        level.sendBlockUpdated(pos, state, state, 3);
        if (!player.isCreative()) {
          heldItem.shrink(1);
        }
        // Optional: Play a sound to indicate success
        level.playSound(null, pos, net.minecraft.sounds.SoundEvents.ITEM_FRAME_ADD_ITEM, net.minecraft.sounds.SoundSource.BLOCKS, 1.0F, 1.0F);
      }
      return InteractionResult.sidedSuccess(level.isClientSide);
    }

    if (heldItem.isEmpty()) {
      // Shift + Empty Hand: Remove Core
      if (player.isShiftKeyDown()) {
        if (!showerEntity.isEmpty()) {
          if (!level.isClientSide) {
            net.minecraft.world.item.ItemStack existingItem = showerEntity.getItem(0);
            net.minecraft.world.level.block.Block.popResource(level, pos, existingItem.copy());
            showerEntity.setItem(0, net.minecraft.world.item.ItemStack.EMPTY);
            
            if (showerEntity.isEffectActive()) {
              showerEntity.toggleEffect();
            }
            
            showerEntity.setChanged();
            level.sendBlockUpdated(pos, state, state, 3);
            level.playSound(null, pos, net.minecraft.sounds.SoundEvents.ITEM_FRAME_REMOVE_ITEM, net.minecraft.sounds.SoundSource.BLOCKS, 1.0F, 1.0F);
          } else {
            if (showerEntity.isEffectActive()) {
              showerEntity.toggleEffect();
              showerEntity.getBathEffectUtils().stopBathEffect();
            }
          }
          return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return InteractionResult.PASS;
      }

      if (showerEntity.isEmpty() && !showerEntity.isEffectActive()) {
        if (level.isClientSide) {
          player.displayClientMessage(net.minecraft.network.chat.Component.translatable("message.showercore.need_core"), true);
        }
        return InteractionResult.SUCCESS;
      }

      showerEntity.toggleEffect();
      if (level.isClientSide) {
        BathEffectUtils bathEffectUtils = showerEntity.getBathEffectUtils();

        if (showerEntity.isEffectActive()) {
          bathEffectUtils.renderBathWater(level, pos, showerEntity::getParticleType);
        } else {
          bathEffectUtils.stopBathEffect();
        }
        return InteractionResult.SUCCESS;
      }
      
      showerEntity.setChanged();
      level.sendBlockUpdated(pos, state, state, 3);
      return InteractionResult.CONSUME;
    }

    return InteractionResult.PASS;
  }

  @Override
  public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
    if (!state.is(newState.getBlock())) {
      BlockEntity blockEntity = level.getBlockEntity(pos);
      if (blockEntity instanceof ShowerHeadContainerEntity showerEntity) {
        if (level.isClientSide) {
          showerEntity.getBathEffectUtils().shutdown();
        }
      }
      super.onRemove(state, level, pos, newState, isMoving);
    }
  }

  @Override
  public List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
    BlockEntity blockEntity = params.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
    if (blockEntity instanceof ShowerHeadContainerEntity showerEntity) {
      ItemStack stack = new ItemStack(this);
      CompoundTag tag = showerEntity.saveWithoutMetadata();
      BlockItem.setBlockEntityData(stack, blockEntity.getType(), tag);
      return Collections.singletonList(stack);
    }
    return super.getDrops(state, params);
  }

  @Override
  public void appendHoverText(
      ItemStack stack,
      @Nullable BlockGetter level,
      List<Component> tooltip,
      TooltipFlag flag) {
    
    CompoundTag tag = BlockItem.getBlockEntityData(stack);
    if (tag != null) {
        if (tag.contains("Items")) {
            net.minecraft.core.NonNullList<ItemStack> items = net.minecraft.core.NonNullList.withSize(1, ItemStack.EMPTY);
            ContainerHelper.loadAllItems(tag, items);
            ItemStack core = items.get(0);
            if (!core.isEmpty()) {
                 tooltip.add(Component.translatable("tooltip.showercore.contains", core.getHoverName()).withStyle(ChatFormatting.GRAY));
            }
        }
    }
    
    tooltip.add(Component.translatable("tooltip.showercore.shower_head.usage.install"));
    tooltip.add(Component.translatable("tooltip.showercore.shower_head.usage.toggle"));
    tooltip.add(Component.translatable("tooltip.showercore.shower_head.usage.remove"));
    super.appendHoverText(stack, level, tooltip, flag);
  }
}
