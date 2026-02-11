package mod.crabmod.showercore.compat;

import net.minecraftforge.fml.ModList;

/**
 * Integration check for Legendary Survival Overhaul mod.
 * Provides a cached check for whether LSO is loaded.
 */
public class LSOIntegration {
    private static final String LSO_MOD_ID = "legendarysurvivaloverhaul";

    // Cache the result to avoid repeated ModList lookups
    private static Boolean cachedLoaded = null;

    /**
     * Check if Legendary Survival Overhaul mod is loaded.
     *
     * @return true if LSO is present in the mod list
     */
    public static boolean isLSOLoaded() {
        if (cachedLoaded == null) {
            cachedLoaded = ModList.get().isLoaded(LSO_MOD_ID);
        }
        return cachedLoaded;
    }
}
