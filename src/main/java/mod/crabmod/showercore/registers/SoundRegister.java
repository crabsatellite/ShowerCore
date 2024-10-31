package mod.crabmod.showercore.registers;

import mod.crabmod.showercore.ShowerCore;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class SoundRegister {
  // Create the DeferredRegister for SoundEvents
  public static final DeferredRegister<SoundEvent> SOUNDS =
      DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, ShowerCore.MODID);
}
