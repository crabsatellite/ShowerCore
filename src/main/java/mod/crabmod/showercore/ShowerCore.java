package mod.crabmod.showercore;

import mod.crabmod.showercore.item.ItemGroup;
import com.mojang.logging.LogUtils;
import mod.crabmod.showercore.effect.ModEffects;
import mod.crabmod.showercore.event.ClientEvent;
import mod.crabmod.showercore.registers.BlockEntitiesRegister;
import mod.crabmod.showercore.registers.BlocksRegister;
import mod.crabmod.showercore.registers.EntityRegister;
import mod.crabmod.showercore.registers.ItemRegister;
import mod.crabmod.showercore.registers.ParticleRegister;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.ModContainer;
import org.slf4j.Logger;

import mod.crabmod.showercore.registers.SoundRegister;

import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

@Mod(ShowerCore.MODID)
public class ShowerCore {

  public static final String MODID = "showercore";
  private static final Logger LOGGER = LogUtils.getLogger();

  public ShowerCore(IEventBus modEventBus, ModContainer modContainer) {
    ItemRegister.register(modEventBus);
    BlocksRegister.register(modEventBus);
    ItemGroup.CREATIVE_MODE_TABS.register(modEventBus);

    ModEffects.register(modEventBus);

    BlockEntitiesRegister.register(modEventBus);
    EntityRegister.register(modEventBus);
    ParticleRegister.register(modEventBus);
    SoundRegister.register(modEventBus);

    modEventBus.addListener(this::commonSetup);
    modEventBus.addListener(this::registerCapabilities);

    NeoForge.EVENT_BUS.register(this);
    modEventBus.addListener(this::addCreative);

    modContainer.registerConfig(ModConfig.Type.COMMON, mod.crabmod.showercore.Config.SPEC);
    modContainer.registerConfig(ModConfig.Type.CLIENT, mod.crabmod.showercore.ClientConfig.SPEC);
  }

  private void registerCapabilities(RegisterCapabilitiesEvent event) {
    event.registerBlockEntity(
        net.neoforged.neoforge.capabilities.Capabilities.FluidHandler.BLOCK,
        BlockEntitiesRegister.BATHTUB_BLOCK_ENTITY.get(),
        (be, side) -> be.getFluidTank());
  }

  private void commonSetup(final FMLCommonSetupEvent event) {}

  private void addCreative(BuildCreativeModeTabContentsEvent event) {
    if (event.getTabKey() == ItemGroup.HOT_BATH_TAB.getKey()) {
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
    }
  }

  @SubscribeEvent
  public void onServerStarting(ServerStartingEvent event) {}

  @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
  public static class ClientModEvents {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
    }
  }
}
