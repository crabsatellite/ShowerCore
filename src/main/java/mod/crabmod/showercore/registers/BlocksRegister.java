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
import mod.crabmod.showercore.item.BathtubBlockItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.minecraft.core.registries.Registries;

public class BlocksRegister {
  public static final DeferredRegister<Block> BLOCKS =
      DeferredRegister.create(Registries.BLOCK, ShowerCore.MODID);

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
          .noOcclusion()
          .requiresCorrectToolForDrops();

  private static final BlockBehaviour.Properties COMMON_PROPERTIES_BATHTUB_WOOD =
      BlockBehaviour.Properties.of()
          .strength(1.0F)
          .sound(SoundType.WOOD)
          .noOcclusion()
          .requiresCorrectToolForDrops();

  private static final BlockBehaviour.Properties COMMON_PROPERTIES_BATHTUB_METAL =
      BlockBehaviour.Properties.of()
          .strength(2.0F)
          .sound(SoundType.METAL)
          .noOcclusion()
          .requiresCorrectToolForDrops();

  private static final BlockBehaviour.Properties COMMON_PROPERTIES_CORE =
      BlockBehaviour.Properties.ofFullCopy(Blocks.CONDUIT).noOcclusion();

  public static final DeferredHolder<Block, Block> RAIN_SHOWER_HEAD_BLACK =
      registerBlock(
          "rain_shower_head_black", () -> new ShowerHeadBlock(COMMON_PROPERTIES_WOOD_NO_COLLISION));

  public static final DeferredHolder<Block, Block> RAIN_SHOWER_HEAD_WHITE =
      registerBlock(
          "rain_shower_head_white", () -> new ShowerHeadBlock(COMMON_PROPERTIES_WOOD_NO_COLLISION));

  public static final DeferredHolder<Block, Block> RAIN_SHOWER_HEAD_ORANGE =
      registerBlock(
          "rain_shower_head_orange", () -> new ShowerHeadBlock(COMMON_PROPERTIES_WOOD_NO_COLLISION));

  public static final DeferredHolder<Block, Block> RAIN_SHOWER_HEAD_MAGENTA =
      registerBlock(
          "rain_shower_head_magenta", () -> new ShowerHeadBlock(COMMON_PROPERTIES_WOOD_NO_COLLISION));

  public static final DeferredHolder<Block, Block> RAIN_SHOWER_HEAD_LIGHT_BLUE =
      registerBlock(
          "rain_shower_head_light_blue", () -> new ShowerHeadBlock(COMMON_PROPERTIES_WOOD_NO_COLLISION));

  public static final DeferredHolder<Block, Block> RAIN_SHOWER_HEAD_YELLOW =
      registerBlock(
          "rain_shower_head_yellow", () -> new ShowerHeadBlock(COMMON_PROPERTIES_WOOD_NO_COLLISION));

  public static final DeferredHolder<Block, Block> RAIN_SHOWER_HEAD_LIME =
      registerBlock(
          "rain_shower_head_lime", () -> new ShowerHeadBlock(COMMON_PROPERTIES_WOOD_NO_COLLISION));

  public static final DeferredHolder<Block, Block> RAIN_SHOWER_HEAD_PINK =
      registerBlock(
          "rain_shower_head_pink", () -> new ShowerHeadBlock(COMMON_PROPERTIES_WOOD_NO_COLLISION));

  public static final DeferredHolder<Block, Block> RAIN_SHOWER_HEAD_GRAY =
      registerBlock(
          "rain_shower_head_gray", () -> new ShowerHeadBlock(COMMON_PROPERTIES_WOOD_NO_COLLISION));

  public static final DeferredHolder<Block, Block> RAIN_SHOWER_HEAD_LIGHT_GRAY =
      registerBlock(
          "rain_shower_head_light_gray", () -> new ShowerHeadBlock(COMMON_PROPERTIES_WOOD_NO_COLLISION));

