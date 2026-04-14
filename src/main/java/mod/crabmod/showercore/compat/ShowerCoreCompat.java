package mod.crabmod.showercore.compat;

import java.lang.reflect.Method;
import mod.crabmod.showercore.Config;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

/**
 * Centralized compatibility handler for ShowerCore (Forge 1.20).
 * All calls to hotBath / Twilight Forest / Alex's Caves APIs are routed through here
 * with try-catch to gracefully handle missing mods.
 */
public class ShowerCoreCompat {

    // ==================== DIRTINESS ====================

    /**
     * Apply gradual dirtiness cleaning for a player in a hot bathtub or shower.
     * Called every tick while the player is in hot water.
     * Uses Forge Capability API (1.20).
     *
     * @param player   The server player to clean
     * @param isMoving Whether the player is moving (1.33x faster cleaning)
     */
    public static void applyDirtinessCleaning(ServerPlayer player, boolean isMoving) {
        if (!Config.isModIntegrationsEnabled()) return;
        try {
            if (!com.crabmod.hotbath.HotBathConfig.isDirtinessEnabled()) return;
            long gameTime = player.level().getGameTime();
            com.crabmod.hotbath.dirtiness.DirtinessCapability.get(player).ifPresent(data -> {
                if (data.progressBath(gameTime, isMoving)) {
                    // Only sync every 5 ticks for performance
                    if (gameTime % 5 == 0) {
                        com.crabmod.hotbath.dirtiness.DirtinessNetworking.syncToClient(player);
                    }
                }
            });
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
            alexsCavesLoaded = net.minecraftforge.fml.ModList.get().isLoaded("alexscaves");
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

    // ==================== SERENE SEASONS ====================

    public enum WinterSubSeason { NONE, EARLY, MID, LATE }

    private static Boolean sereneSeasonsLoaded = null;
    private static boolean sereneSeasonsResolved = false;
    private static Method seasonHelperGetState;
    private static Method seasonStateGetSubSeason;
    private static Method subSeasonName;

    private static boolean isSereneSeasonsLoaded() {
        if (sereneSeasonsLoaded == null) {
            sereneSeasonsLoaded = net.minecraftforge.fml.ModList.get().isLoaded("sereneseasons");
        }
        return sereneSeasonsLoaded;
    }

    private static void resolveSereneSeasons() {
        if (sereneSeasonsResolved) return;
        sereneSeasonsResolved = true;
        try {
            Class<?> seasonHelper = Class.forName("sereneseasons.api.season.SeasonHelper");
            seasonHelperGetState = seasonHelper.getMethod("getSeasonState", Level.class);
            Class<?> iSeasonState = Class.forName("sereneseasons.api.season.ISeasonState");
            seasonStateGetSubSeason = iSeasonState.getMethod("getSubSeason");
            Class<?> subSeason = Class.forName("sereneseasons.api.season.Season$SubSeason");
            subSeasonName = subSeason.getMethod("name");
        } catch (Exception ignored) {
            seasonHelperGetState = null;
        }
    }

    /**
     * Returns the current winter sub-season at the player's level, or NONE if not in winter
     * or if Serene Seasons is not installed.
     */
    public static WinterSubSeason getWinterSubSeason(Level level) {
        if (!Config.isModIntegrationsEnabled()) return WinterSubSeason.NONE;
        if (!isSereneSeasonsLoaded()) return WinterSubSeason.NONE;
        resolveSereneSeasons();
        if (seasonHelperGetState == null) return WinterSubSeason.NONE;
        try {
            Object state = seasonHelperGetState.invoke(null, level);
            if (state == null) return WinterSubSeason.NONE;
            Object sub = seasonStateGetSubSeason.invoke(state);
            if (sub == null) return WinterSubSeason.NONE;
            String name = (String) subSeasonName.invoke(sub);
            if ("EARLY_WINTER".equals(name)) return WinterSubSeason.EARLY;
            if ("MID_WINTER".equals(name)) return WinterSubSeason.MID;
            if ("LATE_WINTER".equals(name)) return WinterSubSeason.LATE;
            return WinterSubSeason.NONE;
        } catch (Exception ignored) {
            return WinterSubSeason.NONE;
        }
    }
}
