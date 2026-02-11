package mod.crabmod.showercore.compat;

import com.mojang.logging.LogUtils;
import mod.crabmod.showercore.utils.CoreUtils;
import net.minecraft.world.entity.player.Player;
import org.slf4j.Logger;
import toughasnails.api.temperature.IPlayerTemperatureModifier;
import toughasnails.api.temperature.TemperatureLevel;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Tough As Nails player temperature modifier for ShowerCore.
 * When a player is in a ShowerCore hot bathtub or under an active shower head,
 * their temperature is set to WARM.
 *
 * Includes a linger effect: after leaving the shower/bath, the WARM temperature
 * persists for 10 seconds before reverting to normal.
 *
 * Follows the same pattern as hotBath's HotBathTANPlayerModifier.
 */
public class ShowerTANPlayerModifier implements IPlayerTemperatureModifier {
    private static final Logger LOGGER = LogUtils.getLogger();

    // Track when players were last seen in a hot bath/shower
    private static final Map<UUID, Long> LAST_IN_SHOWER = new ConcurrentHashMap<>();
    private static final long LINGER_DURATION_MS = 10_000;

    public static void cleanup(Player player) {
        LAST_IN_SHOWER.remove(player.getUUID());
    }

    @Override
    public TemperatureLevel modify(Player player, TemperatureLevel current) {
        // Check if the player is in a ShowerCore hot bathtub or under an active shower
        boolean inShower = CoreUtils.isPlayerInShowerCoreHotWater(player);
        long currentTime = System.currentTimeMillis();
        UUID uuid = player.getUUID();

        if (inShower) {
            if (!LAST_IN_SHOWER.containsKey(uuid)) {
                LOGGER.info("Player {} entered ShowerCore hot water. Applying WARM temperature.",
                        player.getName().getString());
            }
            // Update last seen time
            LAST_IN_SHOWER.put(uuid, currentTime);
            // Return WARM temperature level
            return TemperatureLevel.WARM;
        } else {
            // Check for linger effect
            Long lastSeen = LAST_IN_SHOWER.get(uuid);
            if (lastSeen != null) {
                if (currentTime - lastSeen < LINGER_DURATION_MS) {
                    return TemperatureLevel.WARM;
                } else {
                    // Cleanup if expired
                    LAST_IN_SHOWER.remove(uuid);
                    LOGGER.info("Player {} ShowerCore warm effect expired.", player.getName().getString());
                }
            }
        }

        // Not in ShowerCore hot water and no linger effect, return current temperature unchanged
        return current;
    }
}
