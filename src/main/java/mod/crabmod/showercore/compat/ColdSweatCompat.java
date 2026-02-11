package mod.crabmod.showercore.compat;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

/**
 * Cold Sweat compatibility initialization for ShowerCore.
 * Registers event handlers for Cold Sweat temperature integration.
 */
public class ColdSweatCompat {
    private static final Logger LOGGER = LogUtils.getLogger();

    /**
     * Initialize Cold Sweat compatibility.
     * Registers event handlers on the Forge EVENT_BUS via CompatManager.
     */
    public static void init() {
        LOGGER.info("Initializing Cold Sweat compatibility for ShowerCore...");
        CompatManager.registerEventHandlers("cold_sweat", ColdSweatEventHandler.class);
        LOGGER.info("Cold Sweat event handler registered.");
    }
}
