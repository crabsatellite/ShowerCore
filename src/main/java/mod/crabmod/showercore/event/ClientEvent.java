package mod.crabmod.showercore.event;

import mod.crabmod.showercore.ShowerCore;
import mod.crabmod.showercore.block.bath_core.herbal_bath_core.HerbalBathCoreBlockEntityRenderer;
import mod.crabmod.showercore.block.bath_core.herbal_bath_core.HerbalBathCoreModelLayers;
import mod.crabmod.showercore.block.bath_core.honey_bath_core.HoneyBathCoreBlockEntityRenderer;
import mod.crabmod.showercore.block.bath_core.honey_bath_core.HoneyBathCoreModelLayers;
import mod.crabmod.showercore.block.bath_core.hot_water_core.HotWaterCoreBlockEntityRenderer;
import mod.crabmod.showercore.block.bath_core.hot_water_core.HotWaterCoreModelLayers;
import mod.crabmod.showercore.block.bath_core.milk_bath_core.MilkBathCoreBlockEntityRenderer;
import mod.crabmod.showercore.block.bath_core.milk_bath_core.MilkBathCoreModelLayers;
import mod.crabmod.showercore.block.bath_core.peony_bath_core.PeonyBathCoreBlockEntityRenderer;
import mod.crabmod.showercore.block.bath_core.peony_bath_core.PeonyBathCoreModelLayers;
import mod.crabmod.showercore.block.bath_core.rose_bath_core.RoseBathCoreBlockEntityRenderer;
import mod.crabmod.showercore.block.bath_core.rose_bath_core.RoseBathCoreModelLayers;
import mod.crabmod.showercore.particle.ShowerParticle;
import mod.crabmod.showercore.registers.BlockEntitiesRegister;
import mod.crabmod.showercore.registers.ParticleRegister;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EnchantmentTableParticle;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(
    modid = ShowerCore.MODID,
    bus = Mod.EventBusSubscriber.Bus.MOD,
    value = Dist.CLIENT)
public class ClientEvent {

  @SubscribeEvent
  public static void registerLayer(EntityRenderersEvent.RegisterLayerDefinitions event) {
    // HotWater Core Layers
    event.registerLayerDefinition(
        HotWaterCoreModelLayers.HOT_WATER_CORE_CAGE,
        HotWaterCoreBlockEntityRenderer::createCageLayer);
    event.registerLayerDefinition(
        HotWaterCoreModelLayers.HOT_WATER_CORE_WIND,
        HotWaterCoreBlockEntityRenderer::createWindLayer);
    event.registerLayerDefinition(
        HotWaterCoreModelLayers.HOT_WATER_CORE_EYE,
        HotWaterCoreBlockEntityRenderer::createEyeLayer);
    event.registerLayerDefinition(
        HotWaterCoreModelLayers.HOT_WATER_CORE_SHELL,
        HotWaterCoreBlockEntityRenderer::createShellLayer);

    // Herbal Bath Core Layers
    event.registerLayerDefinition(
        HerbalBathCoreModelLayers.HERBAL_BATH_CORE_CAGE,
        HerbalBathCoreBlockEntityRenderer::createCageLayer);
    event.registerLayerDefinition(
        HerbalBathCoreModelLayers.HERBAL_BATH_CORE_WIND,
        HerbalBathCoreBlockEntityRenderer::createWindLayer);
    event.registerLayerDefinition(
        HerbalBathCoreModelLayers.HERBAL_BATH_CORE_EYE,
        HerbalBathCoreBlockEntityRenderer::createEyeLayer);
    event.registerLayerDefinition(
        HerbalBathCoreModelLayers.HERBAL_BATH_CORE_SHELL,
        HerbalBathCoreBlockEntityRenderer::createShellLayer);

    // Milk Bath Core Layers
    event.registerLayerDefinition(
        MilkBathCoreModelLayers.MILK_BATH_CORE_CAGE,
        MilkBathCoreBlockEntityRenderer::createCageLayer);
    event.registerLayerDefinition(
        MilkBathCoreModelLayers.MILK_BATH_CORE_WIND,
        MilkBathCoreBlockEntityRenderer::createWindLayer);
    event.registerLayerDefinition(
        MilkBathCoreModelLayers.MILK_BATH_CORE_EYE,
        MilkBathCoreBlockEntityRenderer::createEyeLayer);
    event.registerLayerDefinition(
        MilkBathCoreModelLayers.MILK_BATH_CORE_SHELL,
        MilkBathCoreBlockEntityRenderer::createShellLayer);

    // Honey Bath Core Layers
    event.registerLayerDefinition(
        HoneyBathCoreModelLayers.HONEY_BATH_CORE_CAGE,
        HoneyBathCoreBlockEntityRenderer::createCageLayer);
    event.registerLayerDefinition(
        HoneyBathCoreModelLayers.HONEY_BATH_CORE_WIND,
        HoneyBathCoreBlockEntityRenderer::createWindLayer);
    event.registerLayerDefinition(
        HoneyBathCoreModelLayers.HONEY_BATH_CORE_EYE,
        HoneyBathCoreBlockEntityRenderer::createEyeLayer);
    event.registerLayerDefinition(
        HoneyBathCoreModelLayers.HONEY_BATH_CORE_SHELL,
        HoneyBathCoreBlockEntityRenderer::createShellLayer);

    // Rose Bath Core Layers
    event.registerLayerDefinition(
        RoseBathCoreModelLayers.ROSE_BATH_CORE_CAGE,
        RoseBathCoreBlockEntityRenderer::createCageLayer);
    event.registerLayerDefinition(
        RoseBathCoreModelLayers.ROSE_BATH_CORE_WIND,
        RoseBathCoreBlockEntityRenderer::createWindLayer);
    event.registerLayerDefinition(
        RoseBathCoreModelLayers.ROSE_BATH_CORE_EYE,
        RoseBathCoreBlockEntityRenderer::createEyeLayer);
    event.registerLayerDefinition(
        RoseBathCoreModelLayers.ROSE_BATH_CORE_SHELL,
        RoseBathCoreBlockEntityRenderer::createShellLayer);

    // Peony Bath Core Layers
    event.registerLayerDefinition(
        PeonyBathCoreModelLayers.PEONY_BATH_CORE_CAGE,
        PeonyBathCoreBlockEntityRenderer::createCageLayer);
    event.registerLayerDefinition(
        PeonyBathCoreModelLayers.PEONY_BATH_CORE_WIND,
        PeonyBathCoreBlockEntityRenderer::createWindLayer);
    event.registerLayerDefinition(
        PeonyBathCoreModelLayers.PEONY_BATH_CORE_EYE,
        PeonyBathCoreBlockEntityRenderer::createEyeLayer);
    event.registerLayerDefinition(
        PeonyBathCoreModelLayers.PEONY_BATH_CORE_SHELL,
        PeonyBathCoreBlockEntityRenderer::createShellLayer);
  }

