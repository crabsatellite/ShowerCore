// ShowerHeadBlock.java
package mod.crabmod.showercore.block;

import mod.crabmod.showercore.base.RotatableBlock;
import mod.crabmod.showercore.entity.ShowerHeadContainerEntity;
import mod.crabmod.showercore.utils.BathEffectUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ShowerHeadBlock extends RotatableBlock implements EntityBlock {

  public ShowerHeadBlock(Properties properties) {
    super(properties);
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

    if (isCoreItem(heldItem)) {
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
        return InteractionResult.SUCCESS;
      }

      showerEntity.toggleEffect();
      if (level.isClientSide) {
        BathEffectUtils bathEffectUtils = showerEntity.getBathEffectUtils();

        if (showerEntity.isEffectActive()) {
          bathEffectUtils.renderBathWater(level, pos, showerEntity.getParticleType());
        } else {
          bathEffectUtils.stopBathEffect();
        }
        return InteractionResult.SUCCESS;
      }
      return InteractionResult.CONSUME;
    }

    return InteractionResult.PASS;
  }

  private boolean isCoreItem(net.minecraft.world.item.ItemStack stack) {
    if (stack.getItem() instanceof net.minecraft.world.item.BlockItem blockItem) {
      net.minecraft.resources.ResourceLocation registryName = net.minecraftforge.registries.ForgeRegistries.BLOCKS.getKey(blockItem.getBlock());
      if (registryName != null && registryName.getNamespace().equals(mod.crabmod.showercore.ShowerCore.MODID)) {
        String path = registryName.getPath();
        return path.equals("hot_water_core") ||
               path.equals("herbal_bath_core") ||
               path.equals("peony_bath_core") ||
               path.equals("rose_bath_core") ||
               path.equals("milk_bath_core") ||
               path.equals("honey_bath_core");
      }
    }
    return false;
  }
}
