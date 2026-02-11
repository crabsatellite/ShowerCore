package mod.crabmod.showercore.compat;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;
import toughasnails.api.temperature.TemperatureHelper;

/**
 * Registers ShowerCore's temperature modifier with Tough As Nails.
 * Follows the same pattern as hotBath's ToughAsNailsRegistration.
 */
public class ShowerTANRegistration {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static void init() {
        try {
            LOGGER.info("Registering ShowerCore temperature modifiers with Tough As Nails...");
            TemperatureHelper.registerPlayerTemperatureModifier(new ShowerTANPlayerModifier());
            LOGGER.info("Successfully registered ShowerCore temperature modifiers with Tough As Nails!");
        } catch (Exception e) {
            LOGGER.error("Failed to register ShowerCore modifiers with Tough As Nails: {}", e.getMessage(), e);
        }
    }
}
