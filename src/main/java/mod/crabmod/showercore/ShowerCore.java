package mod.crabmod.showercore;

import com.crabmod.hotbath.item.ItemGroup;
import com.mojang.logging.LogUtils;
import mod.crabmod.showercore.compat.CompatManager;
import mod.crabmod.showercore.compat.ColdSweatCompat;
import mod.crabmod.showercore.compat.ColdSweatIntegration;
import mod.crabmod.showercore.compat.LSOCompat;
import mod.crabmod.showercore.compat.LSOIntegration;
import mod.crabmod.showercore.compat.ToughAsNailsCompat;
import mod.crabmod.showercore.compat.ToughAsNailsIntegration;
import mod.crabmod.showercore.effect.ModEffects;
import mod.crabmod.showercore.event.ClientEvent;
import mod.crabmod.showercore.registers.BlockEntitiesRegister;
import mod.crabmod.showercore.registers.BlocksRegister;
import mod.crabmod.showercore.registers.EntityRegister;
import mod.crabmod.showercore.registers.ItemRegister;
import mod.crabmod.showercore.registers.ParticleRegister;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

import mod.crabmod.showercore.registers.SoundRegister;

@Mod(ShowerCore.MODID)
public class ShowerCore {

  public static final String MODID = "showercore";
  private static final Logger LOGGER = LogUtils.getLogger();

  public ShowerCore() {
    IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

    ItemRegister.register(modEventBus);
    BlocksRegister.register(modEventBus);

    ModEffects.register(modEventBus);

    BlockEntitiesRegister.register(modEventBus);
    EntityRegister.register(modEventBus);
    ParticleRegister.register(modEventBus);
    SoundRegister.register(modEventBus);

    modEventBus.addListener(this::commonSetup);

    MinecraftForge.EVENT_BUS.register(this);
    modEventBus.addListener(this::addCreative);

    ModLoadingContext.get()
        .registerConfig(ModConfig.Type.COMMON, mod.crabmod.showercore.Config.SPEC);
    ModLoadingContext.get()
        .registerConfig(ModConfig.Type.CLIENT, mod.crabmod.showercore.ClientConfig.SPEC);
  }

  private void commonSetup(final FMLCommonSetupEvent event) {
    LOGGER.info("ShowerCore common setup starting...");

    // Skip all mod integrations if disabled in config
    if (!Config.isModIntegrationsEnabled()) {
      LOGGER.info("Mod integrations disabled in config - skipping all mod integrations.");
      return;
    }

    // Register all compat modules with the CompatManager
    registerCompatModules();

    // Initialize all registered compats safely
    CompatManager.initializeAll();
  }

  /**
   * Register all compatibility modules with the CompatManager.
   * Each module is registered with its mod ID, display name, load check, and initializer.
   */
  private void registerCompatModules() {
    CompatManager.registerCompat(
        "cold_sweat",
        "Cold Sweat",
        ColdSweatIntegration::isColdSweatLoaded,
        ColdSweatCompat::init,
        "com.momosoftworks.coldsweat.api.temperature.modifier.TempModifier",
        "com.momosoftworks.coldsweat.api.util.Temperature"
    );

    CompatManager.registerCompat(
        "toughasnails",
        "Tough As Nails",
        ToughAsNailsIntegration::isToughAsNailsLoaded,
        ToughAsNailsCompat::init,
        "toughasnails.api.temperature.TemperatureHelper",
        "toughasnails.api.temperature.TemperatureLevel",
        "toughasnails.api.thirst.ThirstHelper"
    );

    CompatManager.registerCompat(
        "legendarysurvivaloverhaul",
        "Legendary Survival Overhaul",
        LSOIntegration::isLSOLoaded,
        LSOCompat::init,
        "sfiomn.legendarysurvivaloverhaul.api.temperature.TemperatureUtil",
        "sfiomn.legendarysurvivaloverhaul.api.thirst.ThirstUtil"
    );
  }

