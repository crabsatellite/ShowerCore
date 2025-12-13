package mod.crabmod.showercore.base;

import java.util.EnumMap;
import java.util.Map;
import mod.crabmod.showercore.utils.VoxelShapeHelper;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.phys.shapes.VoxelShape;
import com.mojang.serialization.MapCodec;

public class RotatableBlock extends BaseRotatableBlock {
  public static final MapCodec<RotatableBlock> CODEC = simpleCodec(RotatableBlock::new);

  @Override
  protected MapCodec<? extends BaseRotatableBlock> codec() {
     return CODEC;
  }

  public RotatableBlock(Properties properties) {
    super(properties);
  }

  @Override
  protected VoxelShape getBaseShape() {
    // Return the base shape (NORTH direction shape), to be provided by the subclass
    return VoxelShapeHelper.createBaseShape();
  }

  @Override
  protected Map<Direction, VoxelShape> createShapeMap(VoxelShape baseShape) {
    // Create the shape map with clockwise rotation
    Map<Direction, VoxelShape> shapeMap = new EnumMap<>(Direction.class);
    shapeMap.put(Direction.NORTH, baseShape);
    shapeMap.put(Direction.EAST, VoxelShapeHelper.rotateShape(baseShape, Direction.EAST));
    shapeMap.put(Direction.SOUTH, VoxelShapeHelper.rotateShape(baseShape, Direction.SOUTH));
    shapeMap.put(Direction.WEST, VoxelShapeHelper.rotateShape(baseShape, Direction.WEST));
    return shapeMap;
  }
}
