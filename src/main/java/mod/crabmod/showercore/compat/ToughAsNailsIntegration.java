package mod.crabmod.showercore.compat;

import net.minecraftforge.fml.ModList;

/**
 * Integration check for Tough As Nails mod.
 * Provides a cached check for whether Tough As Nails is loaded.
 */
public class ToughAsNailsIntegration {
    private static final String TAN_MOD_ID = "toughasnails";

    // Cache the result to avoid repeated ModList lookups
    private static Boolean cachedLoaded = null;

    /**
     * Check if Tough As Nails mod is loaded.
     *
     * @return true if Tough As Nails is present in the mod list
     */
    public static boolean isToughAsNailsLoaded() {
        if (cachedLoaded == null) {
            cachedLoaded = ModList.get().isLoaded(TAN_MOD_ID);
        }
        return cachedLoaded;
    }
}
