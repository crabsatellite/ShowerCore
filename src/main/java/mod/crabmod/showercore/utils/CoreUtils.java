package mod.crabmod.showercore.utils;

import mod.crabmod.showercore.ShowerCore;
import mod.crabmod.showercore.registers.ParticleRegister;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

public class CoreUtils {

    public static boolean isCoreItem(ItemStack stack) {
        if (stack.isEmpty()) return false;
        ResourceLocation registryName = ForgeRegistries.ITEMS.getKey(stack.getItem());
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
        
        ResourceLocation registryName = ForgeRegistries.ITEMS.getKey(stack.getItem());
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
