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
      BlockBehaviour.Properties.copy(Blocks.CONDUIT).noOcclusion();

  public static final RegistryObject<Block> SHOWER_HEAD_BLACK =
      registerBlock(
          "shower_head_black", () -> new ShowerHeadBlock(COMMON_PROPERTIES_WOOD_NO_COLLISION));

  public static final RegistryObject<Block> SHOWER_HEAD_WHITE =
      registerBlock(
          "shower_head_white", () -> new ShowerHeadBlock(COMMON_PROPERTIES_WOOD_NO_COLLISION));

  public static final RegistryObject<Block> SHOWER_HEAD_ORANGE =
      registerBlock(
          "shower_head_orange", () -> new ShowerHeadBlock(COMMON_PROPERTIES_WOOD_NO_COLLISION));

  public static final RegistryObject<Block> SHOWER_HEAD_MAGENTA =
      registerBlock(
          "shower_head_magenta", () -> new ShowerHeadBlock(COMMON_PROPERTIES_WOOD_NO_COLLISION));

  public static final RegistryObject<Block> SHOWER_HEAD_LIGHT_BLUE =
      registerBlock(
          "shower_head_light_blue", () -> new ShowerHeadBlock(COMMON_PROPERTIES_WOOD_NO_COLLISION));

  public static final RegistryObject<Block> SHOWER_HEAD_YELLOW =
      registerBlock(
          "shower_head_yellow", () -> new ShowerHeadBlock(COMMON_PROPERTIES_WOOD_NO_COLLISION));

  public static final RegistryObject<Block> SHOWER_HEAD_LIME =
      registerBlock(
          "shower_head_lime", () -> new ShowerHeadBlock(COMMON_PROPERTIES_WOOD_NO_COLLISION));

  public static final RegistryObject<Block> SHOWER_HEAD_PINK =
      registerBlock(
          "shower_head_pink", () -> new ShowerHeadBlock(COMMON_PROPERTIES_WOOD_NO_COLLISION));

  public static final RegistryObject<Block> SHOWER_HEAD_GRAY =
      registerBlock(
          "shower_head_gray", () -> new ShowerHeadBlock(COMMON_PROPERTIES_WOOD_NO_COLLISION));

  public static final RegistryObject<Block> SHOWER_HEAD_LIGHT_GRAY =
      registerBlock(
          "shower_head_light_gray", () -> new ShowerHeadBlock(COMMON_PROPERTIES_WOOD_NO_COLLISION));

  public static final RegistryObject<Block> SHOWER_HEAD_CYAN =
      registerBlock(
          "shower_head_cyan", () -> new ShowerHeadBlock(COMMON_PROPERTIES_WOOD_NO_COLLISION));

  public static final RegistryObject<Block> SHOWER_HEAD_PURPLE =
      registerBlock(
          "shower_head_purple", () -> new ShowerHeadBlock(COMMON_PROPERTIES_WOOD_NO_COLLISION));

  public static final RegistryObject<Block> SHOWER_HEAD_BLUE =
      registerBlock(
          "shower_head_blue", () -> new ShowerHeadBlock(COMMON_PROPERTIES_WOOD_NO_COLLISION));

  public static final RegistryObject<Block> SHOWER_HEAD_BROWN =
      registerBlock(
          "shower_head_brown", () -> new ShowerHeadBlock(COMMON_PROPERTIES_WOOD_NO_COLLISION));

  public static final RegistryObject<Block> SHOWER_HEAD_GREEN =
      registerBlock(
          "shower_head_green", () -> new ShowerHeadBlock(COMMON_PROPERTIES_WOOD_NO_COLLISION));

  public static final RegistryObject<Block> SHOWER_HEAD_RED =
      registerBlock(
          "shower_head_red", () -> new ShowerHeadBlock(COMMON_PROPERTIES_WOOD_NO_COLLISION));

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

  public static final RegistryObject<Block> SHOWER_HEAD_B_STONE =
      registerBlock(
          "shower_head_b_stone", () -> new ShowerHeadBlock(COMMON_PROPERTIES_STONE_NO_COLLISION));

  public static final RegistryObject<Block> SHOWER_HEAD_B_OAK =
      registerBlock(
          "shower_head_b_oak", () -> new ShowerHeadBlock(COMMON_PROPERTIES_WOOD_NO_COLLISION));

  public static final RegistryObject<Block> SHOWER_HEAD_B_IRON =
      registerBlock(
          "shower_head_b_iron", () -> new ShowerHeadBlock(COMMON_PROPERTIES_METAL_NO_COLLISION));

  public static final RegistryObject<Block> SHOWER_HEAD_B_GOLD =
      registerBlock(
          "shower_head_b_gold", () -> new ShowerHeadBlock(COMMON_PROPERTIES_METAL_NO_COLLISION));

  public static final RegistryObject<Block> SHOWER_HEAD_B_DIAMOND =
      registerBlock(
          "shower_head_b_diamond", () -> new ShowerHeadBlock(COMMON_PROPERTIES_METAL_NO_COLLISION));

  public static final RegistryObject<Block> SHOWER_HEAD_B_BLACK =
      registerBlock(
          "shower_head_b_black", () -> new ShowerHeadBlock(COMMON_PROPERTIES_WOOD_NO_COLLISION));

  public static final RegistryObject<Block> SHOWER_HEAD_B_WHITE =
      registerBlock(
          "shower_head_b_white", () -> new ShowerHeadBlock(COMMON_PROPERTIES_WOOD_NO_COLLISION));

  public static final RegistryObject<Block> SHOWER_HEAD_B_ORANGE =
      registerBlock(
          "shower_head_b_orange", () -> new ShowerHeadBlock(COMMON_PROPERTIES_WOOD_NO_COLLISION));

  public static final RegistryObject<Block> SHOWER_HEAD_B_MAGENTA =
      registerBlock(
          "shower_head_b_magenta", () -> new ShowerHeadBlock(COMMON_PROPERTIES_WOOD_NO_COLLISION));

  public static final RegistryObject<Block> SHOWER_HEAD_B_LIGHT_BLUE =
      registerBlock(
          "shower_head_b_light_blue", () -> new ShowerHeadBlock(COMMON_PROPERTIES_WOOD_NO_COLLISION));

  public static final RegistryObject<Block> SHOWER_HEAD_B_YELLOW =
      registerBlock(
          "shower_head_b_yellow", () -> new ShowerHeadBlock(COMMON_PROPERTIES_WOOD_NO_COLLISION));

  public static final RegistryObject<Block> SHOWER_HEAD_B_LIME =
      registerBlock(
          "shower_head_b_lime", () -> new ShowerHeadBlock(COMMON_PROPERTIES_WOOD_NO_COLLISION));

  public static final RegistryObject<Block> SHOWER_HEAD_B_PINK =
      registerBlock(
          "shower_head_b_pink", () -> new ShowerHeadBlock(COMMON_PROPERTIES_WOOD_NO_COLLISION));

  public static final RegistryObject<Block> SHOWER_HEAD_B_GRAY =
      registerBlock(
          "shower_head_b_gray", () -> new ShowerHeadBlock(COMMON_PROPERTIES_WOOD_NO_COLLISION));

  public static final RegistryObject<Block> SHOWER_HEAD_B_LIGHT_GRAY =
      registerBlock(
          "shower_head_b_light_gray", () -> new ShowerHeadBlock(COMMON_PROPERTIES_WOOD_NO_COLLISION));

  public static final RegistryObject<Block> SHOWER_HEAD_B_CYAN =
      registerBlock(
          "shower_head_b_cyan", () -> new ShowerHeadBlock(COMMON_PROPERTIES_WOOD_NO_COLLISION));

  public static final RegistryObject<Block> SHOWER_HEAD_B_PURPLE =
      registerBlock(
          "shower_head_b_purple", () -> new ShowerHeadBlock(COMMON_PROPERTIES_WOOD_NO_COLLISION));

  public static final RegistryObject<Block> SHOWER_HEAD_B_BLUE =
      registerBlock(
          "shower_head_b_blue", () -> new ShowerHeadBlock(COMMON_PROPERTIES_WOOD_NO_COLLISION));

  public static final RegistryObject<Block> SHOWER_HEAD_B_BROWN =
      registerBlock(
          "shower_head_b_brown", () -> new ShowerHeadBlock(COMMON_PROPERTIES_WOOD_NO_COLLISION));

  public static final RegistryObject<Block> SHOWER_HEAD_B_GREEN =
      registerBlock(
          "shower_head_b_green", () -> new ShowerHeadBlock(COMMON_PROPERTIES_WOOD_NO_COLLISION));

  public static final RegistryObject<Block> SHOWER_HEAD_B_RED =
      registerBlock(
          "shower_head_b_red", () -> new ShowerHeadBlock(COMMON_PROPERTIES_WOOD_NO_COLLISION));

  public static final RegistryObject<Block> SHOWER_HEAD_B_SPRUCE =
      registerBlock(
          "shower_head_b_spruce", () -> new ShowerHeadBlock(COMMON_PROPERTIES_WOOD_NO_COLLISION));

  public static final RegistryObject<Block> SHOWER_HEAD_B_BIRCH =
      registerBlock(
          "shower_head_b_birch", () -> new ShowerHeadBlock(COMMON_PROPERTIES_WOOD_NO_COLLISION));

  public static final RegistryObject<Block> SHOWER_HEAD_B_JUNGLE =
      registerBlock(
          "shower_head_b_jungle", () -> new ShowerHeadBlock(COMMON_PROPERTIES_WOOD_NO_COLLISION));

  public static final RegistryObject<Block> SHOWER_HEAD_B_ACACIA =
      registerBlock(
          "shower_head_b_acacia", () -> new ShowerHeadBlock(COMMON_PROPERTIES_WOOD_NO_COLLISION));

  public static final RegistryObject<Block> SHOWER_HEAD_B_DARK_OAK =
      registerBlock(
          "shower_head_b_dark_oak", () -> new ShowerHeadBlock(COMMON_PROPERTIES_WOOD_NO_COLLISION));

  public static final RegistryObject<Block> SHOWER_HEAD_B_MANGROVE =
      registerBlock(
          "shower_head_b_mangrove", () -> new ShowerHeadBlock(COMMON_PROPERTIES_WOOD_NO_COLLISION));

  public static final RegistryObject<Block> SHOWER_HEAD_B_CHERRY =
      registerBlock(
          "shower_head_b_cherry", () -> new ShowerHeadBlock(COMMON_PROPERTIES_WOOD_NO_COLLISION));

  public static final RegistryObject<Block> SHOWER_HEAD_B_BAMBOO =
      registerBlock(
          "shower_head_b_bamboo", () -> new ShowerHeadBlock(COMMON_PROPERTIES_WOOD_NO_COLLISION));

  public static final RegistryObject<Block> SHOWER_HEAD_B_CRIMSON =
      registerBlock(
          "shower_head_b_crimson", () -> new ShowerHeadBlock(COMMON_PROPERTIES_WOOD_NO_COLLISION));

  public static final RegistryObject<Block> SHOWER_HEAD_B_WARPED =
      registerBlock(
          "shower_head_b_warped", () -> new ShowerHeadBlock(COMMON_PROPERTIES_WOOD_NO_COLLISION));

  public static final RegistryObject<Block> SHOWER_HEAD_B_COBBLESTONE =
      registerBlock(
          "shower_head_b_cobblestone", () -> new ShowerHeadBlock(COMMON_PROPERTIES_STONE_NO_COLLISION));

  public static final RegistryObject<Block> SHOWER_HEAD_B_COPPER =
      registerBlock(
          "shower_head_b_copper", () -> new ShowerHeadBlock(COMMON_PROPERTIES_METAL_NO_COLLISION));

  public static final RegistryObject<Block> BATHTUB_WHITE =
      registerBlock(
          "bathtub_white", () -> new BathtubBlock(COMMON_PROPERTIES_BATHTUB));

  public static final RegistryObject<Block> BATHTUB_ORANGE =
      registerBlock(
          "bathtub_orange", () -> new BathtubBlock(COMMON_PROPERTIES_BATHTUB));

  public static final RegistryObject<Block> BATHTUB_MAGENTA =
      registerBlock(
          "bathtub_magenta", () -> new BathtubBlock(COMMON_PROPERTIES_BATHTUB));

  public static final RegistryObject<Block> BATHTUB_LIGHT_BLUE =
      registerBlock(
          "bathtub_light_blue", () -> new BathtubBlock(COMMON_PROPERTIES_BATHTUB));

  public static final RegistryObject<Block> BATHTUB_YELLOW =
      registerBlock(
          "bathtub_yellow", () -> new BathtubBlock(COMMON_PROPERTIES_BATHTUB));

  public static final RegistryObject<Block> BATHTUB_LIME =
      registerBlock(
          "bathtub_lime", () -> new BathtubBlock(COMMON_PROPERTIES_BATHTUB));

  public static final RegistryObject<Block> BATHTUB_PINK =
      registerBlock(
          "bathtub_pink", () -> new BathtubBlock(COMMON_PROPERTIES_BATHTUB));

  public static final RegistryObject<Block> BATHTUB_GRAY =
      registerBlock(
          "bathtub_gray", () -> new BathtubBlock(COMMON_PROPERTIES_BATHTUB));

  public static final RegistryObject<Block> BATHTUB_LIGHT_GRAY =
      registerBlock(
          "bathtub_light_gray", () -> new BathtubBlock(COMMON_PROPERTIES_BATHTUB));

  public static final RegistryObject<Block> BATHTUB_CYAN =
      registerBlock(
          "bathtub_cyan", () -> new BathtubBlock(COMMON_PROPERTIES_BATHTUB));

  public static final RegistryObject<Block> BATHTUB_PURPLE =
      registerBlock(
          "bathtub_purple", () -> new BathtubBlock(COMMON_PROPERTIES_BATHTUB));

  public static final RegistryObject<Block> BATHTUB_BLUE =
      registerBlock(
          "bathtub_blue", () -> new BathtubBlock(COMMON_PROPERTIES_BATHTUB));

  public static final RegistryObject<Block> BATHTUB_BROWN =
      registerBlock(
          "bathtub_brown", () -> new BathtubBlock(COMMON_PROPERTIES_BATHTUB));

  public static final RegistryObject<Block> BATHTUB_GREEN =
      registerBlock(
          "bathtub_green", () -> new BathtubBlock(COMMON_PROPERTIES_BATHTUB));

  public static final RegistryObject<Block> BATHTUB_RED =
      registerBlock(
          "bathtub_red", () -> new BathtubBlock(COMMON_PROPERTIES_BATHTUB));

  public static final RegistryObject<Block> BATHTUB_BLACK =
      registerBlock(
          "bathtub_black", () -> new BathtubBlock(COMMON_PROPERTIES_BATHTUB));

  public static final RegistryObject<Block> BATHTUB_OAK =
      registerBlock(
          "bathtub_oak", () -> new BathtubBlock(COMMON_PROPERTIES_BATHTUB_WOOD));

  public static final RegistryObject<Block> BATHTUB_SPRUCE =
      registerBlock(
          "bathtub_spruce", () -> new BathtubBlock(COMMON_PROPERTIES_BATHTUB_WOOD));

  public static final RegistryObject<Block> BATHTUB_BIRCH =
      registerBlock(
          "bathtub_birch", () -> new BathtubBlock(COMMON_PROPERTIES_BATHTUB_WOOD));

  public static final RegistryObject<Block> BATHTUB_JUNGLE =
      registerBlock(
          "bathtub_jungle", () -> new BathtubBlock(COMMON_PROPERTIES_BATHTUB_WOOD));

  public static final RegistryObject<Block> BATHTUB_ACACIA =
      registerBlock(
          "bathtub_acacia", () -> new BathtubBlock(COMMON_PROPERTIES_BATHTUB_WOOD));

  public static final RegistryObject<Block> BATHTUB_DARK_OAK =
      registerBlock(
          "bathtub_dark_oak", () -> new BathtubBlock(COMMON_PROPERTIES_BATHTUB_WOOD));

  public static final RegistryObject<Block> BATHTUB_MANGROVE =
      registerBlock(
          "bathtub_mangrove", () -> new BathtubBlock(COMMON_PROPERTIES_BATHTUB_WOOD));

  public static final RegistryObject<Block> BATHTUB_CHERRY =
      registerBlock(
          "bathtub_cherry", () -> new BathtubBlock(COMMON_PROPERTIES_BATHTUB_WOOD));

  public static final RegistryObject<Block> BATHTUB_BAMBOO =
      registerBlock(
          "bathtub_bamboo", () -> new BathtubBlock(COMMON_PROPERTIES_BATHTUB_WOOD));

  public static final RegistryObject<Block> BATHTUB_CRIMSON =
      registerBlock(
          "bathtub_crimson", () -> new BathtubBlock(COMMON_PROPERTIES_BATHTUB_WOOD));

  public static final RegistryObject<Block> BATHTUB_WARPED =
      registerBlock(
          "bathtub_warped", () -> new BathtubBlock(COMMON_PROPERTIES_BATHTUB_WOOD));

  public static final RegistryObject<Block> BATHTUB_STONE =
      registerBlock(
          "bathtub_stone", () -> new BathtubBlock(COMMON_PROPERTIES_BATHTUB));

  public static final RegistryObject<Block> BATHTUB_COBBLESTONE =
      registerBlock(
          "bathtub_cobblestone", () -> new BathtubBlock(COMMON_PROPERTIES_BATHTUB));

  public static final RegistryObject<Block> BATHTUB_IRON =
      registerBlock(
          "bathtub_iron", () -> new BathtubBlock(COMMON_PROPERTIES_BATHTUB_METAL));

  public static final RegistryObject<Block> BATHTUB_GOLD =
      registerBlock(
          "bathtub_gold", () -> new BathtubBlock(COMMON_PROPERTIES_BATHTUB_METAL));

  public static final RegistryObject<Block> BATHTUB_COPPER =
      registerBlock(
          "bathtub_copper", () -> new BathtubBlock(COMMON_PROPERTIES_BATHTUB_METAL));

  public static final RegistryObject<Block> BATHTUB_DIAMOND =
      registerBlock(
          "bathtub_diamond", () -> new BathtubBlock(COMMON_PROPERTIES_BATHTUB_METAL));

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
