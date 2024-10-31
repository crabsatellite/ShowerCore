package mod.crabmod.showercore.block.bath_core.honey_bath_core;

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

public class HoneyBathCoreBlock extends BaseEntityBlock {
  public static final VoxelShape SHAPE = Block.box(5.0D, 5.0D, 5.0D, 11.0D, 11.0D, 11.0D);

  public HoneyBathCoreBlock(Properties pProperties) {
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
    return new HoneyBathCoreBlockEntity(pPos, pState);
  }

  @Nullable
  @Override
  public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
      Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
    return createTickerHelper(
        pBlockEntityType,
        BlockEntitiesRegister.HONEY_BATH_CORE_BLOCK_ENTITY.get(),
        pLevel.isClientSide
            ? HoneyBathCoreBlockEntity::clientTick
            : HoneyBathCoreBlockEntity::serverTick);
  }
}
