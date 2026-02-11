package mod.crabmod.showercore.compat;

import com.mojang.logging.LogUtils;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.slf4j.Logger;

import java.util.UUID;

/**
 * Event handler for Legendary Survival Overhaul integration with ShowerCore.
 * Handles tick-based updates for temperature modifiers.
 *
 * Follows the same pattern as hotBath's LSOEventHandler.
 */
public class LSOEventHandler {
    private static final Logger LOGGER = LogUtils.getLogger();

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        CompatManager.safeEventCall("legendarysurvivaloverhaul", "onPlayerTick", () -> {
            if (event.phase != TickEvent.Phase.END) return;
            Player player = event.player;

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
