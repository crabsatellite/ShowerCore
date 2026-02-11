package mod.crabmod.showercore.compat;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

/**
 * Legendary Survival Overhaul compatibility module for ShowerCore.
 * Registers event handlers for temperature and thirst integration with shower blocks.
 */
public class LSOCompat {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static void init() {
        LOGGER.info("Initializing Legendary Survival Overhaul compatibility for ShowerCore...");
        // API class verification is handled by CompatManager (requiredApiClasses)
        CompatManager.registerEventHandlers("legendarysurvivaloverhaul", LSOEventHandler.class);
        LOGGER.info("Legendary Survival Overhaul event handler registered for ShowerCore.");
    }
}
