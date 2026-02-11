package mod.crabmod.showercore.compat;

import net.neoforged.fml.ModList;

/**
 * Integration check for Cold Sweat mod.
 * Provides a cached check for whether Cold Sweat is loaded.
 */
public class ColdSweatIntegration {
    private static final String COLD_SWEAT_MOD_ID = "cold_sweat";

    // Cache the result to avoid repeated ModList lookups
    private static Boolean cachedLoaded = null;

    public static boolean isColdSweatLoaded() {
        if (cachedLoaded == null) {
            cachedLoaded = ModList.get().isLoaded(COLD_SWEAT_MOD_ID);
        }
        return cachedLoaded;
    }
}
