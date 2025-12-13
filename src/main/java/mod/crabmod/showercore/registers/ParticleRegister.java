package mod.crabmod.showercore.registers;

import mod.crabmod.showercore.ShowerCore;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ParticleRegister {
  public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES =
      DeferredRegister.create(Registries.PARTICLE_TYPE, ShowerCore.MODID);

  public static final DeferredHolder<ParticleType<?>, SimpleParticleType> BATH_CORE_PARTICLE =
      PARTICLE_TYPES.register("bath_core_particle", () -> new SimpleParticleType(true));

  public static final DeferredHolder<ParticleType<?>, SimpleParticleType> HERBAL_BATH_BATH_CORE_PARTICLE =
      PARTICLE_TYPES.register("herbal_bath_bath_core_particle", () -> new SimpleParticleType(true));

  public static final DeferredHolder<ParticleType<?>, SimpleParticleType> HOT_WATER_BATH_CORE_PARTICLE =
      PARTICLE_TYPES.register("hot_water_bath_core_particle", () -> new SimpleParticleType(true));

  public static final DeferredHolder<ParticleType<?>, SimpleParticleType> HONEY_BATH_BATH_CORE_PARTICLE =
      PARTICLE_TYPES.register("honey_bath_bath_core_particle", () -> new SimpleParticleType(true));

  public static final DeferredHolder<ParticleType<?>, SimpleParticleType> MILK_BATH_BATH_CORE_PARTICLE =
      PARTICLE_TYPES.register("milk_bath_bath_core_particle", () -> new SimpleParticleType(true));

  public static final DeferredHolder<ParticleType<?>, SimpleParticleType> PEONY_BATH_BATH_CORE_PARTICLE =
      PARTICLE_TYPES.register("peony_bath_bath_core_particle", () -> new SimpleParticleType(true));

  public static final DeferredHolder<ParticleType<?>, SimpleParticleType> ROSE_BATH_BATH_CORE_PARTICLE =
      PARTICLE_TYPES.register("rose_bath_bath_core_particle", () -> new SimpleParticleType(true));

  public static final DeferredHolder<ParticleType<?>, SimpleParticleType> HOT_WATER_SHOWER_PARTICLE =
      PARTICLE_TYPES.register("hot_water_shower_particle", () -> new SimpleParticleType(true));

  public static final DeferredHolder<ParticleType<?>, SimpleParticleType> HERBAL_BATH_SHOWER_PARTICLE =
      PARTICLE_TYPES.register("herbal_bath_shower_particle", () -> new SimpleParticleType(true));

  public static final DeferredHolder<ParticleType<?>, SimpleParticleType> HONEY_BATH_SHOWER_PARTICLE =
      PARTICLE_TYPES.register("honey_bath_shower_particle", () -> new SimpleParticleType(true));

  public static final DeferredHolder<ParticleType<?>, SimpleParticleType> MILK_BATH_SHOWER_PARTICLE =
      PARTICLE_TYPES.register("milk_bath_shower_particle", () -> new SimpleParticleType(true));

  public static final DeferredHolder<ParticleType<?>, SimpleParticleType> PEONY_BATH_SHOWER_PARTICLE =
      PARTICLE_TYPES.register("peony_bath_shower_particle", () -> new SimpleParticleType(true));

  public static final DeferredHolder<ParticleType<?>, SimpleParticleType> ROSE_BATH_SHOWER_PARTICLE =
      PARTICLE_TYPES.register("rose_bath_shower_particle", () -> new SimpleParticleType(true));

  public static void register(IEventBus eventBus) {
    PARTICLE_TYPES.register(eventBus);
  }
}
