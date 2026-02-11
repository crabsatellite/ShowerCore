package mod.crabmod.showercore.compat;

import com.momosoftworks.coldsweat.api.temperature.modifier.TempModifier;
import com.momosoftworks.coldsweat.api.util.Temperature;
import com.momosoftworks.coldsweat.util.world.WorldHelper;
import mod.crabmod.showercore.utils.CoreUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.function.Function;

/**
 * Cold Sweat temperature modifier for ShowerCore.
 * Applies a warming effect when the player is in a ShowerCore hot bathtub
 * or under an active shower head with a core installed.
 *
 * Follows the same pattern as hotBath's HotBathImmersionModifier.
 * Target temperature: 40°C (standard hot bath temperature).
 */
public class ShowerImmersionColdSweatModifier extends TempModifier {

    private static final double TARGET_TEMP_C = 40.0;

    public ShowerImmersionColdSweatModifier() {
        super();
    }

    @Override
    protected Function<Double, Double> calculate(LivingEntity entity, Temperature.Trait trait) {
        return CompatManager.safeEventCall("cold_sweat", "ShowerImmersionColdSweatModifier.calculate", () -> {
            // Only modify the WORLD temperature trait
            if (trait != Temperature.Trait.WORLD) {
                return (Function<Double, Double>) (temp -> temp);
            }

            // Only apply to players
            if (!(entity instanceof Player player)) {
                return (Function<Double, Double>) (temp -> temp);
            }

            // Check if the player is in a ShowerCore hot bathtub or under an active shower
            if (CoreUtils.isPlayerInShowerCoreHotWater(player)) {
                double targetTempMC = Temperature.convert(TARGET_TEMP_C, Temperature.Units.C, Temperature.Units.MC, true);

                Level level = entity.level();
                BlockPos pos = entity.blockPosition();
                double worldTempMC = WorldHelper.getBiomeTemperature(level, level.getBiome(pos));

                // Use the warmer of target vs world temperature
                double finalTemp = Math.max(targetTempMC, worldTempMC);
                return (Function<Double, Double>) (temp -> finalTemp);
            }

            // Not in ShowerCore hot water, don't modify temperature
            return (Function<Double, Double>) (temp -> temp);
        }, temp -> temp);
    }
}
