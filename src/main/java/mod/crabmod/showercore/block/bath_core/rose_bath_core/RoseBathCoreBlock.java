package mod.crabmod.showercore.block.bath_core.rose_bath_core;

import mod.crabmod.showercore.block.bath_core.hot_water_core.HotWaterCoreBlockEntity;
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

public class RoseBathCoreBlock extends BaseEntityBlock {
  public static final VoxelShape SHAPE = Block.box(5.0D, 5.0D, 5.0D, 11.0D, 11.0D, 11.0D);

  public RoseBathCoreBlock(Properties pProperties) {
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

  @Nullable
  @Override
  public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
    return new RoseBathCoreBlockEntity(pPos, pState);
  }

  @Nullable
  @Override
  public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
          Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
    return createTickerHelper(
            pBlockEntityType,
            BlockEntitiesRegister.ROSE_BATH_CORE_BLOCK_ENTITY.get(),
            pLevel.isClientSide
                    ? RoseBathCoreBlockEntity::clientTick
                    : RoseBathCoreBlockEntity::serverTick);
  }
}
