package mod.crabmod.showercore;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.registries.ForgeRegistries;

// Config class to organize and configure blocks allowed for each specific core type
@Mod.EventBusSubscriber(modid = ShowerCore.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {
  private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

  private static final ForgeConfigSpec.ConfigValue<List<? extends String>> HOT_WATER_CORE_BLOCKS =
      BUILDER
          .comment("Allowed blocks for Hot Water Core activation")
          .defineListAllowEmpty(
              "hot_water_core_blocks",
              List.of("minecraft:prismarine", "minecraft:prismarine_bricks"),
              Config::validateBlockName);

  private static final ForgeConfigSpec.ConfigValue<List<? extends String>> HONEY_BATH_CORE_BLOCKS =
      BUILDER
          .comment("Allowed blocks for Honey Bath Core activation")
          .defineListAllowEmpty(
              "honey_bath_core_blocks",
              List.of("minecraft:honeycomb_block", "minecraft:honey_block"),
              Config::validateBlockName);

  private static final ForgeConfigSpec.ConfigValue<List<? extends String>> MILK_BATH_CORE_BLOCKS =
      BUILDER
          .comment("Allowed blocks for Milk Bath Core activation")
          .defineListAllowEmpty(
              "milk_bath_core_blocks",
              List.of(
                  "minecraft:quartz_block", "minecraft:chiseled_quartz_block",
                  "minecraft:quartz_pillar", "minecraft:smooth_quartz"),
              Config::validateBlockName);

  private static final ForgeConfigSpec.ConfigValue<List<? extends String>> ROSE_BATH_CORE_BLOCKS =
      BUILDER
          .comment("Allowed blocks for Rose Bath Core activation")
          .defineListAllowEmpty(
              "rose_bath_core_blocks",
              List.of(
                  "minecraft:red_wool",
                  "minecraft:red_concrete",
                  "minecraft:red_glazed_terracotta"),
              Config::validateBlockName);

  private static final ForgeConfigSpec.ConfigValue<List<? extends String>> PEONY_BATH_CORE_BLOCKS =
      BUILDER
          .comment("Allowed blocks for Peony Bath Core activation")
          .defineListAllowEmpty(
              "peony_bath_core_blocks",
              List.of(
                  "minecraft:pink_wool",
                  "minecraft:pink_concrete",
                  "minecraft:pink_glazed_terracotta"),
              Config::validateBlockName);

  private static final ForgeConfigSpec.ConfigValue<List<? extends String>> HERBAL_BATH_CORE_BLOCKS =
      BUILDER
          .comment("Allowed blocks for Herbal Bath Core activation")
          .defineListAllowEmpty(
              "herbal_bath_core_blocks",
              List.of(
                  "minecraft:oak_log",
                  "minecraft:stripped_oak_log",
                  "minecraft:oak_leaves",
                  "minecraft:spruce_log",
                  "minecraft:stripped_spruce_log",
                  "minecraft:spruce_leaves",
                  "minecraft:birch_log",
                  "minecraft:stripped_birch_log",
                  "minecraft:birch_leaves",
                  "minecraft:jungle_log",
                  "minecraft:stripped_jungle_log",
                  "minecraft:jungle_leaves",
                  "minecraft:acacia_log",
                  "minecraft:stripped_acacia_log",
                  "minecraft:acacia_leaves",
                  "minecraft:dark_oak_log",
                  "minecraft:stripped_dark_oak_log",
                  "minecraft:dark_oak_leaves",
                  "minecraft:azalea_leaves",
                  "minecraft:flowering_azalea_leaves",
                  "minecraft:mangrove_log",
                  "minecraft:stripped_mangrove_log",
                  "minecraft:mangrove_leaves",
                  "minecraft:cherry_log",
                  "minecraft:stripped_cherry_log",
                  "minecraft:cherry_leaves"),
              Config::validateBlockName);

  static final ForgeConfigSpec SPEC = BUILDER.build();

  public static Set<Block> hotWaterCoreBlocks;
  public static Set<Block> honeyBathCoreBlocks;
  public static Set<Block> milkBathCoreBlocks;
  public static Set<Block> roseBathCoreBlocks;
  public static Set<Block> peonyBathCoreBlocks;
  public static Set<Block> herbalBathCoreBlocks;

  private static boolean validateBlockName(final Object obj) {
    return obj instanceof final String blockName
        && ForgeRegistries.BLOCKS.containsKey(new ResourceLocation(blockName));
  }

  @SubscribeEvent
  static void onLoad(final ModConfigEvent event) {
    hotWaterCoreBlocks =
        HOT_WATER_CORE_BLOCKS.get().stream()
            .map(blockName -> ForgeRegistries.BLOCKS.getValue(new ResourceLocation(blockName)))
            .collect(Collectors.toSet());

    honeyBathCoreBlocks =
        HONEY_BATH_CORE_BLOCKS.get().stream()
            .map(blockName -> ForgeRegistries.BLOCKS.getValue(new ResourceLocation(blockName)))
            .collect(Collectors.toSet());

    milkBathCoreBlocks =
        MILK_BATH_CORE_BLOCKS.get().stream()
            .map(blockName -> ForgeRegistries.BLOCKS.getValue(new ResourceLocation(blockName)))
            .collect(Collectors.toSet());

    roseBathCoreBlocks =
        ROSE_BATH_CORE_BLOCKS.get().stream()
            .map(blockName -> ForgeRegistries.BLOCKS.getValue(new ResourceLocation(blockName)))
            .collect(Collectors.toSet());

    peonyBathCoreBlocks =
        PEONY_BATH_CORE_BLOCKS.get().stream()
            .map(blockName -> ForgeRegistries.BLOCKS.getValue(new ResourceLocation(blockName)))
            .collect(Collectors.toSet());

    herbalBathCoreBlocks =
        HERBAL_BATH_CORE_BLOCKS.get().stream()
            .map(blockName -> ForgeRegistries.BLOCKS.getValue(new ResourceLocation(blockName)))
            .collect(Collectors.toSet());
  }
}
