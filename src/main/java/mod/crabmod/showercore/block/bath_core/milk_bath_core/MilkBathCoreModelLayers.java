package mod.crabmod.showercore.block.bath_core.milk_bath_core;

import mod.crabmod.showercore.ShowerCore;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;

public class MilkBathCoreModelLayers {
  public static final ModelLayerLocation MILK_BATH_CORE_CAGE =
      new ModelLayerLocation(
          new ResourceLocation(ShowerCore.MODID, "milk_bath_core/cage"), "main");

  public static final ModelLayerLocation MILK_BATH_CORE_SHELL =
      new ModelLayerLocation(
          new ResourceLocation(ShowerCore.MODID, "milk_bath_core/shell"), "main");

  public static final ModelLayerLocation MILK_BATH_CORE_EYE =
      new ModelLayerLocation(
          new ResourceLocation(ShowerCore.MODID, "milk_bath_core/eye"), "main");

  public static final ModelLayerLocation MILK_BATH_CORE_WIND =
      new ModelLayerLocation(
          new ResourceLocation(ShowerCore.MODID, "milk_bath_core/wind"), "main");
}
