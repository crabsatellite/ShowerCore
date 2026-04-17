package mod.crabmod.showercore.compat;

import mod.crabmod.showercore.testutil.TestSourceUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Source-level regression test for {@code ShowerCoreCompat.java}.
 *
 * <p>ShowerCoreCompat is the single funnel through which ShowerCore touches
 * external mods (hotBath, Twilight Forest, Alex's Caves). Two invariants must
 * hold at all times:
 *
 * <ol>
 *   <li><b>Every public entry point gates on {@code Config.isModIntegrationsEnabled()}</b>
 *       at the top of its body. A missing gate means a user who disabled mod
 *       integrations in config still takes the reflection path and its cost on
 *       every tick.</li>
 *   <li><b>Every external class reference is wrapped in try-catch.</b>
 *       ShowerCore has no compile-time dependency on these mods — they may be
 *       absent. An un-guarded call leaks a {@link NoClassDefFoundError} into
 *       the caller and crashes the server.</li>
 * </ol>
 */
class ShowerCoreCompatModIntegrationGateTest {

    private static final Path COMPAT_SOURCE = Paths.get(
            "src", "main", "java", "mod", "crabmod", "showercore", "compat", "ShowerCoreCompat.java");

    private static final Pattern APPLY_DIRTINESS_SIG = Pattern.compile(
            "public\\s+static\\s+void\\s+applyDirtinessCleaning\\s*\\(");
    private static final Pattern IS_TF_ICE_MOB_SIG = Pattern.compile(
            "public\\s+static\\s+boolean\\s+isTwilightForestIceMob\\s*\\(");
    private static final Pattern IS_GUMMY_BEAR_SIG = Pattern.compile(
            "public\\s+static\\s+boolean\\s+isGummyBear\\s*\\(");

    private static String source;

    @BeforeAll
    static void loadSource() throws IOException {
        source = TestSourceUtils.readSource(COMPAT_SOURCE);
    }

    @Test
    @DisplayName("applyDirtinessCleaning gates on Config.isModIntegrationsEnabled()")
    void applyDirtinessGatesOnModIntegrations() {
        String body = TestSourceUtils.extractMethodBody(
                source, APPLY_DIRTINESS_SIG, "ShowerCoreCompat.applyDirtinessCleaning");
        assertModIntegrationGate(body, "applyDirtinessCleaning");
    }

    @Test
    @DisplayName("isTwilightForestIceMob gates on Config.isModIntegrationsEnabled()")
    void twilightForestGatesOnModIntegrations() {
        String body = TestSourceUtils.extractMethodBody(
                source, IS_TF_ICE_MOB_SIG, "ShowerCoreCompat.isTwilightForestIceMob");
        assertModIntegrationGate(body, "isTwilightForestIceMob");
    }

    @Test
    @DisplayName("isGummyBear gates on Config.isModIntegrationsEnabled()")
    void gummyBearGatesOnModIntegrations() {
        String body = TestSourceUtils.extractMethodBody(
                source, IS_GUMMY_BEAR_SIG, "ShowerCoreCompat.isGummyBear");
        assertModIntegrationGate(body, "isGummyBear");
    }

    @Test
    @DisplayName("Every external-mod call is wrapped in try-catch (>=3 guards present)")
    void everyExternalCallIsTryCatchGuarded() {
        Pattern catchClause = Pattern.compile(
                "catch\\s*\\(\\s*(?:Exception|Throwable|NoClassDefFoundError)\\b");
        Matcher m = catchClause.matcher(source);
        int count = 0;
        while (m.find()) count++;
        assertTrue(count >= 3,
                "ShowerCoreCompat must have at least 3 try-catch guards (one per external-mod entry: "
                        + "dirtiness, Twilight Forest, Alex's Caves). Found " + count + ". An un-guarded "
                        + "reflection call into an absent mod throws NoClassDefFoundError into the caller "
                        + "and crashes the server tick.");
    }

    @Test
    @DisplayName("isAlexsCavesLoaded uses ModList.isLoaded(\"alexscaves\") instead of a try-catch heuristic")
    void alexsCavesUsesModListIsLoaded() {
        assertTrue(Pattern.compile("ModList\\s*\\.\\s*get\\s*\\(\\s*\\)\\s*\\.\\s*isLoaded\\s*\\(\\s*\"alexscaves\"\\s*\\)")
                        .matcher(source).find(),
                "ShowerCoreCompat must use 'ModList.get().isLoaded(\"alexscaves\")' to detect Alex's Caves. "
                        + "Switching to try/Class.forName without ModList would run the class lookup on "
                        + "every call — ModList.isLoaded is the cheap O(1) authoritative check.");
    }

    @Test
    @DisplayName("GummyBear reflection targets the correct Alex's Caves class FQN")
    void gummyBearReflectionFqn() {
        assertTrue(Pattern.compile(
                        "\"com\\.github\\.alexmodguy\\.alexscaves\\.server\\.entity\\.living\\.GummyBearEntity\"")
                        .matcher(source).find(),
                "ShowerCoreCompat must reflect into Alex's Caves with the FQN "
                        + "'com.github.alexmodguy.alexscaves.server.entity.living.GummyBearEntity'. Typos "
                        + "silently disable the integration — Class.forName returns null and isGummyBear "
                        + "always returns false.");
    }

    private static void assertModIntegrationGate(String body, String methodName) {
        assertTrue(Pattern.compile("Config\\s*\\.\\s*isModIntegrationsEnabled\\s*\\(\\s*\\)")
                        .matcher(body).find(),
                "ShowerCoreCompat." + methodName + " must check 'Config.isModIntegrationsEnabled()' at the "
                        + "top of its body and early-return when false. Missing → users who disabled mod "
                        + "integrations in config still pay the reflection / external-API cost every tick.");
    }
}
