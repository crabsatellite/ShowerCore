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

@Mod.EventBusSubscriber(modid = ShowerCore.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {
  private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

  private static final ForgeConfigSpec.ConfigValue<List<? extends String>> BLOCK_STRINGS =
      BUILDER
          .comment(
              "ShowerCore Config - select blocks allowed for Shower Structure\n\nAllowed blocks for structure around the shower core")
          .defineListAllowEmpty(
              "blocks",
              List.of(
                  "minecraft:quartz_block",
                  "minecraft:chiseled_quartz_block",
                  "minecraft:quartz_pillar",
                  "minecraft:smooth_quartz"),
              Config::validateBlockName);

  static final ForgeConfigSpec SPEC = BUILDER.build();

  public static Set<Block> blocks;

  private static boolean validateBlockName(final Object obj) {
    return obj instanceof final String blockName
        && ForgeRegistries.BLOCKS.containsKey(new ResourceLocation(blockName));
  }

  @SubscribeEvent
  static void onLoad(final ModConfigEvent event) {
    blocks =
        BLOCK_STRINGS.get().stream()
            .map(blockName -> ForgeRegistries.BLOCKS.getValue(new ResourceLocation(blockName)))
            .collect(Collectors.toSet());
  }
}
