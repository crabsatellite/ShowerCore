package mod.crabmod.showercore.block.bath_core.hot_water_core;

import mod.crabmod.showercore.ShowerCore;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;

public class HotWaterCoreModelLayers {
  public static final ModelLayerLocation HOT_WATER_CORE_CAGE =
      new ModelLayerLocation(new ResourceLocation(ShowerCore.MODID, "hot_water_core/cage"), "main");

  public static final ModelLayerLocation HOT_WATER_CORE_SHELL =
      new ModelLayerLocation(new ResourceLocation(ShowerCore.MODID, "hot_water_core/shell"), "main");

  public static final ModelLayerLocation HOT_WATER_CORE_EYE =
      new ModelLayerLocation(new ResourceLocation(ShowerCore.MODID, "hot_water_core/eye"), "main");

  public static final ModelLayerLocation HOT_WATER_CORE_WIND =
      new ModelLayerLocation(new ResourceLocation(ShowerCore.MODID, "hot_water_core/wind"), "main");
}
