package mod.crabmod.showercore.compat;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

/**
 * Tough As Nails compatibility module for ShowerCore.
 * Registers event handlers for temperature and thirst integration with shower blocks.
 */
public class ToughAsNailsCompat {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static void init() {
        LOGGER.info("Initializing Tough As Nails compatibility for ShowerCore...");
        // API class verification is handled by CompatManager (requiredApiClasses)
        CompatManager.registerEventHandlers("toughasnails", ToughAsNailsEventHandler.class);
        LOGGER.info("Tough As Nails event handler registered for ShowerCore.");
    }
}