  public static final DeferredHolder<Block, Block> RAIN_SHOWER_HEAD_CYAN =
      registerBlock(
          "rain_shower_head_cyan", () -> new ShowerHeadBlock(COMMON_PROPERTIES_WOOD_NO_COLLISION));

  public static final DeferredHolder<Block, Block> RAIN_SHOWER_HEAD_PURPLE =
      registerBlock(
          "rain_shower_head_purple", () -> new ShowerHeadBlock(COMMON_PROPERTIES_WOOD_NO_COLLISION));

  public static final DeferredHolder<Block, Block> RAIN_SHOWER_HEAD_BLUE =
      registerBlock(
          "rain_shower_head_blue", () -> new ShowerHeadBlock(COMMON_PROPERTIES_WOOD_NO_COLLISION));

  public static final DeferredHolder<Block, Block> RAIN_SHOWER_HEAD_BROWN =
      registerBlock(
          "rain_shower_head_brown", () -> new ShowerHeadBlock(COMMON_PROPERTIES_WOOD_NO_COLLISION));

  public static final DeferredHolder<Block, Block> RAIN_SHOWER_HEAD_GREEN =
      registerBlock(
          "rain_shower_head_green", () -> new ShowerHeadBlock(COMMON_PROPERTIES_WOOD_NO_COLLISION));

  public static final DeferredHolder<Block, Block> RAIN_SHOWER_HEAD_RED =
      registerBlock(
          "rain_shower_head_red", () -> new ShowerHeadBlock(COMMON_PROPERTIES_WOOD_NO_COLLISION));

  public static final DeferredHolder<Block, Block> RAIN_SHOWER_HEAD_OAK =
      registerBlock(
          "rain_shower_head_oak", () -> new ShowerHeadBlock(COMMON_PROPERTIES_WOOD_NO_COLLISION));

  public static final DeferredHolder<Block, Block> RAIN_SHOWER_HEAD_SPRUCE =
      registerBlock(
          "rain_shower_head_spruce", () -> new ShowerHeadBlock(COMMON_PROPERTIES_WOOD_NO_COLLISION));

  public static final DeferredHolder<Block, Block> RAIN_SHOWER_HEAD_BIRCH =
      registerBlock(
          "rain_shower_head_birch", () -> new ShowerHeadBlock(COMMON_PROPERTIES_WOOD_NO_COLLISION));

  public static final DeferredHolder<Block, Block> RAIN_SHOWER_HEAD_JUNGLE =
      registerBlock(
          "rain_shower_head_jungle", () -> new ShowerHeadBlock(COMMON_PROPERTIES_WOOD_NO_COLLISION));

  public static final DeferredHolder<Block, Block> RAIN_SHOWER_HEAD_ACACIA =
      registerBlock(
          "rain_shower_head_acacia", () -> new ShowerHeadBlock(COMMON_PROPERTIES_WOOD_NO_COLLISION));

  public static final DeferredHolder<Block, Block> RAIN_SHOWER_HEAD_DARK_OAK =
      registerBlock(
          "rain_shower_head_dark_oak", () -> new ShowerHeadBlock(COMMON_PROPERTIES_WOOD_NO_COLLISION));

  public static final DeferredHolder<Block, Block> RAIN_SHOWER_HEAD_MANGROVE =
      registerBlock(
          "rain_shower_head_mangrove", () -> new ShowerHeadBlock(COMMON_PROPERTIES_WOOD_NO_COLLISION));

  public static final DeferredHolder<Block, Block> RAIN_SHOWER_HEAD_CHERRY =
      registerBlock(
          "rain_shower_head_cherry", () -> new ShowerHeadBlock(COMMON_PROPERTIES_WOOD_NO_COLLISION));

  public static final DeferredHolder<Block, Block> RAIN_SHOWER_HEAD_BAMBOO =
      registerBlock(
          "rain_shower_head_bamboo", () -> new ShowerHeadBlock(COMMON_PROPERTIES_WOOD_NO_COLLISION));

