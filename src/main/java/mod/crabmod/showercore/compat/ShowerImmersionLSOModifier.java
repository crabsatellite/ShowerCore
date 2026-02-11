package mod.crabmod.showercore.compat;

import mod.crabmod.showercore.utils.CoreUtils;
import net.minecraft.world.entity.player.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Handles LSO temperature effects when player is in a ShowerCore hot bathtub
 * or under an active shower head.
 *
 * Follows the same pattern as hotBath's HotBathImmersionLSOModifier:
 * - Warms player to HOT zone while in bath/shower
 * - Accumulates cold resistance every 10 seconds (max 5 min)
 * - Applies cold immunity after 10 seconds (persists 10s after leaving)
 */
public class ShowerImmersionLSOModifier {

    private static final Map<UUID, Integer> BATH_TIMERS = new ConcurrentHashMap<>();
    private static final int UPDATE_INTERVAL = 200; // 10 seconds
    private static final int RESISTANCE_GAIN_PER_UPDATE = 1200; // 1 minute
    private static final int COLD_IMMUNITY_START_DELAY = 200; // Start after 10 seconds
    private static final int TEMP_UPDATE_INTERVAL = 20; // Update temperature modifier every 1 second (20 ticks)

    public static boolean isPlayerInBath(UUID playerUUID) {
        return BATH_TIMERS.containsKey(playerUUID);
    }

    public static void tick(Player player) {
        UUID playerUUID = player.getUUID();
        boolean isInShowerCoreHotWater = CoreUtils.isPlayerInShowerCoreHotWater(player);

        if (isInShowerCoreHotWater) {
            int timer = BATH_TIMERS.getOrDefault(playerUUID, 0) + 1;
            BATH_TIMERS.put(playerUUID, timer);

            // Warm player to HOT zone - update every second
            if (timer % TEMP_UPDATE_INTERVAL == 0) {
                ShowerLSOApiHelper.applyBathTemperatureModifier(player);
            }

            // Accumulate cold resistance every 10 seconds
            if (timer % UPDATE_INTERVAL == 0) {
                ShowerLSOApiHelper.updateImmersionResistanceEffect(player, RESISTANCE_GAIN_PER_UPDATE);
            }

            // Apply cold immunity after 10 seconds (prevents shivering after leaving)
            if (timer >= COLD_IMMUNITY_START_DELAY && timer % TEMP_UPDATE_INTERVAL == 0) {
                ShowerLSOApiHelper.applyColdImmunityEffect(player);
            }
        } else {
            if (BATH_TIMERS.containsKey(playerUUID)) {
                ShowerLSOApiHelper.removeBathTemperatureModifier(player);
                BATH_TIMERS.remove(playerUUID);
            }
        }
    }

    public static void cleanup(Player player) {
        BATH_TIMERS.remove(player.getUUID());
    }
}
