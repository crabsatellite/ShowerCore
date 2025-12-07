package mod.crabmod.showercore;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = ShowerCore.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientConfig {
  private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

  public static final ForgeConfigSpec.BooleanValue ENABLE_TRANSLUCENT_PARTICLES =
      BUILDER
          .comment("Enable translucent rendering for shower particles")
          .define("enable_translucent_particles", false);

  static final ForgeConfigSpec SPEC = BUILDER.build();

  public static boolean enableTranslucentParticles;

  @SubscribeEvent
  static void onLoad(final ModConfigEvent event) {
    if (event.getConfig().getSpec() == SPEC) {
      enableTranslucentParticles = ENABLE_TRANSLUCENT_PARTICLES.get();
    }
  }
}