  public static final DeferredHolder<Block, Block> RAIN_SHOWER_HEAD_CRIMSON =
      registerBlock(
          "rain_shower_head_crimson", () -> new ShowerHeadBlock(COMMON_PROPERTIES_WOOD_NO_COLLISION));

  public static final DeferredHolder<Block, Block> RAIN_SHOWER_HEAD_WARPED =
      registerBlock(
          "rain_shower_head_warped", () -> new ShowerHeadBlock(COMMON_PROPERTIES_WOOD_NO_COLLISION));

  public static final DeferredHolder<Block, Block> RAIN_SHOWER_HEAD_STONE =
      registerBlock(
          "rain_shower_head_stone", () -> new ShowerHeadBlock(COMMON_PROPERTIES_STONE_NO_COLLISION));

  public static final DeferredHolder<Block, Block> RAIN_SHOWER_HEAD_COBBLESTONE =
      registerBlock(
          "rain_shower_head_cobblestone",
          () -> new ShowerHeadBlock(COMMON_PROPERTIES_STONE_NO_COLLISION));

  public static final DeferredHolder<Block, Block> RAIN_SHOWER_HEAD_IRON =
      registerBlock(
          "rain_shower_head_iron", () -> new ShowerHeadBlock(COMMON_PROPERTIES_METAL_NO_COLLISION));

  public static final DeferredHolder<Block, Block> RAIN_SHOWER_HEAD_GOLD =
      registerBlock(
          "rain_shower_head_gold", () -> new ShowerHeadBlock(COMMON_PROPERTIES_METAL_NO_COLLISION));

  public static final DeferredHolder<Block, Block> RAIN_SHOWER_HEAD_COPPER =
      registerBlock(
          "rain_shower_head_copper", () -> new ShowerHeadBlock(COMMON_PROPERTIES_METAL_NO_COLLISION));

  public static final DeferredHolder<Block, Block> RAIN_SHOWER_HEAD_DIAMOND =
      registerBlock(
          "rain_shower_head_diamond", () -> new ShowerHeadBlock(COMMON_PROPERTIES_METAL_NO_COLLISION));

  public static final DeferredHolder<Block, Block> COMPACT_SHOWER_HEAD_STONE =
      registerBlock(
          "compact_shower_head_stone", () -> new ShowerHeadBlock(COMMON_PROPERTIES_STONE_NO_COLLISION));

  public static final DeferredHolder<Block, Block> COMPACT_SHOWER_HEAD_OAK =
      registerBlock(
          "compact_shower_head_oak", () -> new ShowerHeadBlock(COMMON_PROPERTIES_WOOD_NO_COLLISION));

  public static final DeferredHolder<Block, Block> COMPACT_SHOWER_HEAD_IRON =
      registerBlock(
          "compact_shower_head_iron", () -> new ShowerHeadBlock(COMMON_PROPERTIES_METAL_NO_COLLISION));

  public static final DeferredHolder<Block, Block> COMPACT_SHOWER_HEAD_GOLD =
      registerBlock(
          "compact_shower_head_gold", () -> new ShowerHeadBlock(COMMON_PROPERTIES_METAL_NO_COLLISION));

  public static final DeferredHolder<Block, Block> COMPACT_SHOWER_HEAD_DIAMOND =
      registerBlock(
          "compact_shower_head_diamond", () -> new ShowerHeadBlock(COMMON_PROPERTIES_METAL_NO_COLLISION));

  public static final DeferredHolder<Block, Block> COMPACT_SHOWER_HEAD_BLACK =
      registerBlock(
          "compact_shower_head_black", () -> new ShowerHeadBlock(COMMON_PROPERTIES_WOOD_NO_COLLISION));

  public static final DeferredHolder<Block, Block> COMPACT_SHOWER_HEAD_WHITE =
      registerBlock(
          "compact_shower_head_white", () -> new ShowerHeadBlock(COMMON_PROPERTIES_WOOD_NO_COLLISION));

  public static final DeferredHolder<Block, Block> COMPACT_SHOWER_HEAD_ORANGE =
      registerBlock(
          "compact_shower_head_orange", () -> new ShowerHeadBlock(COMMON_PROPERTIES_WOOD_NO_COLLISION));

