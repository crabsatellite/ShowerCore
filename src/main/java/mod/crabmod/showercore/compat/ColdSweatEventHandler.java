package mod.crabmod.showercore.compat;

import com.mojang.logging.LogUtils;
import com.momosoftworks.coldsweat.api.event.core.init.DefaultTempModifiersEvent;
import com.momosoftworks.coldsweat.api.event.core.registry.TempModifierRegisterEvent;
import com.momosoftworks.coldsweat.api.util.Temperature;
import com.momosoftworks.coldsweat.api.util.placement.Matcher;
import com.momosoftworks.coldsweat.api.util.placement.Placement;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.slf4j.Logger;

/**
 * Event handler for Cold Sweat integration with ShowerCore.
 * Registers and applies a temperature modifier that warms players
 * when they are in a ShowerCore hot bathtub or under an active shower head.
 *
 * Follows the same pattern as hotBath's ColdSweatEventHandler.
 */
public class ColdSweatEventHandler {
    private static final Logger LOGGER = LogUtils.getLogger();

    @SubscribeEvent
    @SuppressWarnings("removal")
    public static void onTempModifierRegister(TempModifierRegisterEvent event) {
        CompatManager.safeEventCall("cold_sweat", "onTempModifierRegister", () -> {
            LOGGER.info("Registering ShowerCore temperature modifiers with Cold Sweat...");
            event.register(new ResourceLocation("showercore:immersion"), ShowerImmersionColdSweatModifier::new);
            LOGGER.info("Successfully registered ShowerCore temperature modifiers!");
        });
    }

    @SubscribeEvent
    public static void onDefaultModifiers(DefaultTempModifiersEvent event) {
        CompatManager.safeEventCall("cold_sweat", "onDefaultModifiers", () -> {
            event.addModifier(
                    Temperature.Trait.WORLD,
                    new ShowerImmersionColdSweatModifier(),
                    Placement.LAST.noDuplicates(Matcher.SAME_CLASS)
            );
        });
    }
}
