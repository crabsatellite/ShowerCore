package mod.crabmod.showercore.compat;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

/**
 * Legendary Survival Overhaul compatibility initialization for ShowerCore.
 * Registers event handlers for LSO temperature and thirst integration.
 */
public class LSOCompat {
    private static final Logger LOGGER = LogUtils.getLogger();

    /**
     * Initialize LSO compatibility.
     * Registers event handlers on the Forge EVENT_BUS via CompatManager.
     */
    public static void init() {
        LOGGER.info("Initializing Legendary Survival Overhaul compatibility for ShowerCore...");
        CompatManager.registerEventHandlers("legendarysurvivaloverhaul", LSOEventHandler.class);
        LOGGER.info("Legendary Survival Overhaul event handler registered.");
    }
}
