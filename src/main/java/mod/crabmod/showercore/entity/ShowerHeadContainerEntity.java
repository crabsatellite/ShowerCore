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
}
