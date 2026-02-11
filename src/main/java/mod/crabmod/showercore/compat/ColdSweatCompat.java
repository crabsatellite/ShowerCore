package mod.crabmod.showercore.compat;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

/**
 * Cold Sweat compatibility module for ShowerCore.
 * Registers event handlers for temperature integration with shower blocks.
 */
public class ColdSweatCompat {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static void init() {
        LOGGER.info("Initializing Cold Sweat compatibility for ShowerCore...");
        // API class verification is handled by CompatManager (requiredApiClasses)
        CompatManager.registerEventHandlers("cold_sweat", ColdSweatEventHandler.class);
        LOGGER.info("Cold Sweat event handler registered for ShowerCore.");
    }
}
