package mod.crabmod.showercore.registers;

import mod.crabmod.showercore.ShowerCore;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public class SoundRegister {
  // Create the DeferredRegister for SoundEvents
  public static final DeferredRegister<SoundEvent> SOUNDS =
      DeferredRegister.create(Registries.SOUND_EVENT, ShowerCore.MODID);

  public static final DeferredHolder<SoundEvent, SoundEvent> RUBBER_DUCK =
      SOUNDS.register(
          "rubber_duck", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(ShowerCore.MODID, "rubber_duck")));

  public static void register(IEventBus eventBus) {
    SOUNDS.register(eventBus);
  }
}
