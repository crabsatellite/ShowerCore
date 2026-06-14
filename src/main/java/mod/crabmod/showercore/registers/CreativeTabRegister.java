package mod.crabmod.showercore.registers;

import java.util.function.Consumer;
import mod.crabmod.showercore.ShowerCore;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class CreativeTabRegister {
  public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
      DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ShowerCore.MODID);

  public static final RegistryObject<CreativeModeTab> BATHTUBS =
      CREATIVE_TABS.register(
          "bathtubs",
          () ->
              CreativeModeTab.builder()
                  .title(Component.translatable("itemGroup.showercore.bathtubs"))
                  .icon(() -> new ItemStack(BlocksRegister.BATHTUB_CLAWFOOT_WHITE.get()))
                  .displayItems((parameters, output) -> addBathtubs(output::accept))
                  .build());

  public static void addBathtubs(Consumer<ItemLike> output) {
    output.accept(BlocksRegister.BATHTUB_WHITE.get());
    output.accept(BlocksRegister.BATHTUB_ORANGE.get());
    output.accept(BlocksRegister.BATHTUB_MAGENTA.get());
    output.accept(BlocksRegister.BATHTUB_LIGHT_BLUE.get());
    output.accept(BlocksRegister.BATHTUB_YELLOW.get());
    output.accept(BlocksRegister.BATHTUB_LIME.get());
    output.accept(BlocksRegister.BATHTUB_PINK.get());
    output.accept(BlocksRegister.BATHTUB_GRAY.get());
    output.accept(BlocksRegister.BATHTUB_LIGHT_GRAY.get());
    output.accept(BlocksRegister.BATHTUB_CYAN.get());
    output.accept(BlocksRegister.BATHTUB_PURPLE.get());
    output.accept(BlocksRegister.BATHTUB_BLUE.get());
    output.accept(BlocksRegister.BATHTUB_BROWN.get());
    output.accept(BlocksRegister.BATHTUB_GREEN.get());
    output.accept(BlocksRegister.BATHTUB_RED.get());
    output.accept(BlocksRegister.BATHTUB_BLACK.get());
    output.accept(BlocksRegister.BATHTUB_OAK.get());
    output.accept(BlocksRegister.BATHTUB_SPRUCE.get());
    output.accept(BlocksRegister.BATHTUB_BIRCH.get());
    output.accept(BlocksRegister.BATHTUB_JUNGLE.get());
    output.accept(BlocksRegister.BATHTUB_ACACIA.get());
    output.accept(BlocksRegister.BATHTUB_DARK_OAK.get());
    output.accept(BlocksRegister.BATHTUB_MANGROVE.get());
    output.accept(BlocksRegister.BATHTUB_CHERRY.get());
    output.accept(BlocksRegister.BATHTUB_BAMBOO.get());
    output.accept(BlocksRegister.BATHTUB_CRIMSON.get());
    output.accept(BlocksRegister.BATHTUB_WARPED.get());
    output.accept(BlocksRegister.BATHTUB_STONE.get());
    output.accept(BlocksRegister.BATHTUB_COBBLESTONE.get());
    output.accept(BlocksRegister.BATHTUB_IRON.get());
    output.accept(BlocksRegister.BATHTUB_GOLD.get());
    output.accept(BlocksRegister.BATHTUB_COPPER.get());
    output.accept(BlocksRegister.BATHTUB_DIAMOND.get());
    output.accept(BlocksRegister.BATHTUB_CLAWFOOT_WHITE.get());
    output.accept(BlocksRegister.BATHTUB_CLAWFOOT_ORANGE.get());
    output.accept(BlocksRegister.BATHTUB_CLAWFOOT_MAGENTA.get());
    output.accept(BlocksRegister.BATHTUB_CLAWFOOT_LIGHT_BLUE.get());
    output.accept(BlocksRegister.BATHTUB_CLAWFOOT_YELLOW.get());
    output.accept(BlocksRegister.BATHTUB_CLAWFOOT_LIME.get());
    output.accept(BlocksRegister.BATHTUB_CLAWFOOT_PINK.get());
    output.accept(BlocksRegister.BATHTUB_CLAWFOOT_GRAY.get());
    output.accept(BlocksRegister.BATHTUB_CLAWFOOT_LIGHT_GRAY.get());
    output.accept(BlocksRegister.BATHTUB_CLAWFOOT_CYAN.get());
    output.accept(BlocksRegister.BATHTUB_CLAWFOOT_PURPLE.get());
    output.accept(BlocksRegister.BATHTUB_CLAWFOOT_BLUE.get());
    output.accept(BlocksRegister.BATHTUB_CLAWFOOT_BROWN.get());
    output.accept(BlocksRegister.BATHTUB_CLAWFOOT_GREEN.get());
    output.accept(BlocksRegister.BATHTUB_CLAWFOOT_RED.get());
    output.accept(BlocksRegister.BATHTUB_CLAWFOOT_BLACK.get());
    output.accept(BlocksRegister.BATHTUB_CLAWFOOT_OAK.get());
    output.accept(BlocksRegister.BATHTUB_CLAWFOOT_SPRUCE.get());
    output.accept(BlocksRegister.BATHTUB_CLAWFOOT_BIRCH.get());
    output.accept(BlocksRegister.BATHTUB_CLAWFOOT_JUNGLE.get());
    output.accept(BlocksRegister.BATHTUB_CLAWFOOT_ACACIA.get());
    output.accept(BlocksRegister.BATHTUB_CLAWFOOT_DARK_OAK.get());
    output.accept(BlocksRegister.BATHTUB_CLAWFOOT_MANGROVE.get());
    output.accept(BlocksRegister.BATHTUB_CLAWFOOT_CHERRY.get());
    output.accept(BlocksRegister.BATHTUB_CLAWFOOT_BAMBOO.get());
    output.accept(BlocksRegister.BATHTUB_CLAWFOOT_CRIMSON.get());
    output.accept(BlocksRegister.BATHTUB_CLAWFOOT_WARPED.get());
    output.accept(BlocksRegister.BATHTUB_CLAWFOOT_STONE.get());
    output.accept(BlocksRegister.BATHTUB_CLAWFOOT_COBBLESTONE.get());
    output.accept(BlocksRegister.BATHTUB_CLAWFOOT_IRON.get());
    output.accept(BlocksRegister.BATHTUB_CLAWFOOT_GOLD.get());
    output.accept(BlocksRegister.BATHTUB_CLAWFOOT_COPPER.get());
    output.accept(BlocksRegister.BATHTUB_CLAWFOOT_DIAMOND.get());
  }

  public static void register(IEventBus eventBus) {
    CREATIVE_TABS.register(eventBus);
  }
}