  private void addCreative(BuildCreativeModeTabContentsEvent event) {
    try {
      if (event.getTabKey() != ItemGroup.HOT_BATH_TAB.getKey()) {
        return;
      }
    } catch (Throwable e) {
      LOGGER.error("Failed to access Hot Bath creative tab - Hot Bath mod may not be installed: {}", e.getMessage());
      return;
    }

    try {
      event.accept(BlocksRegister.HOT_WATER_CORE.get());
      event.accept(BlocksRegister.HERBAL_BATH_CORE.get());
      event.accept(BlocksRegister.PEONY_BATH_CORE.get());
      event.accept(BlocksRegister.ROSE_BATH_CORE.get());
      event.accept(BlocksRegister.MILK_BATH_CORE.get());
      event.accept(BlocksRegister.HONEY_BATH_CORE.get());
      event.accept(BlocksRegister.RAIN_SHOWER_HEAD_BLACK.get());
      event.accept(BlocksRegister.RAIN_SHOWER_HEAD_WHITE.get());
      event.accept(BlocksRegister.RAIN_SHOWER_HEAD_ORANGE.get());
      event.accept(BlocksRegister.RAIN_SHOWER_HEAD_MAGENTA.get());
      event.accept(BlocksRegister.RAIN_SHOWER_HEAD_LIGHT_BLUE.get());
      event.accept(BlocksRegister.RAIN_SHOWER_HEAD_YELLOW.get());
      event.accept(BlocksRegister.RAIN_SHOWER_HEAD_LIME.get());
      event.accept(BlocksRegister.RAIN_SHOWER_HEAD_PINK.get());
      event.accept(BlocksRegister.RAIN_SHOWER_HEAD_GRAY.get());
      event.accept(BlocksRegister.RAIN_SHOWER_HEAD_LIGHT_GRAY.get());
      event.accept(BlocksRegister.RAIN_SHOWER_HEAD_CYAN.get());
      event.accept(BlocksRegister.RAIN_SHOWER_HEAD_PURPLE.get());
      event.accept(BlocksRegister.RAIN_SHOWER_HEAD_BLUE.get());
      event.accept(BlocksRegister.RAIN_SHOWER_HEAD_BROWN.get());
      event.accept(BlocksRegister.RAIN_SHOWER_HEAD_GREEN.get());
      event.accept(BlocksRegister.RAIN_SHOWER_HEAD_RED.get());
      event.accept(BlocksRegister.RAIN_SHOWER_HEAD_OAK.get());
      event.accept(BlocksRegister.RAIN_SHOWER_HEAD_SPRUCE.get());
      event.accept(BlocksRegister.RAIN_SHOWER_HEAD_BIRCH.get());
      event.accept(BlocksRegister.RAIN_SHOWER_HEAD_JUNGLE.get());
      event.accept(BlocksRegister.RAIN_SHOWER_HEAD_ACACIA.get());
      event.accept(BlocksRegister.RAIN_SHOWER_HEAD_DARK_OAK.get());
      event.accept(BlocksRegister.RAIN_SHOWER_HEAD_MANGROVE.get());
      event.accept(BlocksRegister.RAIN_SHOWER_HEAD_CHERRY.get());
      event.accept(BlocksRegister.RAIN_SHOWER_HEAD_BAMBOO.get());
      event.accept(BlocksRegister.RAIN_SHOWER_HEAD_CRIMSON.get());
      event.accept(BlocksRegister.RAIN_SHOWER_HEAD_WARPED.get());
      event.accept(BlocksRegister.RAIN_SHOWER_HEAD_STONE.get());
      event.accept(BlocksRegister.RAIN_SHOWER_HEAD_COBBLESTONE.get());
      event.accept(BlocksRegister.RAIN_SHOWER_HEAD_IRON.get());
      event.accept(BlocksRegister.RAIN_SHOWER_HEAD_GOLD.get());
      event.accept(BlocksRegister.RAIN_SHOWER_HEAD_COPPER.get());
      event.accept(BlocksRegister.RAIN_SHOWER_HEAD_DIAMOND.get());
      event.accept(BlocksRegister.COMPACT_SHOWER_HEAD_STONE.get());
      event.accept(BlocksRegister.COMPACT_SHOWER_HEAD_OAK.get());
      event.accept(BlocksRegister.COMPACT_SHOWER_HEAD_IRON.get());
      event.accept(BlocksRegister.COMPACT_SHOWER_HEAD_GOLD.get());
      event.accept(BlocksRegister.COMPACT_SHOWER_HEAD_DIAMOND.get());
      event.accept(BlocksRegister.COMPACT_SHOWER_HEAD_BLACK.get());
      event.accept(BlocksRegister.COMPACT_SHOWER_HEAD_WHITE.get());
      event.accept(BlocksRegister.COMPACT_SHOWER_HEAD_ORANGE.get());
      event.accept(BlocksRegister.COMPACT_SHOWER_HEAD_MAGENTA.get());
      event.accept(BlocksRegister.COMPACT_SHOWER_HEAD_LIGHT_BLUE.get());
      event.accept(BlocksRegister.COMPACT_SHOWER_HEAD_YELLOW.get());
      event.accept(BlocksRegister.COMPACT_SHOWER_HEAD_LIME.get());
      event.accept(BlocksRegister.COMPACT_SHOWER_HEAD_PINK.get());
      event.accept(BlocksRegister.COMPACT_SHOWER_HEAD_GRAY.get());
      event.accept(BlocksRegister.COMPACT_SHOWER_HEAD_LIGHT_GRAY.get());
      event.accept(BlocksRegister.COMPACT_SHOWER_HEAD_CYAN.get());
      event.accept(BlocksRegister.COMPACT_SHOWER_HEAD_PURPLE.get());
      event.accept(BlocksRegister.COMPACT_SHOWER_HEAD_BLUE.get());
      event.accept(BlocksRegister.COMPACT_SHOWER_HEAD_BROWN.get());
      event.accept(BlocksRegister.COMPACT_SHOWER_HEAD_GREEN.get());
      event.accept(BlocksRegister.COMPACT_SHOWER_HEAD_RED.get());
      event.accept(BlocksRegister.COMPACT_SHOWER_HEAD_SPRUCE.get());
      event.accept(BlocksRegister.COMPACT_SHOWER_HEAD_BIRCH.get());
      event.accept(BlocksRegister.COMPACT_SHOWER_HEAD_JUNGLE.get());
      event.accept(BlocksRegister.COMPACT_SHOWER_HEAD_ACACIA.get());
      event.accept(BlocksRegister.COMPACT_SHOWER_HEAD_DARK_OAK.get());
      event.accept(BlocksRegister.COMPACT_SHOWER_HEAD_MANGROVE.get());
      event.accept(BlocksRegister.COMPACT_SHOWER_HEAD_CHERRY.get());
      event.accept(BlocksRegister.COMPACT_SHOWER_HEAD_BAMBOO.get());
      event.accept(BlocksRegister.COMPACT_SHOWER_HEAD_CRIMSON.get());
      event.accept(BlocksRegister.COMPACT_SHOWER_HEAD_WARPED.get());
      event.accept(BlocksRegister.COMPACT_SHOWER_HEAD_COBBLESTONE.get());
      event.accept(BlocksRegister.COMPACT_SHOWER_HEAD_COPPER.get());
      event.accept(BlocksRegister.BATHTUB_WHITE.get());
      event.accept(BlocksRegister.BATHTUB_ORANGE.get());
      event.accept(BlocksRegister.BATHTUB_MAGENTA.get());
      event.accept(BlocksRegister.BATHTUB_LIGHT_BLUE.get());
      event.accept(BlocksRegister.BATHTUB_YELLOW.get());
      event.accept(BlocksRegister.BATHTUB_LIME.get());
      event.accept(BlocksRegister.BATHTUB_PINK.get());
      event.accept(BlocksRegister.BATHTUB_GRAY.get());
      event.accept(BlocksRegister.BATHTUB_LIGHT_GRAY.get());
      event.accept(BlocksRegister.BATHTUB_CYAN.get());
      event.accept(BlocksRegister.BATHTUB_PURPLE.get());
      event.accept(BlocksRegister.BATHTUB_BLUE.get());
      event.accept(BlocksRegister.BATHTUB_BROWN.get());
      event.accept(BlocksRegister.BATHTUB_GREEN.get());
      event.accept(BlocksRegister.BATHTUB_RED.get());
      event.accept(BlocksRegister.BATHTUB_BLACK.get());
      event.accept(BlocksRegister.BATHTUB_OAK.get());
      event.accept(BlocksRegister.BATHTUB_SPRUCE.get());
      event.accept(BlocksRegister.BATHTUB_BIRCH.get());
      event.accept(BlocksRegister.BATHTUB_JUNGLE.get());
      event.accept(BlocksRegister.BATHTUB_ACACIA.get());
      event.accept(BlocksRegister.BATHTUB_DARK_OAK.get());
      event.accept(BlocksRegister.BATHTUB_MANGROVE.get());
      event.accept(BlocksRegister.BATHTUB_CHERRY.get());
      event.accept(BlocksRegister.BATHTUB_BAMBOO.get());
      event.accept(BlocksRegister.BATHTUB_CRIMSON.get());
      event.accept(BlocksRegister.BATHTUB_WARPED.get());
      event.accept(BlocksRegister.BATHTUB_STONE.get());
      event.accept(BlocksRegister.BATHTUB_COBBLESTONE.get());
      event.accept(BlocksRegister.BATHTUB_IRON.get());
      event.accept(BlocksRegister.BATHTUB_GOLD.get());
      event.accept(BlocksRegister.BATHTUB_COPPER.get());
      event.accept(BlocksRegister.BATHTUB_DIAMOND.get());
      event.accept(ItemRegister.RUBBER_DUCK.get());
    } catch (Throwable e) {
      LOGGER.error("Error adding ShowerCore items to creative tab: {}", e.getMessage());
    }
  }

  @SubscribeEvent
  public void onServerStarting(ServerStartingEvent event) {}

  @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
  public static class ClientModEvents {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
      MinecraftForge.EVENT_BUS.addListener(ClientEvent::registerParticleFactories);
    }
  }
}
