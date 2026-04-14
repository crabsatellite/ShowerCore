package mod.crabmod.showercore.compat;

import mod.crabmod.showercore.testutil.TestSourceUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Source-level regression test for Bug T (1.20 only) — in a Serene Seasons
 * winter, bathing in a hot bathtub must grant a sub-season-dependent buff:
 *
 * <ul>
 *   <li>EARLY_WINTER → Resistance I for 10 s</li>
 *   <li>MID_WINTER   → Resistance II + Regeneration I for 10 s</li>
 *   <li>LATE_WINTER  → Resistance I for 10 s</li>
 * </ul>
 *
 * <p>Integration with Serene Seasons uses reflection through
 * {@code ShowerCoreCompat#getWinterSubSeason(Level)} so ShowerCore has no
 * compile-time dependency on SS. This test enforces:
 *
 * <ol>
 *   <li>{@code ShowerCoreCompat.WinterSubSeason} enum with NONE/EARLY/MID/LATE.</li>
 *   <li>{@code getWinterSubSeason} uses reflection against SS API classes.</li>
 *   <li>SS mod-id gate via {@code ModList.isLoaded("sereneseasons")}.</li>
 *   <li>SS sub-season names EARLY_WINTER / MID_WINTER / LATE_WINTER are
 *       mapped to the enum.</li>
 *   <li>{@code BathtubBlock#entityInside} switches on that enum and applies
 *       the effects at the documented amplifier / duration / cadence.</li>
 * </ol>
 */
class WinterSubSeasonEffectsRegressionTest {

    private static final Path COMPAT_SOURCE = Paths.get(
            "src", "main", "java", "mod", "crabmod", "showercore", "compat", "ShowerCoreCompat.java");

    private static final Path BATHTUB_SOURCE = Paths.get(
            "src", "main", "java", "mod", "crabmod", "showercore", "block", "BathtubBlock.java");

    private static final Pattern ENTITY_INSIDE_SIG = Pattern.compile("\\bvoid\\s+entityInside\\s*\\(");

    private static String compatSource;
    private static String bathtubSource;
    private static String entityInsideBody;

    @BeforeAll
    static void loadSources() throws IOException {
        compatSource = TestSourceUtils.readSource(COMPAT_SOURCE);
        bathtubSource = TestSourceUtils.readSource(BATHTUB_SOURCE);
        entityInsideBody = TestSourceUtils.extractMethodBody(
                bathtubSource, ENTITY_INSIDE_SIG, "BathtubBlock.entityInside");
    }

    @Test
    @DisplayName("ShowerCoreCompat.WinterSubSeason enum has NONE / EARLY / MID / LATE")
    void winterSubSeasonEnumHasAllFour() {
        Pattern enumDecl = Pattern.compile("enum\\s+WinterSubSeason\\s*\\{([^}]*)\\}", Pattern.DOTALL);
        java.util.regex.Matcher m = enumDecl.matcher(compatSource);
        assertTrue(m.find(), "ShowerCoreCompat must declare 'enum WinterSubSeason { ... }'.");

        String enumBody = m.group(1);
        for (String name : new String[] {"NONE", "EARLY", "MID", "LATE"}) {
            assertTrue(Pattern.compile("\\b" + name + "\\b").matcher(enumBody).find(),
                    "WinterSubSeason enum body must contain '" + name + "'. BathtubBlock switches on these "
                            + "four constants; dropping any one will break the corresponding sub-season buff.");
        }
    }

    @Test
    @DisplayName("getWinterSubSeason uses ModList.isLoaded(\"sereneseasons\") gate")
    void getWinterSubSeasonGatesOnModLoaded() {
        Pattern gate = Pattern.compile(
                "ModList\\s*\\.\\s*get\\s*\\(\\s*\\)\\s*\\.\\s*isLoaded\\s*\\(\\s*\"sereneseasons\"\\s*\\)");
        assertTrue(gate.matcher(compatSource).find(),
                "ShowerCoreCompat must gate the SS code path on 'ModList.get().isLoaded(\"sereneseasons\")'. "
                        + "Without this gate the reflection lookup tries every tick even without SS installed, "
                        + "wasting performance and potentially logging Class.forName stack traces.");
    }

    @Test
    @DisplayName("getWinterSubSeason reflects into sereneseasons.api.season.{SeasonHelper, ISeasonState, Season$SubSeason}")
    void getWinterSubSeasonUsesReflection() {
        assertTrue(Pattern.compile("sereneseasons\\.api\\.season\\.SeasonHelper").matcher(compatSource).find(),
                "Reflection must look up 'sereneseasons.api.season.SeasonHelper'.");
        assertTrue(Pattern.compile("sereneseasons\\.api\\.season\\.ISeasonState").matcher(compatSource).find(),
                "Reflection must look up 'sereneseasons.api.season.ISeasonState'.");
        assertTrue(Pattern.compile("sereneseasons\\.api\\.season\\.Season\\$SubSeason").matcher(compatSource).find(),
                "Reflection must look up 'sereneseasons.api.season.Season$SubSeason'.");
    }

    @Test
    @DisplayName("getWinterSubSeason maps EARLY_WINTER / MID_WINTER / LATE_WINTER enum names")
    void getWinterSubSeasonMapsAllThreeWinterNames() {
        assertTrue(Pattern.compile("\"EARLY_WINTER\"").matcher(compatSource).find(),
                "getWinterSubSeason must match the SS sub-season name literal 'EARLY_WINTER'.");
        assertTrue(Pattern.compile("\"MID_WINTER\"").matcher(compatSource).find(),
                "getWinterSubSeason must match 'MID_WINTER'.");
        assertTrue(Pattern.compile("\"LATE_WINTER\"").matcher(compatSource).find(),
                "getWinterSubSeason must match 'LATE_WINTER'.");
    }

    @Test
    @DisplayName("BathtubBlock.entityInside calls ShowerCoreCompat.getWinterSubSeason(level)")
    void bathtubCallsGetWinterSubSeason() {
        Pattern call = Pattern.compile(
                "ShowerCoreCompat\\s*\\.\\s*getWinterSubSeason\\s*\\(\\s*level\\s*\\)");
        assertTrue(call.matcher(entityInsideBody).find(),
                "BathtubBlock.entityInside must call 'ShowerCoreCompat.getWinterSubSeason(level)'. "
                        + "Without this call the winter buffs are never applied — Bug T regresses.");
    }

    @Test
    @DisplayName("Winter buff is LivingEntity-only, server-side, and cadence-gated (every 100 ticks)")
    void winterBuffIsServerSideAndGated() {
        int callIdx = entityInsideBody.indexOf("ShowerCoreCompat.getWinterSubSeason");
        assertNotEquals(-1, callIdx, "getWinterSubSeason call not found.");

        int windowStart = Math.max(0, callIdx - 400);
        String guardWindow = entityInsideBody.substring(windowStart, callIdx);

        assertTrue(Pattern.compile("instanceof\\s+LivingEntity").matcher(guardWindow).find(),
                "Winter-buff path must be guarded by 'instanceof LivingEntity' so items and arrows don't "
                        + "receive MobEffects.");
        assertTrue(Pattern.compile("tickCount\\s*%\\s*100\\s*==\\s*0").matcher(guardWindow).find(),
                "Winter-buff path must be gated by 'entity.tickCount % 100 == 0' (once every 5s). Refreshing "
                        + "a 200-tick effect every tick causes effect-icon flicker and wasted traffic.");
    }

    @Test
    @DisplayName("EARLY and LATE sub-seasons apply Resistance I (duration 200, amplifier 0)")
    void earlyAndLateApplyResistance1() {
        assertTrue(Pattern.compile("case\\s+EARLY\\s*:").matcher(entityInsideBody).find(),
                "switch must contain 'case EARLY:'.");
        assertTrue(Pattern.compile("case\\s+LATE\\s*:").matcher(entityInsideBody).find(),
                "switch must contain 'case LATE:'.");

        Pattern resistance1 = Pattern.compile(
                "MobEffects\\s*\\.\\s*DAMAGE_RESISTANCE\\s*,\\s*200\\s*,\\s*0");
        assertTrue(resistance1.matcher(entityInsideBody).find(),
                "Must apply 'new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 200, 0, ...)' (Resistance I "
                        + "for 10s) for EARLY/LATE winter. If duration != 200 or amplifier != 0, the buff "
                        + "strength or refresh cadence is wrong.");
    }

    @Test
    @DisplayName("MID sub-season applies Resistance II AND Regeneration I")
    void midAppliesResistance2AndRegen1() {
        assertTrue(Pattern.compile("case\\s+MID\\s*:").matcher(entityInsideBody).find(),
                "switch must contain 'case MID:'.");

        Pattern resistance2 = Pattern.compile(
                "MobEffects\\s*\\.\\s*DAMAGE_RESISTANCE\\s*,\\s*200\\s*,\\s*1");
        assertTrue(resistance2.matcher(entityInsideBody).find(),
                "MID winter must apply Resistance II (amplifier 1) via 'MobEffectInstance("
                        + "MobEffects.DAMAGE_RESISTANCE, 200, 1, ...)'.");

        Pattern regen1 = Pattern.compile(
                "MobEffects\\s*\\.\\s*REGENERATION\\s*,\\s*200\\s*,\\s*0");
        assertTrue(regen1.matcher(entityInsideBody).find(),
                "MID winter must also apply Regeneration I via 'MobEffectInstance(MobEffects.REGENERATION, "
                        + "200, 0, ...)'. Missing this makes MID winter identical to EARLY/LATE — a silent "
                        + "regression that's easy to introduce by dropping one statement during a refactor.");
    }
}
