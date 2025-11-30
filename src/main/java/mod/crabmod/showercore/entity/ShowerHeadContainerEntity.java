package mod.crabmod.showercore.entity;

import mod.crabmod.showercore.base.BaseShowerHeadBlockEntity;
import mod.crabmod.showercore.registers.BlockEntitiesRegister;
import mod.crabmod.showercore.utils.BathEffectUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;

public class ShowerHeadContainerEntity extends BaseShowerHeadBlockEntity {
  private boolean effectActive = false;
  private final BathEffectUtils bathEffectUtils;

  public ShowerHeadContainerEntity(BlockPos pos, BlockState state) {
    super(BlockEntitiesRegister.SHOWER_HEAD_CONTAINER.get(), pos, state);
    this.bathEffectUtils = new BathEffectUtils();
  }

  @Override
  protected Component getDefaultName() {
    return Component.translatable("container.shower_head");
  }

  // 切换效果状态的方法
  public void toggleEffect() {
    effectActive = !effectActive;
  }

  // 获取当前效果状态的方法
  public boolean isEffectActive() {
    return effectActive;
  }

  public BathEffectUtils getBathEffectUtils() {
    return this.bathEffectUtils;
  }

  public net.minecraft.core.particles.SimpleParticleType getParticleType() {
    if (this.isEmpty()) {
      return mod.crabmod.showercore.registers.ParticleRegister.HOT_WATER_SHOWER_PARTICLE.get();
    }
    net.minecraft.world.item.Item item = this.getItem(0).getItem();
    if (item == mod.crabmod.showercore.registers.BlocksRegister.HERBAL_BATH_CORE.get().asItem()) {
      return mod.crabmod.showercore.registers.ParticleRegister.HERBAL_BATH_SHOWER_PARTICLE.get();
    }
    if (item == mod.crabmod.showercore.registers.BlocksRegister.HONEY_BATH_CORE.get().asItem()) {
      return mod.crabmod.showercore.registers.ParticleRegister.HONEY_BATH_SHOWER_PARTICLE.get();
    }
    if (item == mod.crabmod.showercore.registers.BlocksRegister.MILK_BATH_CORE.get().asItem()) {
      return mod.crabmod.showercore.registers.ParticleRegister.MILK_BATH_SHOWER_PARTICLE.get();
    }
    if (item == mod.crabmod.showercore.registers.BlocksRegister.PEONY_BATH_CORE.get().asItem()) {
      return mod.crabmod.showercore.registers.ParticleRegister.PEONY_BATH_SHOWER_PARTICLE.get();
    }
    if (item == mod.crabmod.showercore.registers.BlocksRegister.ROSE_BATH_CORE.get().asItem()) {
      return mod.crabmod.showercore.registers.ParticleRegister.ROSE_BATH_SHOWER_PARTICLE.get();
    }
    return mod.crabmod.showercore.registers.ParticleRegister.HOT_WATER_SHOWER_PARTICLE.get();
  }

  @Override
  public net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket getUpdatePacket() {
    return net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket.create(this);
  }

  @Override
  public net.minecraft.nbt.CompoundTag getUpdateTag() {
    return this.saveWithoutMetadata();
  }

  @Override
  public void load(net.minecraft.nbt.CompoundTag tag) {
    super.load(tag);
    this.effectActive = tag.getBoolean("EffectActive");
  }

  @Override
  protected void saveAdditional(net.minecraft.nbt.CompoundTag tag) {
    super.saveAdditional(tag);
    tag.putBoolean("EffectActive", this.effectActive);
  }
}
