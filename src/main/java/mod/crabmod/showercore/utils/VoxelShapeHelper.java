package mod.crabmod.showercore.utils;

import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class VoxelShapeHelper {

  // This method rotates the given shape based on the direction
  public static VoxelShape rotateShape(VoxelShape shape, Direction direction) {
    VoxelShape[] buffer = new VoxelShape[] {shape, Shapes.empty()};

    int times = 0;
    switch (direction) {
      case EAST:
        times = 1;
        break;
      case SOUTH:
        times = 2;
        break;
      case WEST:
        times = 3;
        break;
      default:
        break;
    }

    for (int i = 0; i < times; i++) {
      buffer[0].forAllBoxes(
          (minX, minY, minZ, maxX, maxY, maxZ) -> {
            buffer[1] =
                Shapes.or(buffer[1], Shapes.box(1 - maxZ, minY, minX, 1 - minZ, maxY, maxX));
          });
      buffer[0] = buffer[1];
      buffer[1] = Shapes.empty();
    }

    return buffer[0];
  }

  // Example of a base shape (a simple full cube)
  public static VoxelShape createBaseShape() {
    // Create a full cube shape (minX, minY, minZ, maxX, maxY, maxZ)
    return Shapes.block();
  }
}
