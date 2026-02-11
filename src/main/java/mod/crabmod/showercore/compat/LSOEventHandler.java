package mod.crabmod.showercore.compat;

import com.mojang.logging.LogUtils;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.slf4j.Logger;

/**
 * Event handler for Legendary Survival Overhaul integration with ShowerCore.
 * Handles shower-related temperature and thirst modifications.
 *
 * All event methods are wrapped with CompatManager.safeEventCall() to ensure
 * that any errors automatically disable this integration without crashing.
 */
public class LSOEventHandler {
    private static final Logger LOGGER = LogUtils.getLogger();

    /**
     * Handle player tick events for shower temperature and thirst effects.
     * When a player is using a shower, apply LSO temperature/thirst modifiers.
     */
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        CompatManager.safeEventCall("legendarysurvivaloverhaul", "onPlayerTick", () -> {
            if (event.phase != TickEvent.Phase.END) return;
            Player player = event.player;

            // Only process on server side
            if (player.level().isClientSide()) {
                return;
            }

            // TODO: Implement shower temperature and thirst effects for LSO
            // This will modify the player's LSO temperature/thirst when using a shower
        });
    }

    /**
     * Clean up player data when they log out to prevent memory leaks.
     */
    @SubscribeEvent
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        CompatManager.safeEventCall("legendarysurvivaloverhaul", "onPlayerLogout", () -> {
            // TODO: Clean up any cached player data for LSO integration
        });
    }

    /**
     * Clean up player data when they die and respawn.
     */
    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        CompatManager.safeEventCall("legendarysurvivaloverhaul", "onPlayerClone", () -> {
            if (event.isWasDeath()) {
                // TODO: Clean up any cached player data for LSO integration
            }
        });
    }
}
