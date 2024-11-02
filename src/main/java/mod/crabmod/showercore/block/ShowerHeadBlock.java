package mod.crabmod.showercore.block;

import java.awt.*;
import mod.crabmod.showercore.base.RotatableBlock;
import mod.crabmod.showercore.entity.ShowerHeadContainerEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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

    if (level.isClientSide) {
      if (!isShiftDown) {
        for (int i = 0; i < 20; i++) {
          double offsetX = 0.5 + (level.random.nextDouble() - 0.5) * 0.6;
          double offsetY = -0.2;
          double offsetZ = 0.5 + (level.random.nextDouble() - 0.5) * 0.6;
          level.addParticle(
              ParticleTypes.RAIN,
              pos.getX() + offsetX,
              pos.getY() + offsetY,
              pos.getZ() + offsetZ,
              0,
              -0.1,
              0);
        }

        level.playLocalSound(
            pos.getX(),
            pos.getY(),
            pos.getZ(),
            SoundEvents.WEATHER_RAIN,
            SoundSource.BLOCKS,
            1.0F,
            1.0F,
            false);

        return InteractionResult.SUCCESS;
      } else {
        return InteractionResult.PASS;
      }
    }

    if (isShiftDown) { // Shift + 右键：打开Menu
      BlockEntity blockEntity = level.getBlockEntity(pos);
      if (blockEntity instanceof ShowerHeadContainerEntity) {
        player.openMenu((MenuProvider) blockEntity);
        return InteractionResult.CONSUME;
      }
    }

    return InteractionResult.PASS;
  }
}
