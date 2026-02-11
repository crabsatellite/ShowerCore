package mod.crabmod.showercore.compat;

import com.mojang.logging.LogUtils;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import org.slf4j.Logger;

/**
 * Event handler for Legendary Survival Overhaul integration with ShowerCore.
 * Handles temperature and thirst modifications when players use shower blocks.
 *
 * Follows the same pattern as hotBath's LSOEventHandler.
 */
public class LSOEventHandler {
    private static final Logger LOGGER = LogUtils.getLogger();

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        CompatManager.safeEventCall("legendarysurvivaloverhaul", "onPlayerTick", () -> {
            Player player = event.getEntity();

            // Only process on server side
            if (player.level().isClientSide()) {
                return;
            }

            // Tick the immersion modifier to apply/remove shower temperature effects
            ShowerImmersionLSOModifier.tick(player);
        });
    }

    /**
     * Clean up player data when they log out to prevent memory leaks.
     */
    @SubscribeEvent
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        CompatManager.safeEventCall("legendarysurvivaloverhaul", "onPlayerLogout", () -> {
            Player player = event.getEntity();

            ShowerImmersionLSOModifier.cleanup(player);
            ShowerLSOApiHelper.cleanupPlayerCache(player);
        });
    }

    /**
     * Clean up player data when they die and respawn.
     * This prevents stale bath timers from persisting after death.
     */
    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        CompatManager.safeEventCall("legendarysurvivaloverhaul", "onPlayerClone", () -> {
            // Only clean up on death (not dimension change)
            if (event.isWasDeath()) {
                Player original = event.getOriginal();

                ShowerImmersionLSOModifier.cleanup(original);
                ShowerLSOApiHelper.cleanupPlayerCache(original);
            }
        });
    }
}
