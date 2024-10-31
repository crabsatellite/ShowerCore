package mod.crabmod.showercore.entity;

import mod.crabmod.showercore.base.BaseShowerHeadContainerBlockEntity;
import mod.crabmod.showercore.registers.BlockEntitiesRegister;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;

public class ShowerHeadContainerEntity extends BaseShowerHeadContainerBlockEntity {

  public ShowerHeadContainerEntity(BlockPos pos, BlockState state) {
    super(BlockEntitiesRegister.SHOWER_HEAD_CONTAINER.get(), pos, state);
  }

  @Override
  protected Component getDefaultName() {
    return Component.translatable("container.shower_head");
  }
}