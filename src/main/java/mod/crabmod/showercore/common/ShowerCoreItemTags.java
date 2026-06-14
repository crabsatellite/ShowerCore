package mod.crabmod.showercore.common;

import mod.crabmod.showercore.ShowerCore;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public final class ShowerCoreItemTags {
  public static final TagKey<Item> BATH_CORES = tag("creative/bath_cores");
  public static final TagKey<Item> SHOWER_HEADS = tag("creative/shower_heads");
  public static final TagKey<Item> BATHTUBS = tag("creative/bathtubs");
  public static final TagKey<Item> CLAWFOOT_BATHTUBS = tag("creative/clawfoot_bathtubs");
  public static final TagKey<Item> ACCESSORIES = tag("creative/accessories");

  private ShowerCoreItemTags() {}

  private static TagKey<Item> tag(String name) {
    return TagKey.create(Registries.ITEM, new ResourceLocation(ShowerCore.MODID, name));
  }
}
