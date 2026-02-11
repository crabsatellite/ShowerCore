package mod.crabmod.showercore.compat;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

/**
 * Tough As Nails compatibility module for ShowerCore.
 * Registers event handlers for cleanup and registers the temperature modifier
 * with TAN's TemperatureHelper.
 *
 * Follows the same pattern as hotBath's ToughAsNailsCompat.
 */
public class ToughAsNailsCompat {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static void init() {
        LOGGER.info("Initializing Tough As Nails compatibility for ShowerCore...");
        // API class verification is handled by CompatManager (requiredApiClasses)
        CompatManager.registerEventHandlers("toughasnails", ToughAsNailsEventHandler.class);
        ShowerTANRegistration.init();
        LOGGER.info("Tough As Nails integration initialized for ShowerCore.");
    }
}
