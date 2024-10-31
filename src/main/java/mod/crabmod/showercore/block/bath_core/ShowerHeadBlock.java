package mod.crabmod.showercore.block.bath_core;

import mod.crabmod.showercore.base.RotatableBlock;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ShowerHeadBlock extends RotatableBlock {

  public ShowerHeadBlock(Properties properties) {
    super(properties);
  }

  @Override
  protected VoxelShape getBaseShape() {
    return Shapes.or(box(4.5, -6, 4, 11.5, 25, 16));
  }
}
