package mod.crabmod.showercore.compat;

import net.neoforged.fml.ModList;

/**
 * Integration check for Tough As Nails mod.
 * Provides a cached check for whether Tough As Nails is loaded.
 */
public class ToughAsNailsIntegration {
    private static final String TAN_MOD_ID = "toughasnails";

    // Cache the result to avoid repeated ModList lookups
    private static Boolean cachedLoaded = null;

    public static boolean isToughAsNailsLoaded() {
        if (cachedLoaded == null) {
            cachedLoaded = ModList.get().isLoaded(TAN_MOD_ID);
        }
        return cachedLoaded;
    }
}
