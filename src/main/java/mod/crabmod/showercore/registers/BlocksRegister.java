package mod.crabmod.showercore.registers;

import java.util.function.Supplier;
import mod.crabmod.showercore.ShowerCore;
import mod.crabmod.showercore.block.ShowerHeadBlock;
import mod.crabmod.showercore.block.BathtubBlock;
import mod.crabmod.showercore.block.bath_core.herbal_bath_core.HerbalBathCoreBlock;
import mod.crabmod.showercore.block.bath_core.honey_bath_core.HoneyBathCoreBlock;
import mod.crabmod.showercore.block.bath_core.hot_water_core.HotWaterCoreBlock;
import mod.crabmod.showercore.block.bath_core.milk_bath_core.MilkBathCoreBlock;
import mod.crabmod.showercore.block.bath_core.peony_bath_core.PeonyBathCoreBlock;
import mod.crabmod.showercore.block.bath_core.rose_bath_core.RoseBathCoreBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BlocksRegister {
  public static final DeferredRegister<Block> BLOCKS =
      DeferredRegister.create(ForgeRegistries.BLOCKS, ShowerCore.MODID);

  private static final BlockBehaviour.Properties COMMON_PROPERTIES_WOOL =
      BlockBehaviour.Properties.of().strength(0.2F).sound(SoundType.WOOL).noOcclusion();

  private static final BlockBehaviour.Properties COMMON_PROPERTIES_WOOD =
      BlockBehaviour.Properties.of().strength(1.0F).sound(SoundType.WOOD).noOcclusion();

  private static final BlockBehaviour.Properties COMMON_PROPERTIES_WOOD_NO_COLLISION =
      BlockBehaviour.Properties.of()
          .strength(1.0F)
          .sound(SoundType.WOOD)
          .noOcclusion()
          .noCollission();

  private static final BlockBehaviour.Properties COMMON_PROPERTIES_LIGHT =
      BlockBehaviour.Properties.of()
          .strength(1.0F)
          .sound(SoundType.WOOD)
          .noOcclusion()
          .lightLevel(state -> 14);

  private static final BlockBehaviour.Properties COMMON_PROPERTIES_LIGHT_NO_COLLISION =
      BlockBehaviour.Properties.of()
          .strength(1.0F)
          .sound(SoundType.WOOD)
          .noOcclusion()
          .noCollission()
          .lightLevel(state -> 14);

  private static final BlockBehaviour.Properties COMMON_PROPERTIES_STONE_NO_COLLISION =
      BlockBehaviour.Properties.of()
          .strength(1.5F)
          .sound(SoundType.STONE)
          .noOcclusion()
          .noCollission();

  private static final BlockBehaviour.Properties COMMON_PROPERTIES_METAL_NO_COLLISION =
      BlockBehaviour.Properties.of()
          .strength(2.0F)
          .sound(SoundType.METAL)
          .noOcclusion()
          .noCollission();

  private static final BlockBehaviour.Properties COMMON_PROPERTIES_BATHTUB =
      BlockBehaviour.Properties.of()
          .strength(2.0F)
          .sound(SoundType.STONE)
          .noOcclusion();

  private static final BlockBehaviour.Properties COMMON_PROPERTIES_CORE =
      BlockBehaviour.Properties.copy(Blocks.CONDUIT).noOcclusion();

  public static final RegistryObject<Block> SHOWER_HEAD_BLACK =
      registerBlock(
          "shower_head_black", () -> new ShowerHeadBlock(COMMON_PROPERTIES_WOOD_NO_COLLISION));

  public static final RegistryObject<Block> SHOWER_HEAD_WHITE =
      registerBlock(
          "shower_head_white", () -> new ShowerHeadBlock(COMMON_PROPERTIES_WOOD_NO_COLLISION));

  public static final RegistryObject<Block> SHOWER_HEAD_OAK =
      registerBlock(
          "shower_head_oak", () -> new ShowerHeadBlock(COMMON_PROPERTIES_WOOD_NO_COLLISION));

  public static final RegistryObject<Block> SHOWER_HEAD_SPRUCE =
      registerBlock(
          "shower_head_spruce", () -> new ShowerHeadBlock(COMMON_PROPERTIES_WOOD_NO_COLLISION));

  public static final RegistryObject<Block> SHOWER_HEAD_BIRCH =
      registerBlock(
          "shower_head_birch", () -> new ShowerHeadBlock(COMMON_PROPERTIES_WOOD_NO_COLLISION));

  public static final RegistryObject<Block> SHOWER_HEAD_JUNGLE =
      registerBlock(
          "shower_head_jungle", () -> new ShowerHeadBlock(COMMON_PROPERTIES_WOOD_NO_COLLISION));

  public static final RegistryObject<Block> SHOWER_HEAD_ACACIA =
      registerBlock(
          "shower_head_acacia", () -> new ShowerHeadBlock(COMMON_PROPERTIES_WOOD_NO_COLLISION));

  public static final RegistryObject<Block> SHOWER_HEAD_DARK_OAK =
      registerBlock(
          "shower_head_dark_oak", () -> new ShowerHeadBlock(COMMON_PROPERTIES_WOOD_NO_COLLISION));

  public static final RegistryObject<Block> SHOWER_HEAD_MANGROVE =
      registerBlock(
          "shower_head_mangrove", () -> new ShowerHeadBlock(COMMON_PROPERTIES_WOOD_NO_COLLISION));

  public static final RegistryObject<Block> SHOWER_HEAD_CHERRY =
      registerBlock(
          "shower_head_cherry", () -> new ShowerHeadBlock(COMMON_PROPERTIES_WOOD_NO_COLLISION));

  public static final RegistryObject<Block> SHOWER_HEAD_BAMBOO =
      registerBlock(
          "shower_head_bamboo", () -> new ShowerHeadBlock(COMMON_PROPERTIES_WOOD_NO_COLLISION));

  public static final RegistryObject<Block> SHOWER_HEAD_CRIMSON =
      registerBlock(
          "shower_head_crimson", () -> new ShowerHeadBlock(COMMON_PROPERTIES_WOOD_NO_COLLISION));

  public static final RegistryObject<Block> SHOWER_HEAD_WARPED =
      registerBlock(
          "shower_head_warped", () -> new ShowerHeadBlock(COMMON_PROPERTIES_WOOD_NO_COLLISION));

  public static final RegistryObject<Block> SHOWER_HEAD_STONE =
      registerBlock(
          "shower_head_stone", () -> new ShowerHeadBlock(COMMON_PROPERTIES_STONE_NO_COLLISION));

  public static final RegistryObject<Block> SHOWER_HEAD_COBBLESTONE =
      registerBlock(
          "shower_head_cobblestone",
          () -> new ShowerHeadBlock(COMMON_PROPERTIES_STONE_NO_COLLISION));

  public static final RegistryObject<Block> SHOWER_HEAD_IRON =
      registerBlock(
          "shower_head_iron", () -> new ShowerHeadBlock(COMMON_PROPERTIES_METAL_NO_COLLISION));

  public static final RegistryObject<Block> SHOWER_HEAD_GOLD =
      registerBlock(
          "shower_head_gold", () -> new ShowerHeadBlock(COMMON_PROPERTIES_METAL_NO_COLLISION));

  public static final RegistryObject<Block> SHOWER_HEAD_COPPER =
      registerBlock(
          "shower_head_copper", () -> new ShowerHeadBlock(COMMON_PROPERTIES_METAL_NO_COLLISION));

  public static final RegistryObject<Block> SHOWER_HEAD_DIAMOND =
      registerBlock(
          "shower_head_diamond", () -> new ShowerHeadBlock(COMMON_PROPERTIES_METAL_NO_COLLISION));

  public static final RegistryObject<Block> BATHTUB_WHITE =
      registerBlock(
          "bathtub_white", () -> new BathtubBlock(COMMON_PROPERTIES_BATHTUB));

  public static final RegistryObject<Block> HOT_WATER_CORE =
      registerBlock("hot_water_core", () -> new HotWaterCoreBlock(COMMON_PROPERTIES_CORE));

  public static final RegistryObject<Block> HERBAL_BATH_CORE =
      registerBlock("herbal_bath_core", () -> new HerbalBathCoreBlock(COMMON_PROPERTIES_CORE));

  public static final RegistryObject<Block> PEONY_BATH_CORE =
      registerBlock("peony_bath_core", () -> new PeonyBathCoreBlock(COMMON_PROPERTIES_CORE));

  public static final RegistryObject<Block> ROSE_BATH_CORE =
      registerBlock("rose_bath_core", () -> new RoseBathCoreBlock(COMMON_PROPERTIES_CORE));

  public static final RegistryObject<Block> MILK_BATH_CORE =
      registerBlock("milk_bath_core", () -> new MilkBathCoreBlock(COMMON_PROPERTIES_CORE));

  public static final RegistryObject<Block> HONEY_BATH_CORE =
      registerBlock("honey_bath_core", () -> new HoneyBathCoreBlock(COMMON_PROPERTIES_CORE));

  private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
    RegistryObject<T> toReturn = BLOCKS.register(name, block);
    registerBlockItem(name, toReturn);
    return toReturn;
  }

  private static <T extends Block> RegistryObject<Item> registerBlockItem(
      String name, RegistryObject<T> block) {
    return ItemRegister.ITEMS.register(
        name, () -> new BlockItem(block.get(), new Item.Properties()));
  }

  public static void register(IEventBus eventBus) {
    BLOCKS.register(eventBus);
  }
}
