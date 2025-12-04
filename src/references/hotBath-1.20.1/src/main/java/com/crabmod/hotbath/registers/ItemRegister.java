package com.crabmod.hotbath.registers;

import com.crabmod.hotbath.HotBath;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ItemRegister {
  public static final DeferredRegister<Item> ITEMS =
      DeferredRegister.create(ForgeRegistries.ITEMS, HotBath.MOD_ID);

  public static final RegistryObject<Item> HERBAL_BATH_BUCKET =
      ITEMS.register(
          "herbal_bath_bucket",
          () ->
              new BucketItem(
                  FluidsRegister.HERBAL_BATH_FLUID,
                  new Item.Properties().stacksTo(1)));

  public static final RegistryObject<Item> HONEY_BATH_BUCKET =
      ITEMS.register(
          "honey_bath_bucket",
          () ->
              new BucketItem(
                  FluidsRegister.HONEY_BATH_FLUID,
                  new Item.Properties().stacksTo(1)));

  public static final RegistryObject<Item> HOT_WATER_BUCKET = ITEMS.register("hot_water_bucket",
          () -> new BucketItem(FluidsRegister.HOT_WATER_FLUID,
                  new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));

  public static final RegistryObject<Item> MILK_BATH_BUCKET =
      ITEMS.register(
          "milk_bath_bucket",
          () ->
              new BucketItem(
                  FluidsRegister.MILK_BATH_FLUID,
                  new Item.Properties().stacksTo(1)));

  public static final RegistryObject<Item> PEONY_BATH_BUCKET =
      ITEMS.register(
          "peony_bath_bucket",
          () ->
              new BucketItem(
                  FluidsRegister.PEONY_BATH_FLUID,
                  new Item.Properties().stacksTo(1)));

  public static final RegistryObject<Item> ROSE_BATH_BUCKET =
      ITEMS.register(
          "rose_bath_bucket",
          () ->
              new BucketItem(
                  FluidsRegister.ROSE_BATH_FLUID,
                  new Item.Properties().stacksTo(1)));

  public static final RegistryObject<Item> BATH_HERB =
      ITEMS.register("bath_herb", () -> new Item(new Item.Properties()));

  public static void register(IEventBus eventBus) {
    ITEMS.register(eventBus);
  }
}
