package mod.crabmod.showercore;

import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = ShowerCore.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ClientConfig {
  private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

  public static final ModConfigSpec.BooleanValue ENABLE_TRANSLUCENT_PARTICLES =
      BUILDER
          .comment("Enable translucent rendering for shower particles")
          .define("enable_translucent_particles", false);

  static final ModConfigSpec SPEC = BUILDER.build();

  public static boolean enableTranslucentParticles;

  @SubscribeEvent
  static void onLoad(final ModConfigEvent event) {
    if (event.getConfig().getSpec() == SPEC) {
      enableTranslucentParticles = ENABLE_TRANSLUCENT_PARTICLES.get();
    }
  }
}
