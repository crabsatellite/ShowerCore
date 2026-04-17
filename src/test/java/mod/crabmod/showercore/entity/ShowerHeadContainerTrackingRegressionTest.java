package mod.crabmod.showercore.entity;

import mod.crabmod.showercore.testutil.TestSourceUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Source-level regression test for {@code ShowerHeadContainerEntity.java}'s
 * cross-thread player-tracking set.
 *
 * <p>{@code PLAYERS_UNDER_ACTIVE_SHOWER} is a static {@code Set<UUID>} written
 * from the server tick thread (shower-head {@link
 * mod.crabmod.showercore.entity.ShowerHeadContainerEntity#tick}) and read/
 * removed from the Forge event thread (player logout / clone in
 * {@code ServerEvent}). It MUST be concurrent-safe or those threads race.
 *
 * <p>Invariants guarded:
 * <ul>
 *   <li>The set is initialised as {@code ConcurrentHashMap.newKeySet()} —
 *       a plain {@link java.util.HashSet} or {@code Collections.synchronizedSet}
 *       either throws {@link java.util.ConcurrentModificationException} during
 *       iteration or silently drops writes under contention.</li>
 *   <li>{@code cleanupPlayer(UUID)} exists, is public static, and removes from
 *       the set — this is the entry point called by {@code ServerEvent} and
 *       the tests guarding it depend on its signature being stable.</li>
 *   <li>{@code isPlayerUnderActiveShower(UUID)} exists and reads the set —
 *       temperature-compat mods call this externally to detect ongoing
 *       shower sessions.</li>
 * </ul>
 */
class ShowerHeadContainerTrackingRegressionTest {

    private static final Path CONTAINER_SOURCE = Paths.get(
            "src", "main", "java", "mod", "crabmod", "showercore", "entity",
            "ShowerHeadContainerEntity.java");

    private static final Pattern CLEANUP_SIG = Pattern.compile(
            "public\\s+static\\s+void\\s+cleanupPlayer\\s*\\(\\s*UUID\\s+\\w+\\s*\\)");
    private static final Pattern IS_PLAYER_SIG = Pattern.compile(
            "public\\s+static\\s+boolean\\s+isPlayerUnderActiveShower\\s*\\(\\s*UUID\\s+\\w+\\s*\\)");

    private static String source;
    private static String cleanupBody;
    private static String isPlayerBody;

    @BeforeAll
    static void loadSource() throws IOException {
        source = TestSourceUtils.readSource(CONTAINER_SOURCE);
        cleanupBody = TestSourceUtils.extractMethodBody(
                source, CLEANUP_SIG, "ShowerHeadContainerEntity.cleanupPlayer");
        isPlayerBody = TestSourceUtils.extractMethodBody(
                source, IS_PLAYER_SIG, "ShowerHeadContainerEntity.isPlayerUnderActiveShower");
    }

    @Test
    @DisplayName("PLAYERS_UNDER_ACTIVE_SHOWER is initialised as ConcurrentHashMap.newKeySet() (thread-safe)")
    void trackingSetIsConcurrentHashMapKeySet() {
        assertTrue(Pattern.compile(
                        "PLAYERS_UNDER_ACTIVE_SHOWER\\s*=\\s*java\\s*\\.\\s*util\\s*\\.\\s*concurrent\\s*\\."
                                + "\\s*ConcurrentHashMap\\s*\\.\\s*newKeySet\\s*\\(\\s*\\)")
                        .matcher(source).find(),
                "PLAYERS_UNDER_ACTIVE_SHOWER must be initialised as "
                        + "'java.util.concurrent.ConcurrentHashMap.newKeySet()'. Switching to HashSet or "
                        + "Collections.synchronizedSet introduces a race between the server tick thread "
                        + "(which adds entries during entity tick) and the Forge event thread (which "
                        + "removes entries on logout/clone). Under load this silently drops writes, leaks "
                        + "UUIDs, or throws ConcurrentModificationException during iteration.");
    }

    @Test
    @DisplayName("cleanupPlayer is public static and removes from the tracking set")
    void cleanupPlayerRemovesFromTrackingSet() {
        assertTrue(CLEANUP_SIG.matcher(source).find(),
                "ShowerHeadContainerEntity must declare 'public static void cleanupPlayer(UUID uuid)'. "
                        + "ServerEvent calls this verbatim — renaming or reordering breaks cleanup.");
        assertTrue(Pattern.compile("PLAYERS_UNDER_ACTIVE_SHOWER\\s*\\.\\s*remove\\s*\\(")
                        .matcher(cleanupBody).find(),
                "cleanupPlayer must call 'PLAYERS_UNDER_ACTIVE_SHOWER.remove(uuid)'. An empty or no-op "
                        + "body compiles but silently never removes the UUID, so the set leaks.");
    }

    @Test
    @DisplayName("isPlayerUnderActiveShower is public static and reads the tracking set")
    void isPlayerUnderActiveShowerReadsSet() {
        assertTrue(IS_PLAYER_SIG.matcher(source).find(),
                "ShowerHeadContainerEntity must declare 'public static boolean "
                        + "isPlayerUnderActiveShower(UUID uuid)'. Temperature-compat mods (Cold Sweat, "
                        + "TAN, LSO via hotBath's API) call this externally; renaming it silently disables "
                        + "temperature mod integration for active showers.");
        assertTrue(Pattern.compile("PLAYERS_UNDER_ACTIVE_SHOWER\\s*\\.\\s*contains\\s*\\(")
                        .matcher(isPlayerBody).find(),
                "isPlayerUnderActiveShower must call 'PLAYERS_UNDER_ACTIVE_SHOWER.contains(uuid)'. A "
                        + "hard-coded 'return false' compiles but silently disables compat.");
    }
}
