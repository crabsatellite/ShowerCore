package mod.crabmod.showercore.compat;

import mod.crabmod.showercore.Config;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

/**
 * Centralized compatibility handler for ShowerCore.
 * All calls to hotBath / Twilight Forest / Alex's Caves APIs are routed through here
 * with try-catch to gracefully handle missing mods.
 */
public class ShowerCoreCompat {

    // ==================== DIRTINESS ====================

    /**
     * Apply gradual dirtiness cleaning for a player in a hot bathtub or shower.
     * Called every tick while the player is in hot water.
     *
     * @param player   The server player to clean
     * @param isMoving Whether the player is moving (1.33x faster cleaning)
     */
    public static void applyDirtinessCleaning(ServerPlayer player, boolean isMoving) {
        if (!Config.isModIntegrationsEnabled()) return;
        try {
            if (!com.crabmod.hotbath.HotBathConfig.isDirtinessEnabled()) return;
            com.crabmod.hotbath.dirtiness.DirtinessData data = player.getData(
                    com.crabmod.hotbath.dirtiness.DirtinessAttachment.DIRTINESS);
            long gameTime = player.level().getGameTime();
            if (data.progressBath(gameTime, isMoving)) {
                // Only sync every 5 ticks for performance
                if (gameTime % 5 == 0) {
                    com.crabmod.hotbath.dirtiness.DirtinessNetworking.syncToClient(player);
                }
            }
        } catch (Exception ignored) {
            // hotBath not installed or dirtiness classes unavailable
        }
    }

    // ==================== TWILIGHT FOREST ====================

    /**
     * Check if an entity is a Twilight Forest ice mob.
     * Returns false if Twilight Forest is not installed.
     */
    public static boolean isTwilightForestIceMob(Entity entity) {
        if (!Config.isModIntegrationsEnabled()) return false;
        try {
            return com.crabmod.hotbath.compat.TwilightForestIntegration.isTwilightForestIceMob(entity);
        } catch (Exception ignored) {
            return false;
        }
    }

    // ==================== ALEX'S CAVES ====================

    private static Boolean alexsCavesLoaded = null;

    private static boolean isAlexsCavesLoaded() {
        if (alexsCavesLoaded == null) {
            alexsCavesLoaded = net.neoforged.fml.ModList.get().isLoaded("alexscaves");
        }
        return alexsCavesLoaded;
    }

    private static Class<?> gummyBearClass = null;
    private static boolean gummyBearClassResolved = false;

    /**
     * Check if an entity is an Alex's Caves GummyBear.
     * Uses reflection to avoid compile-time dependency on Alex's Caves.
     * Returns false if Alex's Caves is not installed.
     */
    public static boolean isGummyBear(Entity entity) {
        if (!Config.isModIntegrationsEnabled()) return false;
        if (!isAlexsCavesLoaded()) return false;
        try {
            if (!gummyBearClassResolved) {
                gummyBearClassResolved = true;
                gummyBearClass = Class.forName("com.github.alexmodguy.alexscaves.server.entity.living.GummyBearEntity");
            }
            return gummyBearClass != null && gummyBearClass.isInstance(entity);
        } catch (Exception ignored) {
            gummyBearClassResolved = true;
            return false;
        }
    }
}
