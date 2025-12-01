package mod.crabmod.showercore;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
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
                  "minecraft:mossy_cobblestone", "minecraft:moss_block",
                  "minecraft:vine", "minecraft:oak_leaves",
                  "minecraft:spruce_leaves", "minecraft:birch_leaves",
                  "minecraft:jungle_leaves", "minecraft:acacia_leaves",
                  "minecraft:dark_oak_leaves", "minecraft:mangrove_leaves",
                  "minecraft:cherry_leaves", "minecraft:azalea_leaves",
                  "minecraft:flowering_azalea_leaves"),
              Config::validateBlockName);

  private static final ForgeConfigSpec.ConfigValue<List<? extends String>> STEAM_FLUIDS =
      BUILDER
          .comment("Fluids that produce steam in the bathtub.",
                   "Add fluid registry names here, e.g., 'minecraft:lava' or 'some_mod:hot_spring_water'.",
                   "Default: empty (only built-in hot fluids produce steam)")
          .defineListAllowEmpty(
              "steam_fluids",
              List.of(),
              Config::validateFluidName);

  private static final ForgeConfigSpec.ConfigValue<List<? extends String>> RUBBER_DUCK_DESTROY_FLUIDS =
      BUILDER
          .comment("Fluids that will destroy the Rubber Duck (e.g. lava)")
          .defineListAllowEmpty(
              "rubber_duck_destroy_fluids",
              List.of("minecraft:lava", "minecraft:flowing_lava"),
              Config::validateFluidName);

  static final ForgeConfigSpec SPEC = BUILDER.build();

  public static Set<Block> hotWaterCoreBlocks;
  public static Set<Block> honeyBathCoreBlocks;
  public static Set<Block> milkBathCoreBlocks;
  public static Set<Block> roseBathCoreBlocks;
  public static Set<Block> peonyBathCoreBlocks;
  public static Set<Block> herbalBathCoreBlocks;
  public static Set<Fluid> steamFluids;
  public static Set<Fluid> rubberDuckDestroyFluids;

  private static boolean validateBlockName(final Object obj) {
    return obj instanceof final String blockName
        && ForgeRegistries.BLOCKS.containsKey(new ResourceLocation(blockName));
  }

  private static boolean validateFluidName(final Object obj) {
    return obj instanceof final String fluidName
        && ForgeRegistries.FLUIDS.containsKey(new ResourceLocation(fluidName));
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

    steamFluids =
        STEAM_FLUIDS.get().stream()
            .map(fluidName -> ForgeRegistries.FLUIDS.getValue(new ResourceLocation(fluidName)))
            .collect(Collectors.toSet());

    rubberDuckDestroyFluids =
        RUBBER_DUCK_DESTROY_FLUIDS.get().stream()
            .map(fluidName -> ForgeRegistries.FLUIDS.getValue(new ResourceLocation(fluidName)))
            .collect(Collectors.toSet());
  }
}
