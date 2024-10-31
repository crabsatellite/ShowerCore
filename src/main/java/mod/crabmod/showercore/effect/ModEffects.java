package mod.crabmod.showercore.effect;

import mod.crabmod.showercore.ShowerCore;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEffects {
  public static final DeferredRegister<MobEffect> MOB_EFFECTS =
      DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, ShowerCore.MODID);

  public static void register(IEventBus eventBus) {
    MOB_EFFECTS.register(eventBus);
  }
}
