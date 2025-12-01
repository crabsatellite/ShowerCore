package mod.crabmod.showercore.registers;

import mod.crabmod.showercore.ShowerCore;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.RegistryObject;

public class SoundRegister {
  // Create the DeferredRegister for SoundEvents
  public static final DeferredRegister<SoundEvent> SOUNDS =
      DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, ShowerCore.MODID);

  public static final RegistryObject<SoundEvent> RUBBER_DUCK =
      SOUNDS.register(
          "rubber_duck", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(ShowerCore.MODID, "rubber_duck")));

  public static void register(IEventBus eventBus) {
    SOUNDS.register(eventBus);
  }
}
