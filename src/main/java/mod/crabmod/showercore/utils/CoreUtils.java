package mod.crabmod.showercore.utils;

import mod.crabmod.showercore.ShowerCore;
import mod.crabmod.showercore.block.BathtubBlock;
import net.minecraft.world.level.block.state.properties.BedPart;
import mod.crabmod.showercore.registers.ParticleRegister;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.registries.BuiltInRegistries;

import net.minecraft.world.level.Level;
import mod.crabmod.showercore.entity.SeatEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.Direction;

public class CoreUtils {

    public static boolean isEntityInBathtub(Entity entity, BathtubBlock.LiquidType liquidType) {
        if (entity.getVehicle() instanceof SeatEntity) {
             return checkBathtubAt(entity.level(), entity.getVehicle().blockPosition(), liquidType);
        }
        
        BlockPos pos = entity.blockPosition();
        if (checkBathtubAt(entity.level(), pos, liquidType)) {
            return isEntityInLiquidBounds(entity, pos);
        }
        return false;
    }

    public static boolean isEntityInAnyHotBathtub(Entity entity) {
        if (entity.getVehicle() instanceof SeatEntity) {
             return checkAnyHotBathtubAt(entity.level(), entity.getVehicle().blockPosition());
        }
        
        BlockPos pos = entity.blockPosition();
        if (checkAnyHotBathtubAt(entity.level(), pos)) {
            return isEntityInLiquidBounds(entity, pos);
        }
        return false;
    }

    private static boolean isEntityInLiquidBounds(Entity entity, BlockPos pos) {
        BlockState state = entity.level().getBlockState(pos);
        if (!(state.getBlock() instanceof BathtubBlock)) return false;
        
        AABB liquidBox = getLiquidBox(state, pos);
        return entity.getBoundingBox().intersects(liquidBox);
    }

    private static AABB getLiquidBox(BlockState state, BlockPos pos) {
        Direction facing = state.getValue(BathtubBlock.FACING);
        BedPart part = state.getValue(BathtubBlock.PART);
        
        double minX = 0.125;
        double maxX = 0.875;
        double minZ = 0.125;
        double maxZ = 0.875;
        double minY = 0.25;
        double maxY = 0.7; // Slightly less than 0.75 to avoid rim standing

        if (part == BedPart.FOOT) {
            switch (facing) {
                case NORTH: minZ = 0.0; break;
                case SOUTH: maxZ = 1.0; break;
                case WEST:  minX = 0.0; break;
                case EAST:  maxX = 1.0; break;
                default: break;
            }
        } else {
            switch (facing) {
                case NORTH: maxZ = 1.0; break;
                case SOUTH: minZ = 0.0; break;
                case WEST:  maxX = 1.0; break;
                case EAST:  minX = 0.0; break;
                default: break;
            }
        }
        
        return new AABB(pos.getX() + minX, pos.getY() + minY, pos.getZ() + minZ,
                        pos.getX() + maxX, pos.getY() + maxY, pos.getZ() + maxZ);
    }

    private static boolean checkBathtubAt(Level level, BlockPos pos, BathtubBlock.LiquidType liquidType) {
        BlockState state = level.getBlockState(pos);
        if (state.getBlock() instanceof BathtubBlock) {
            return state.getValue(BathtubBlock.LIQUID) == liquidType;
        }
        return false;
    }

    private static boolean checkAnyHotBathtubAt(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (state.getBlock() instanceof BathtubBlock) {
            BathtubBlock.LiquidType type = state.getValue(BathtubBlock.LIQUID);
            return type != BathtubBlock.LiquidType.EMPTY && type != BathtubBlock.LiquidType.WATER;
        }
        return false;
    }

    public static boolean isCoreItem(ItemStack stack) {
        if (stack.isEmpty()) return false;
        ResourceLocation registryName = BuiltInRegistries.ITEM.getKey(stack.getItem());
        if (registryName != null && registryName.getNamespace().equals(ShowerCore.MODID)) {
             String path = registryName.getPath();
             return isCorePath(path);
        }
        return false;
    }

    private static boolean isCorePath(String path) {
        return path.equals("hot_water_core") ||
               path.equals("herbal_bath_core") ||
               path.equals("peony_bath_core") ||
               path.equals("rose_bath_core") ||
               path.equals("milk_bath_core") ||
               path.equals("honey_bath_core");
    }

    public static SimpleParticleType getParticleForCore(ItemStack stack) {
        if (stack.isEmpty()) {
            return ParticleRegister.HOT_WATER_SHOWER_PARTICLE.get();
        }
        
        ResourceLocation registryName = BuiltInRegistries.ITEM.getKey(stack.getItem());
        if (registryName != null && registryName.getNamespace().equals(ShowerCore.MODID)) {
            String path = registryName.getPath();
            switch (path) {
                case "herbal_bath_core":
                    return ParticleRegister.HERBAL_BATH_SHOWER_PARTICLE.get();
                case "honey_bath_core":
                    return ParticleRegister.HONEY_BATH_SHOWER_PARTICLE.get();
                case "milk_bath_core":
                    return ParticleRegister.MILK_BATH_SHOWER_PARTICLE.get();
                case "peony_bath_core":
                    return ParticleRegister.PEONY_BATH_SHOWER_PARTICLE.get();
                case "rose_bath_core":
                    return ParticleRegister.ROSE_BATH_SHOWER_PARTICLE.get();
                case "hot_water_core":
                default:
                    return ParticleRegister.HOT_WATER_SHOWER_PARTICLE.get();
            }
        }
        return ParticleRegister.HOT_WATER_SHOWER_PARTICLE.get();
    }
}
