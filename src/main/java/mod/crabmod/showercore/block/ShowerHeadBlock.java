// ShowerHeadBlock.java
package mod.crabmod.showercore.block;

import mod.crabmod.showercore.base.RotatableBlock;
import mod.crabmod.showercore.entity.ShowerHeadContainerEntity;
import mod.crabmod.showercore.utils.BathEffectUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
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

    boolean isShiftDown = player.isShiftKeyDown();

    if (!player.getMainHandItem().isEmpty()) {
      return InteractionResult.PASS;
    }

    BlockEntity blockEntity = level.getBlockEntity(pos);
    if (!(blockEntity instanceof ShowerHeadContainerEntity showerEntity)) {
      return InteractionResult.PASS;
    }

    if (level.isClientSide) {
      if (!isShiftDown) {
        showerEntity.toggleEffect();
        BathEffectUtils bathEffectUtils = showerEntity.getBathEffectUtils();

        if (showerEntity.isEffectActive()) {
          bathEffectUtils.renderBathWater(level, pos);
        } else {
          bathEffectUtils.stopBathEffect();
        }
        return InteractionResult.SUCCESS;
      } else {
        return InteractionResult.PASS;
      }
    }

    if (isShiftDown) {
      // 打开交互界面
      player.openMenu((MenuProvider) showerEntity);
      return InteractionResult.CONSUME;
    }

    return InteractionResult.PASS;
  }
}
