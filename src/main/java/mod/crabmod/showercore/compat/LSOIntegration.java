package mod.crabmod.showercore.compat;

import net.neoforged.fml.ModList;

/**
 * Integration check for Legendary Survival Overhaul mod.
 * Provides a cached check for whether LSO is loaded.
 */
public class LSOIntegration {
    private static final String LSO_MOD_ID = "legendarysurvivaloverhaul";

    // Cache the result to avoid repeated ModList lookups
    private static Boolean cachedLoaded = null;

    public static boolean isLSOLoaded() {
        if (cachedLoaded == null) {
            cachedLoaded = ModList.get().isLoaded(LSO_MOD_ID);
        }
        return cachedLoaded;
    }
}
