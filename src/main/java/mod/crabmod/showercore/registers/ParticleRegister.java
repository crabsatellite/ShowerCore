package mod.crabmod.showercore.registers;

import mod.crabmod.showercore.ShowerCore;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ParticleRegister {
  public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES =
      DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, ShowerCore.MODID);

  public static final RegistryObject<SimpleParticleType> BATH_CORE_PARTICLE =
      PARTICLE_TYPES.register("bath_core_particle", () -> new SimpleParticleType(true));

  public static final RegistryObject<SimpleParticleType> HOT_WATER_SHOWER_PARTICLE =
      PARTICLE_TYPES.register("hot_water_shower_particle", () -> new SimpleParticleType(true));

  public static final RegistryObject<SimpleParticleType> HERBAL_BATH_SHOWER_PARTICLE =
      PARTICLE_TYPES.register("herbal_bath_shower_particle", () -> new SimpleParticleType(true));

  public static final RegistryObject<SimpleParticleType> HONEY_BATH_SHOWER_PARTICLE =
      PARTICLE_TYPES.register("honey_bath_shower_particle", () -> new SimpleParticleType(true));

  public static final RegistryObject<SimpleParticleType> MILK_BATH_SHOWER_PARTICLE =
      PARTICLE_TYPES.register("milk_bath_shower_particle", () -> new SimpleParticleType(true));

  public static final RegistryObject<SimpleParticleType> PEONY_BATH_SHOWER_PARTICLE =
      PARTICLE_TYPES.register("peony_bath_shower_particle", () -> new SimpleParticleType(true));

  public static final RegistryObject<SimpleParticleType> ROSE_BATH_SHOWER_PARTICLE =
      PARTICLE_TYPES.register("rose_bath_shower_particle", () -> new SimpleParticleType(true));

  public static void register(IEventBus eventBus) {
    PARTICLE_TYPES.register(eventBus);
  }
}
