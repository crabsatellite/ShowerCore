package mod.crabmod.showercore.compat;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import sfiomn.legendarysurvivaloverhaul.api.temperature.TemperatureUtil;
import sfiomn.legendarysurvivaloverhaul.registry.MobEffectRegistry;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Helper class for LSO API calls in ShowerCore.
 * Only loaded when Legendary Survival Overhaul is present.
 *
 * Follows the same pattern as hotBath's LSOApiHelper, providing temperature
 * modification and cold resistance/immunity effects for players using
 * ShowerCore bathtubs and showers.
 */
public class ShowerLSOApiHelper {

    // Cached Holders (initialized lazily to avoid class loading issues)
    private static Holder<MobEffect> COLD_RESISTANCE_HOLDER;
    private static Holder<MobEffect> COLD_IMMUNITY_HOLDER;

    // Cache for temperature modifier values to avoid redundant API calls
    private static final Map<UUID, CachedTempData> TEMP_MODIFIER_CACHE = new ConcurrentHashMap<>();
    private static final double TEMP_UPDATE_THRESHOLD = 0.5;

    private record CachedTempData(double appliedWarmth, long lastCalculationTime) {}

    // Immersion: Cold resistance accumulation
    private static final int COLD_RESISTANCE_AMPLIFIER = 2;
    private static final int MAX_RESISTANCE_DURATION = 6000; // 5 minutes

    // Bath temperature modifier
    private static final UUID SHOWER_TEMP_MODIFIER_UUID = UUID.fromString("b2c3d4e5-f6a7-8901-bcde-f12345678901");
    private static final float TARGET_WARM_TEMPERATURE = 27.0f; // HOT zone middle

    // Cold immunity effect - prevents shivering after leaving bath/shower
    private static final int COLD_IMMUNITY_DURATION = 200; // 10 seconds

    // How often to fully recalculate temperature (5 seconds in game ticks)
    private static final long TEMP_RECALCULATION_INTERVAL = 100;

    private static Holder<MobEffect> getColdResistanceHolder() {
        if (COLD_RESISTANCE_HOLDER == null) {
            COLD_RESISTANCE_HOLDER = BuiltInRegistries.MOB_EFFECT.wrapAsHolder(MobEffectRegistry.COLD_RESISTANCE.get());
        }
        return COLD_RESISTANCE_HOLDER;
    }

    private static Holder<MobEffect> getColdImmunityHolder() {
        if (COLD_IMMUNITY_HOLDER == null) {
            COLD_IMMUNITY_HOLDER = BuiltInRegistries.MOB_EFFECT.wrapAsHolder(MobEffectRegistry.COLD_IMMUNITY.get());
        }
        return COLD_IMMUNITY_HOLDER;
    }

    /**
     * Update cold resistance effect by adding duration.
     * Called every 10 seconds of bathing/showering to add 1 minute of resistance.
     */
    public static void updateImmersionResistanceEffect(Player player, int durationToAdd) {
        Holder<MobEffect> coldResistanceHolder = getColdResistanceHolder();

        int currentDuration = 0;
        MobEffectInstance currentEffect = player.getEffect(coldResistanceHolder);
        if (currentEffect != null) {
            currentDuration = currentEffect.getDuration();
        }

        int newDuration = Math.min(currentDuration + durationToAdd, MAX_RESISTANCE_DURATION);

        MobEffectInstance effect = new MobEffectInstance(
                coldResistanceHolder,
                newDuration,
                COLD_RESISTANCE_AMPLIFIER,
                false,
                true,
                true
        );
        player.addEffect(effect);
    }

    /**
     * Apply temperature modifier to warm player to HOT zone (27 degrees C).
     * Only warms if player is colder than target; no effect if already warm.
     * Uses caching to minimize API calls.
     */
    public static void applyBathTemperatureModifier(Player player) {
        UUID playerUUID = player.getUUID();
        long currentTime = player.level().getGameTime();

        CachedTempData cached = TEMP_MODIFIER_CACHE.get(playerUUID);

        boolean needsRecalculation = cached == null ||
                (currentTime - cached.lastCalculationTime()) >= TEMP_RECALCULATION_INTERVAL;

        if (needsRecalculation) {
            float currentTargetTemp = TemperatureUtil.getPlayerTargetTemperature(player);

            double currentModifier = cached != null ? cached.appliedWarmth() : 0;
            float baseTargetTemp = (float) (currentTargetTemp - currentModifier);

            double warmthNeeded = Math.max(0, TARGET_WARM_TEMPERATURE - baseTargetTemp);

            if (cached == null || Math.abs(warmthNeeded - currentModifier) > TEMP_UPDATE_THRESHOLD) {
                TemperatureUtil.addTemperatureModifier(player, warmthNeeded, SHOWER_TEMP_MODIFIER_UUID);
            }
            TEMP_MODIFIER_CACHE.put(playerUUID, new CachedTempData(warmthNeeded, currentTime));
        }
    }

    /**
     * Remove temperature modifier when player leaves bath/shower.
     */
    public static void removeBathTemperatureModifier(Player player) {
        UUID playerUUID = player.getUUID();
        CachedTempData cached = TEMP_MODIFIER_CACHE.get(playerUUID);
        if (cached != null && cached.appliedWarmth() > 0) {
            TemperatureUtil.addTemperatureModifier(player, 0.0, SHOWER_TEMP_MODIFIER_UUID);
        }
        TEMP_MODIFIER_CACHE.remove(playerUUID);
    }

    /**
     * Apply COLD_IMMUNITY effect to prevent shivering.
     * Used after bathing/showering for 10+ seconds, refreshed only when missing or about to expire.
     */
    public static void applyColdImmunityEffect(Player player) {
        Holder<MobEffect> holder = getColdImmunityHolder();

        MobEffectInstance current = player.getEffect(holder);
        if (current == null || current.getDuration() < 100) {
            MobEffectInstance effect = new MobEffectInstance(
                    holder,
                    COLD_IMMUNITY_DURATION,
                    0,
                    false,
                    false,
                    true
            );
            player.addEffect(effect);
        }
    }

    /**
     * Clean up player cache when they log out to prevent memory leaks.
     */
    public static void cleanupPlayerCache(Player player) {
        TEMP_MODIFIER_CACHE.remove(player.getUUID());
    }
}
