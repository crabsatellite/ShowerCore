package mod.crabmod.showercore.compat;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Event handler for Tough As Nails integration with ShowerCore.
 * Handles cleanup of temperature modifier tracking data when players
 * log out or die, to prevent memory leaks.
 *
 * The actual temperature modification is handled by {@link ShowerTANPlayerModifier},
 * which is registered with TAN's TemperatureHelper during initialization.
 *
 * Follows the same pattern as hotBath's ToughAsNailsEventHandler.
 */
public class ToughAsNailsEventHandler {

    @SubscribeEvent
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        CompatManager.safeEventCall("toughasnails", "onPlayerLogout", () -> {
            cleanup(event.getEntity());
        });
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.Clone event) {
        CompatManager.safeEventCall("toughasnails", "onPlayerRespawn", () -> {
            if (event.isWasDeath()) {
                cleanup(event.getEntity());
            }
        });
    }

    private static void cleanup(Player player) {
        ShowerTANPlayerModifier.cleanup(player);
    }
}