  public static final DeferredHolder<Block, Block> COMPACT_SHOWER_HEAD_MAGENTA =
      registerBlock(
          "compact_shower_head_magenta", () -> new ShowerHeadBlock(COMMON_PROPERTIES_WOOD_NO_COLLISION));

  public static final DeferredHolder<Block, Block> COMPACT_SHOWER_HEAD_LIGHT_BLUE =
      registerBlock(
          "compact_shower_head_light_blue", () -> new ShowerHeadBlock(COMMON_PROPERTIES_WOOD_NO_COLLISION));

  public static final DeferredHolder<Block, Block> COMPACT_SHOWER_HEAD_YELLOW =
      registerBlock(
          "compact_shower_head_yellow", () -> new ShowerHeadBlock(COMMON_PROPERTIES_WOOD_NO_COLLISION));

  public static final DeferredHolder<Block, Block> COMPACT_SHOWER_HEAD_LIME =
      registerBlock(
          "compact_shower_head_lime", () -> new ShowerHeadBlock(COMMON_PROPERTIES_WOOD_NO_COLLISION));

  public static final DeferredHolder<Block, Block> COMPACT_SHOWER_HEAD_PINK =
      registerBlock(
          "compact_shower_head_pink", () -> new ShowerHeadBlock(COMMON_PROPERTIES_WOOD_NO_COLLISION));

  public static final DeferredHolder<Block, Block> COMPACT_SHOWER_HEAD_GRAY =
      registerBlock(
          "compact_shower_head_gray", () -> new ShowerHeadBlock(COMMON_PROPERTIES_WOOD_NO_COLLISION));

  public static final DeferredHolder<Block, Block> COMPACT_SHOWER_HEAD_LIGHT_GRAY =
      registerBlock(
          "compact_shower_head_light_gray", () -> new ShowerHeadBlock(COMMON_PROPERTIES_WOOD_NO_COLLISION));

  public static final DeferredHolder<Block, Block> COMPACT_SHOWER_HEAD_CYAN =
      registerBlock(
          "compact_shower_head_cyan", () -> new ShowerHeadBlock(COMMON_PROPERTIES_WOOD_NO_COLLISION));

  public static final DeferredHolder<Block, Block> COMPACT_SHOWER_HEAD_PURPLE =
      registerBlock(
          "compact_shower_head_purple", () -> new ShowerHeadBlock(COMMON_PROPERTIES_WOOD_NO_COLLISION));

  public static final DeferredHolder<Block, Block> COMPACT_SHOWER_HEAD_BLUE =
      registerBlock(
          "compact_shower_head_blue", () -> new ShowerHeadBlock(COMMON_PROPERTIES_WOOD_NO_COLLISION));

  public static final DeferredHolder<Block, Block> COMPACT_SHOWER_HEAD_BROWN =
      registerBlock(
          "compact_shower_head_brown", () -> new ShowerHeadBlock(COMMON_PROPERTIES_WOOD_NO_COLLISION));

  public static final DeferredHolder<Block, Block> COMPACT_SHOWER_HEAD_GREEN =
      registerBlock(
          "compact_shower_head_green", () -> new ShowerHeadBlock(COMMON_PROPERTIES_WOOD_NO_COLLISION));

  public static final DeferredHolder<Block, Block> COMPACT_SHOWER_HEAD_RED =
      registerBlock(
          "compact_shower_head_red", () -> new ShowerHeadBlock(COMMON_PROPERTIES_WOOD_NO_COLLISION));

  public static final DeferredHolder<Block, Block> COMPACT_SHOWER_HEAD_SPRUCE =
      registerBlock(
          "compact_shower_head_spruce", () -> new ShowerHeadBlock(COMMON_PROPERTIES_WOOD_NO_COLLISION));