  @SubscribeEvent
  public static void registerBER(EntityRenderersEvent.RegisterRenderers event) {
    // milk, honey, peony, rose, herbal
    event.registerBlockEntityRenderer(
        BlockEntitiesRegister.HOT_WATER_CORE_BLOCK_ENTITY.get(),
        HotWaterCoreBlockEntityRenderer::new);
    event.registerBlockEntityRenderer(
        BlockEntitiesRegister.HERBAL_BATH_CORE_BLOCK_ENTITY.get(),
        HerbalBathCoreBlockEntityRenderer::new);
    event.registerBlockEntityRenderer(
        BlockEntitiesRegister.MILK_BATH_CORE_BLOCK_ENTITY.get(),
        MilkBathCoreBlockEntityRenderer::new);
    event.registerBlockEntityRenderer(
        BlockEntitiesRegister.HONEY_BATH_CORE_BLOCK_ENTITY.get(),
        HoneyBathCoreBlockEntityRenderer::new);
    event.registerBlockEntityRenderer(
        BlockEntitiesRegister.ROSE_BATH_CORE_BLOCK_ENTITY.get(),
        RoseBathCoreBlockEntityRenderer::new);
    event.registerBlockEntityRenderer(
        BlockEntitiesRegister.PEONY_BATH_CORE_BLOCK_ENTITY.get(),
        PeonyBathCoreBlockEntityRenderer::new);
  }

  @SubscribeEvent
  public static void registerParticles(final RegisterParticleProvidersEvent event) {
    Minecraft.getInstance()
        .particleEngine
        .register(
            ParticleRegister.BATH_CORE_PARTICLE.get(),
            EnchantmentTableParticle.NautilusProvider::new);

    event.registerSpriteSet(
        ParticleRegister.HOT_WATER_SHOWER_PARTICLE.get(), ShowerParticle.Provider::new);
  }

  public static void registerParticleFactories(RegisterParticleProvidersEvent event) {
    event.registerSpriteSet(
        ParticleRegister.HOT_WATER_SHOWER_PARTICLE.get(), ShowerParticle.Provider::new);
  }
}
