package mod.crabmod.showercore.registers;

import mod.crabmod.showercore.ShowerCore;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

import mod.crabmod.showercore.item.RubberDuckItem;

public class ItemRegister {
  public static final DeferredRegister<Item> ITEMS =
          DeferredRegister.create(Registries.ITEM, ShowerCore.MODID);

  public static final DeferredHolder<Item, Item> RUBBER_DUCK = ITEMS.register("rubber_duck", () -> new RubberDuckItem(new Item.Properties()));

  public static void register(IEventBus eventBus) {
    ITEMS.register(eventBus);
  }
}