  public static final DeferredHolder<Block, Block> COMPACT_SHOWER_HEAD_BIRCH =
      registerBlock(
          "compact_shower_head_birch", () -> new ShowerHeadBlock(COMMON_PROPERTIES_WOOD_NO_COLLISION));

  public static final DeferredHolder<Block, Block> COMPACT_SHOWER_HEAD_JUNGLE =
      registerBlock(
          "compact_shower_head_jungle", () -> new ShowerHeadBlock(COMMON_PROPERTIES_WOOD_NO_COLLISION));

  public static final DeferredHolder<Block, Block> COMPACT_SHOWER_HEAD_ACACIA =
      registerBlock(
          "compact_shower_head_acacia", () -> new ShowerHeadBlock(COMMON_PROPERTIES_WOOD_NO_COLLISION));

  public static final DeferredHolder<Block, Block> COMPACT_SHOWER_HEAD_DARK_OAK =
      registerBlock(
          "compact_shower_head_dark_oak", () -> new ShowerHeadBlock(COMMON_PROPERTIES_WOOD_NO_COLLISION));

  public static final DeferredHolder<Block, Block> COMPACT_SHOWER_HEAD_MANGROVE =
      registerBlock(
          "compact_shower_head_mangrove", () -> new ShowerHeadBlock(COMMON_PROPERTIES_WOOD_NO_COLLISION));

  public static final DeferredHolder<Block, Block> COMPACT_SHOWER_HEAD_CHERRY =
      registerBlock(
          "compact_shower_head_cherry", () -> new ShowerHeadBlock(COMMON_PROPERTIES_WOOD_NO_COLLISION));

  public static final DeferredHolder<Block, Block> COMPACT_SHOWER_HEAD_BAMBOO =
      registerBlock(
          "compact_shower_head_bamboo", () -> new ShowerHeadBlock(COMMON_PROPERTIES_WOOD_NO_COLLISION));

  public static final DeferredHolder<Block, Block> COMPACT_SHOWER_HEAD_CRIMSON =
      registerBlock(
          "compact_shower_head_crimson", () -> new ShowerHeadBlock(COMMON_PROPERTIES_WOOD_NO_COLLISION));

  public static final DeferredHolder<Block, Block> COMPACT_SHOWER_HEAD_WARPED =
      registerBlock(
          "compact_shower_head_warped", () -> new ShowerHeadBlock(COMMON_PROPERTIES_WOOD_NO_COLLISION));

  public static final DeferredHolder<Block, Block> COMPACT_SHOWER_HEAD_COBBLESTONE =
      registerBlock(
          "compact_shower_head_cobblestone", () -> new ShowerHeadBlock(COMMON_PROPERTIES_STONE_NO_COLLISION));

  public static final DeferredHolder<Block, Block> COMPACT_SHOWER_HEAD_COPPER =
      registerBlock(
          "compact_shower_head_copper", () -> new ShowerHeadBlock(COMMON_PROPERTIES_METAL_NO_COLLISION));

  public static final DeferredHolder<Block, Block> BATHTUB_WHITE =
      registerBlock(
          "bathtub_white", () -> new BathtubBlock(COMMON_PROPERTIES_BATHTUB));

  public static final DeferredHolder<Block, Block> BATHTUB_ORANGE =
      registerBlock(
          "bathtub_orange", () -> new BathtubBlock(COMMON_PROPERTIES_BATHTUB));

  public static final DeferredHolder<Block, Block> BATHTUB_MAGENTA =
      registerBlock(
          "bathtub_magenta", () -> new BathtubBlock(COMMON_PROPERTIES_BATHTUB));

  public static final DeferredHolder<Block, Block> BATHTUB_LIGHT_BLUE =
      registerBlock(
          "bathtub_light_blue", () -> new BathtubBlock(COMMON_PROPERTIES_BATHTUB));

  public static final DeferredHolder<Block, Block> BATHTUB_YELLOW =
      registerBlock(
          "bathtub_yellow", () -> new BathtubBlock(COMMON_PROPERTIES_BATHTUB));

