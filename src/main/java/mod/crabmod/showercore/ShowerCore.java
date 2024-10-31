package mod.crabmod.showercore;

import com.crabmod.hotbath.item.ItemGroup;
import com.mojang.logging.LogUtils;
import mod.crabmod.showercore.effect.ModEffects;
import mod.crabmod.showercore.registers.BlockEntitiesRegister;
import mod.crabmod.showercore.registers.BlocksRegister;
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
    ParticleRegister.register(modEventBus);

    modEventBus.addListener(this::commonSetup);

    MinecraftForge.EVENT_BUS.register(this);
    modEventBus.addListener(this::addCreative);

    ModLoadingContext.get()
        .registerConfig(ModConfig.Type.COMMON, mod.crabmod.showercore.Config.SPEC);
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
      event.accept(BlocksRegister.SHOWER_HEAD_BLACK.get());
      event.accept(BlocksRegister.SHOWER_HEAD_WHITE.get());
    }
  }

  @SubscribeEvent
  public void onServerStarting(ServerStartingEvent event) {}

  @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
  public static class ClientModEvents {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {}
  }
}
