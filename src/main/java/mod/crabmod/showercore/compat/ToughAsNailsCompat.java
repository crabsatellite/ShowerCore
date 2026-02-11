package mod.crabmod.showercore.compat;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

/**
 * Tough As Nails compatibility initialization for ShowerCore.
 * Registers event handlers for Tough As Nails temperature and thirst integration.
 */
public class ToughAsNailsCompat {
    private static final Logger LOGGER = LogUtils.getLogger();

    /**
     * Initialize Tough As Nails compatibility.
     * Registers event handlers on the Forge EVENT_BUS via CompatManager.
     */
    public static void init() {
        LOGGER.info("Initializing Tough As Nails compatibility for ShowerCore...");
        CompatManager.registerEventHandlers("toughasnails", ToughAsNailsEventHandler.class);
        LOGGER.info("Tough As Nails event handler registered.");
    }
}