  public static final DeferredHolder<Block, Block> BATHTUB_LIME =
      registerBlock(
          "bathtub_lime", () -> new BathtubBlock(COMMON_PROPERTIES_BATHTUB));

  public static final DeferredHolder<Block, Block> BATHTUB_PINK =
      registerBlock(
          "bathtub_pink", () -> new BathtubBlock(COMMON_PROPERTIES_BATHTUB));

  public static final DeferredHolder<Block, Block> BATHTUB_GRAY =
      registerBlock(
          "bathtub_gray", () -> new BathtubBlock(COMMON_PROPERTIES_BATHTUB));

  public static final DeferredHolder<Block, Block> BATHTUB_LIGHT_GRAY =
      registerBlock(
          "bathtub_light_gray", () -> new BathtubBlock(COMMON_PROPERTIES_BATHTUB));

  public static final DeferredHolder<Block, Block> BATHTUB_CYAN =
      registerBlock(
          "bathtub_cyan", () -> new BathtubBlock(COMMON_PROPERTIES_BATHTUB));

  public static final DeferredHolder<Block, Block> BATHTUB_PURPLE =
      registerBlock(
          "bathtub_purple", () -> new BathtubBlock(COMMON_PROPERTIES_BATHTUB));

  public static final DeferredHolder<Block, Block> BATHTUB_BLUE =
      registerBlock(
          "bathtub_blue", () -> new BathtubBlock(COMMON_PROPERTIES_BATHTUB));

  public static final DeferredHolder<Block, Block> BATHTUB_BROWN =
      registerBlock(
          "bathtub_brown", () -> new BathtubBlock(COMMON_PROPERTIES_BATHTUB));

  public static final DeferredHolder<Block, Block> BATHTUB_GREEN =
      registerBlock(
          "bathtub_green", () -> new BathtubBlock(COMMON_PROPERTIES_BATHTUB));

  public static final DeferredHolder<Block, Block> BATHTUB_RED =
      registerBlock(
          "bathtub_red", () -> new BathtubBlock(COMMON_PROPERTIES_BATHTUB));

  public static final DeferredHolder<Block, Block> BATHTUB_BLACK =
      registerBlock(
          "bathtub_black", () -> new BathtubBlock(COMMON_PROPERTIES_BATHTUB));

  public static final DeferredHolder<Block, Block> BATHTUB_OAK =
      registerBlock(
          "bathtub_oak", () -> new BathtubBlock(COMMON_PROPERTIES_BATHTUB_WOOD));

  public static final DeferredHolder<Block, Block> BATHTUB_SPRUCE =
      registerBlock(
          "bathtub_spruce", () -> new BathtubBlock(COMMON_PROPERTIES_BATHTUB_WOOD));

  public static final DeferredHolder<Block, Block> BATHTUB_BIRCH =
      registerBlock(
          "bathtub_birch", () -> new BathtubBlock(COMMON_PROPERTIES_BATHTUB_WOOD));

  public static final DeferredHolder<Block, Block> BATHTUB_JUNGLE =
      registerBlock(
          "bathtub_jungle", () -> new BathtubBlock(COMMON_PROPERTIES_BATHTUB_WOOD));

  public static final DeferredHolder<Block, Block> BATHTUB_ACACIA =
      registerBlock(
          "bathtub_acacia", () -> new BathtubBlock(COMMON_PROPERTIES_BATHTUB_WOOD));

  public static final DeferredHolder<Block, Block> BATHTUB_DARK_OAK =
      registerBlock(
          "bathtub_dark_oak", () -> new BathtubBlock(COMMON_PROPERTIES_BATHTUB_WOOD));

  public static final DeferredHolder<Block, Block> BATHTUB_MANGROVE =
      registerBlock(
          "bathtub_mangrove", () -> new BathtubBlock(COMMON_PROPERTIES_BATHTUB_WOOD));

  public static final DeferredHolder<Block, Block> BATHTUB_CHERRY =
      registerBlock(
          "bathtub_cherry", () -> new BathtubBlock(COMMON_PROPERTIES_BATHTUB_WOOD));

