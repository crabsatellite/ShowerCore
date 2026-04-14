package mod.crabmod.showercore.compat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

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

    @Test
    @DisplayName("ShowerCoreCompat.WinterSubSeason enum has NONE / EARLY / MID / LATE")
    void winterSubSeasonEnumHasAllFour() throws IOException {
        String source = readSource(COMPAT_SOURCE);
        Pattern enumDecl = Pattern.compile(
                "enum\\s+WinterSubSeason\\s*\\{[^}]*NONE[^}]*EARLY[^}]*MID[^}]*LATE[^}]*\\}",
                Pattern.DOTALL);
        assertTrue(enumDecl.matcher(source).find(),
                "ShowerCoreCompat must declare 'enum WinterSubSeason { NONE, EARLY, MID, LATE }'. "
                        + "BathtubBlock switches on these four constants; dropping any one will break "
                        + "the corresponding sub-season buff.");
    }

    @Test
    @DisplayName("getWinterSubSeason uses ModList.isLoaded(\"sereneseasons\") gate")
    void getWinterSubSeasonGatesOnModLoaded() throws IOException {
        String source = readSource(COMPAT_SOURCE);
        Pattern gate = Pattern.compile(
                "ModList\\s*\\.\\s*get\\s*\\(\\s*\\)\\s*\\.\\s*isLoaded\\s*\\(\\s*\"sereneseasons\"\\s*\\)");
        assertTrue(gate.matcher(source).find(),
                "ShowerCoreCompat must gate the SS code path on 'ModList.get().isLoaded(\"sereneseasons\")'. "
                        + "Without this gate the reflection lookup tries every tick even without SS installed, "
                        + "wasting performance and potentially logging Class.forName stack traces.");
    }

    @Test
    @DisplayName("getWinterSubSeason reflects into sereneseasons.api.season.{SeasonHelper, ISeasonState, Season$SubSeason}")
    void getWinterSubSeasonUsesReflection() throws IOException {
        String source = readSource(COMPAT_SOURCE);
        assertTrue(Pattern.compile("sereneseasons\\.api\\.season\\.SeasonHelper").matcher(source).find(),
                "Reflection must look up 'sereneseasons.api.season.SeasonHelper'.");
        assertTrue(Pattern.compile("sereneseasons\\.api\\.season\\.ISeasonState").matcher(source).find(),
                "Reflection must look up 'sereneseasons.api.season.ISeasonState'.");
        assertTrue(Pattern.compile("sereneseasons\\.api\\.season\\.Season\\$SubSeason").matcher(source).find(),
                "Reflection must look up 'sereneseasons.api.season.Season$SubSeason'.");
    }

    @Test
    @DisplayName("getWinterSubSeason maps EARLY_WINTER / MID_WINTER / LATE_WINTER enum names")
    void getWinterSubSeasonMapsAllThreeWinterNames() throws IOException {
        String source = readSource(COMPAT_SOURCE);
        assertTrue(Pattern.compile("\"EARLY_WINTER\"").matcher(source).find(),
                "getWinterSubSeason must match the SS sub-season name literal 'EARLY_WINTER'.");
        assertTrue(Pattern.compile("\"MID_WINTER\"").matcher(source).find(),
                "getWinterSubSeason must match 'MID_WINTER'.");
        assertTrue(Pattern.compile("\"LATE_WINTER\"").matcher(source).find(),
                "getWinterSubSeason must match 'LATE_WINTER'.");
    }

    @Test
    @DisplayName("BathtubBlock.entityInside calls ShowerCoreCompat.getWinterSubSeason(level)")
    void bathtubCallsGetWinterSubSeason() throws IOException {
        String body = extractEntityInsideBody(readSource(BATHTUB_SOURCE));
        Pattern call = Pattern.compile(
                "ShowerCoreCompat\\s*\\.\\s*getWinterSubSeason\\s*\\(\\s*level\\s*\\)");
        assertTrue(call.matcher(body).find(),
                "BathtubBlock.entityInside must call 'ShowerCoreCompat.getWinterSubSeason(level)'. "
                        + "Without this call the winter buffs are never applied — Bug T regresses.");
    }

    @Test
    @DisplayName("Winter buff is LivingEntity-only, server-side, and cadence-gated (every 100 ticks)")
    void winterBuffIsServerSideAndGated() throws IOException {
        String body = extractEntityInsideBody(readSource(BATHTUB_SOURCE));
        int callIdx = body.indexOf("ShowerCoreCompat.getWinterSubSeason");
        assertNotEquals(-1, callIdx, "getWinterSubSeason call not found.");

        int windowStart = Math.max(0, callIdx - 400);
        String guardWindow = body.substring(windowStart, callIdx);

        assertTrue(Pattern.compile("instanceof\\s+LivingEntity").matcher(guardWindow).find(),
                "Winter-buff path must be guarded by 'instanceof LivingEntity' so items and arrows don't "
                        + "receive MobEffects.");
        assertTrue(Pattern.compile("tickCount\\s*%\\s*100\\s*==\\s*0").matcher(guardWindow).find(),
                "Winter-buff path must be gated by 'entity.tickCount % 100 == 0' (once every 5s). Refreshing "
                        + "a 200-tick effect every tick causes effect-icon flicker and wasted traffic.");
    }

    @Test
    @DisplayName("EARLY and LATE sub-seasons apply Resistance I (duration 200, amplifier 0)")
    void earlyAndLateApplyResistance1() throws IOException {
        String body = extractEntityInsideBody(readSource(BATHTUB_SOURCE));
        // The switch body includes `case EARLY:` and `case LATE:` falling into a single
        // `addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 200, 0, ...))` call.
        Pattern earlyCase = Pattern.compile("case\\s+EARLY\\s*:");
        Pattern lateCase = Pattern.compile("case\\s+LATE\\s*:");
        assertTrue(earlyCase.matcher(body).find(), "switch must contain 'case EARLY:'.");
        assertTrue(lateCase.matcher(body).find(), "switch must contain 'case LATE:'.");

        Pattern resistance1 = Pattern.compile(
                "MobEffects\\s*\\.\\s*DAMAGE_RESISTANCE\\s*,\\s*200\\s*,\\s*0");
        assertTrue(resistance1.matcher(body).find(),
                "Must apply 'new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 200, 0, ...)' (Resistance I "
                        + "for 10s) for EARLY/LATE winter. If duration != 200 or amplifier != 0, the buff "
                        + "strength or refresh cadence is wrong.");
    }

    @Test
    @DisplayName("MID sub-season applies Resistance II AND Regeneration I")
    void midAppliesResistance2AndRegen1() throws IOException {
        String body = extractEntityInsideBody(readSource(BATHTUB_SOURCE));

        Pattern midCase = Pattern.compile("case\\s+MID\\s*:");
        assertTrue(midCase.matcher(body).find(), "switch must contain 'case MID:'.");

        Pattern resistance2 = Pattern.compile(
                "MobEffects\\s*\\.\\s*DAMAGE_RESISTANCE\\s*,\\s*200\\s*,\\s*1");
        assertTrue(resistance2.matcher(body).find(),
                "MID winter must apply Resistance II (amplifier 1) via 'MobEffectInstance("
                        + "MobEffects.DAMAGE_RESISTANCE, 200, 1, ...)'.");

        Pattern regen1 = Pattern.compile(
                "MobEffects\\s*\\.\\s*REGENERATION\\s*,\\s*200\\s*,\\s*0");
        assertTrue(regen1.matcher(body).find(),
                "MID winter must also apply Regeneration I via 'MobEffectInstance(MobEffects.REGENERATION, "
                        + "200, 0, ...)'. Missing this makes MID winter identical to EARLY/LATE — a silent "
                        + "regression that's easy to introduce by dropping one statement during a refactor.");
    }

    // ---- Helpers ----------------------------------------------------------

    private static String readSource(Path path) throws IOException {
        if (!Files.isRegularFile(path)) {
            fail(path.getFileName() + " not found at " + path.toAbsolutePath());
        }
        return Files.readString(path);
    }

    private static String extractEntityInsideBody(String source) {
        Pattern sig = Pattern.compile("\\bvoid\\s+entityInside\\s*\\(");
        return extractMethodBody(source, sig, "BathtubBlock.entityInside");
    }

    private static String extractMethodBody(String source, Pattern sig, String humanName) {
        Matcher m = sig.matcher(source);
        if (!m.find()) {
            fail("Could not locate signature for '" + humanName + "'");
        }
        int parenOpen = source.indexOf('(', m.start());
        int pDepth = 1;
        int j = parenOpen + 1;
        while (j < source.length() && pDepth > 0) {
            char c = source.charAt(j);
            if (c == '(') pDepth++;
            else if (c == ')') pDepth--;
            j++;
        }
        int openBrace = source.indexOf('{', j);
        assertNotEquals(-1, openBrace, humanName + " has no opening body brace");

        int depth = 1;
        int i = openBrace + 1;
        while (i < source.length() && depth > 0) {
            char c = source.charAt(i);
            if (c == '{') depth++;
            else if (c == '}') depth--;
            i++;
        }
        return source.substring(openBrace + 1, i - 1);
    }
}
