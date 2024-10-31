package mod.crabmod.showercore.registers;

import static mod.crabmod.showercore.ShowerCore.MODID;

import mod.crabmod.showercore.block.bath_core.herbal_bath_core.HerbalBathCoreBlockEntity;
import mod.crabmod.showercore.block.bath_core.honey_bath_core.HoneyBathCoreBlockEntity;
import mod.crabmod.showercore.block.bath_core.hot_water_core.HotWaterCoreBlockEntity;
import mod.crabmod.showercore.block.bath_core.milk_bath_core.MilkBathCoreBlockEntity;
import mod.crabmod.showercore.block.bath_core.peony_bath_core.PeonyBathCoreBlockEntity;
import mod.crabmod.showercore.block.bath_core.rose_bath_core.RoseBathCoreBlockEntity;
import mod.crabmod.showercore.entity.ShowerHeadContainerEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BlockEntitiesRegister {
  public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
      DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MODID);

  public static final RegistryObject<BlockEntityType<HotWaterCoreBlockEntity>>
      HOT_WATER_CORE_BLOCK_ENTITY =
          BLOCK_ENTITIES.register(
              "hot_water_core_block_entity",
              () ->
                  BlockEntityType.Builder.of(
                          HotWaterCoreBlockEntity::new, BlocksRegister.HOT_WATER_CORE.get())
                      .build(null));

  public static final RegistryObject<BlockEntityType<HerbalBathCoreBlockEntity>>
      HERBAL_BATH_CORE_BLOCK_ENTITY =
          BLOCK_ENTITIES.register(
              "herbal_bath_core_block_entity",
              () ->
                  BlockEntityType.Builder.of(
                          HerbalBathCoreBlockEntity::new, BlocksRegister.HERBAL_BATH_CORE.get())
                      .build(null));

  public static final RegistryObject<BlockEntityType<HoneyBathCoreBlockEntity>>
      HONEY_BATH_CORE_BLOCK_ENTITY =
          BLOCK_ENTITIES.register(
              "honey_bath_core_block_entity",
              () ->
                  BlockEntityType.Builder.of(
                          HoneyBathCoreBlockEntity::new, BlocksRegister.HONEY_BATH_CORE.get())
                      .build(null));

  public static final RegistryObject<BlockEntityType<MilkBathCoreBlockEntity>>
      MILK_BATH_CORE_BLOCK_ENTITY =
          BLOCK_ENTITIES.register(
              "milk_bath_core_block_entity",
              () ->
                  BlockEntityType.Builder.of(
                          MilkBathCoreBlockEntity::new, BlocksRegister.MILK_BATH_CORE.get())
                      .build(null));

  public static final RegistryObject<BlockEntityType<PeonyBathCoreBlockEntity>>
      PEONY_BATH_CORE_BLOCK_ENTITY =
          BLOCK_ENTITIES.register(
              "peony_bath_core_block_entity",
              () ->
                  BlockEntityType.Builder.of(
                          PeonyBathCoreBlockEntity::new, BlocksRegister.PEONY_BATH_CORE.get())
                      .build(null));

  public static final RegistryObject<BlockEntityType<RoseBathCoreBlockEntity>>
      ROSE_BATH_CORE_BLOCK_ENTITY =
          BLOCK_ENTITIES.register(
              "rose_bath_core_block_entity",
              () ->
                  BlockEntityType.Builder.of(
                          RoseBathCoreBlockEntity::new, BlocksRegister.ROSE_BATH_CORE.get())
                      .build(null));

  public static final RegistryObject<BlockEntityType<ShowerHeadContainerEntity>>
      SHOWER_HEAD_CONTAINER =
          BLOCK_ENTITIES.register(
              "shower_head_container",
              () ->
                  BlockEntityType.Builder.of(
                          ShowerHeadContainerEntity::new,
                          BlocksRegister.SHOWER_HEAD_BLACK.get(),
                          BlocksRegister.SHOWER_HEAD_WHITE.get())
                      .build(null));

  public static void register(IEventBus eventBus) {
    BLOCK_ENTITIES.register(eventBus);
  }
}