  public static final DeferredHolder<Block, Block> BATHTUB_BAMBOO =
      registerBlock(
          "bathtub_bamboo", () -> new BathtubBlock(COMMON_PROPERTIES_BATHTUB_WOOD));

  public static final DeferredHolder<Block, Block> BATHTUB_CRIMSON =
      registerBlock(
          "bathtub_crimson", () -> new BathtubBlock(COMMON_PROPERTIES_BATHTUB_WOOD));

  public static final DeferredHolder<Block, Block> BATHTUB_WARPED =
      registerBlock(
          "bathtub_warped", () -> new BathtubBlock(COMMON_PROPERTIES_BATHTUB_WOOD));

  public static final DeferredHolder<Block, Block> BATHTUB_STONE =
      registerBlock(
          "bathtub_stone", () -> new BathtubBlock(COMMON_PROPERTIES_BATHTUB));

  public static final DeferredHolder<Block, Block> BATHTUB_COBBLESTONE =
      registerBlock(
          "bathtub_cobblestone", () -> new BathtubBlock(COMMON_PROPERTIES_BATHTUB));

  public static final DeferredHolder<Block, Block> BATHTUB_IRON =
      registerBlock(
          "bathtub_iron", () -> new BathtubBlock(COMMON_PROPERTIES_BATHTUB_METAL));

  public static final DeferredHolder<Block, Block> BATHTUB_GOLD =
      registerBlock(
          "bathtub_gold", () -> new BathtubBlock(COMMON_PROPERTIES_BATHTUB_METAL));

  public static final DeferredHolder<Block, Block> BATHTUB_COPPER =
      registerBlock(
          "bathtub_copper", () -> new BathtubBlock(COMMON_PROPERTIES_BATHTUB_METAL));

  public static final DeferredHolder<Block, Block> BATHTUB_DIAMOND =
      registerBlock(
          "bathtub_diamond", () -> new BathtubBlock(COMMON_PROPERTIES_BATHTUB_METAL));

  public static final DeferredHolder<Block, Block> HOT_WATER_CORE =
      registerBlock("hot_water_core", () -> new HotWaterCoreBlock(COMMON_PROPERTIES_WOOD));

  public static final DeferredHolder<Block, Block> HERBAL_BATH_CORE =
      registerBlock("herbal_bath_core", () -> new HerbalBathCoreBlock(COMMON_PROPERTIES_CORE));

  public static final DeferredHolder<Block, Block> PEONY_BATH_CORE =
      registerBlock("peony_bath_core", () -> new PeonyBathCoreBlock(COMMON_PROPERTIES_CORE));

  public static final DeferredHolder<Block, Block> ROSE_BATH_CORE =
      registerBlock("rose_bath_core", () -> new RoseBathCoreBlock(COMMON_PROPERTIES_CORE));

  public static final DeferredHolder<Block, Block> MILK_BATH_CORE =
      registerBlock("milk_bath_core", () -> new MilkBathCoreBlock(COMMON_PROPERTIES_CORE));

  public static final DeferredHolder<Block, Block> HONEY_BATH_CORE =
      registerBlock("honey_bath_core", () -> new HoneyBathCoreBlock(COMMON_PROPERTIES_CORE));

  private static <T extends Block> DeferredHolder<Block, T> registerBlock(String name, Supplier<T> block) {
    DeferredHolder<Block, T> toReturn = BLOCKS.register(name, block);
    registerBlockItem(name, toReturn);
    return toReturn;
  }

  private static <T extends Block> DeferredHolder<Item, Item> registerBlockItem(
      String name, DeferredHolder<Block, T> block) {
    return ItemRegister.ITEMS.register(
        name, () -> {
            T b = block.get();
            if (b instanceof BathtubBlock) {
                return new BathtubBlockItem(b, new Item.Properties());
            }
            return new BlockItem(b, new Item.Properties());
        });
  }

  public static void register(IEventBus eventBus) {
    BLOCKS.register(eventBus);
  }
}
