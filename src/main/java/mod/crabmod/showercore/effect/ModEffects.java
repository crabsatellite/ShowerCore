package mod.crabmod.showercore.effect;

import mod.crabmod.showercore.ShowerCore;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ModEffects {
  public static final DeferredRegister<MobEffect> MOB_EFFECTS =
      DeferredRegister.create(Registries.MOB_EFFECT, ShowerCore.MODID);

  public static void register(IEventBus eventBus) {
    MOB_EFFECTS.register(eventBus);
  }
}
