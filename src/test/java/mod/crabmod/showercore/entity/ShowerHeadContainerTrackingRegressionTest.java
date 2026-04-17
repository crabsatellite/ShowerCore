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
 * cross-thread player-tracking set (1.20 Forge).
 *
 * <p>{@code PLAYERS_UNDER_ACTIVE_SHOWER} is a static {@code Set<UUID>} written
 * from the server tick thread and read/removed from the Forge event thread.
 * It MUST be concurrent-safe or those threads race.
 *
 * <p>Invariants guarded:
 * <ul>
 *   <li>The set is initialised as {@code ConcurrentHashMap.newKeySet()}.</li>
 *   <li>{@code cleanupPlayer(UUID)} exists, is public static, and removes.</li>
 *   <li>{@code isPlayerUnderActiveShower(UUID)} exists and reads.</li>
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
                "PLAYERS_UNDER_ACTIVE_SHOWER must be 'java.util.concurrent.ConcurrentHashMap.newKeySet()' "
                        + "for thread-safe access. HashSet or Collections.synchronizedSet races between "
                        + "the tick thread (writes) and the event thread (logout cleanup removes).");
    }

    @Test
    @DisplayName("cleanupPlayer is public static and removes from the tracking set")
    void cleanupPlayerRemovesFromTrackingSet() {
        assertTrue(CLEANUP_SIG.matcher(source).find(),
                "ShowerHeadContainerEntity must declare 'public static void cleanupPlayer(UUID uuid)'.");
        assertTrue(Pattern.compile("PLAYERS_UNDER_ACTIVE_SHOWER\\s*\\.\\s*remove\\s*\\(")
                        .matcher(cleanupBody).find(),
                "cleanupPlayer must call 'PLAYERS_UNDER_ACTIVE_SHOWER.remove(uuid)'.");
    }

    @Test
    @DisplayName("isPlayerUnderActiveShower is public static and reads the tracking set")
    void isPlayerUnderActiveShowerReadsSet() {
        assertTrue(IS_PLAYER_SIG.matcher(source).find(),
                "ShowerHeadContainerEntity must declare 'public static boolean "
                        + "isPlayerUnderActiveShower(UUID uuid)'.");
        assertTrue(Pattern.compile("PLAYERS_UNDER_ACTIVE_SHOWER\\s*\\.\\s*contains\\s*\\(")
                        .matcher(isPlayerBody).find(),
                "isPlayerUnderActiveShower must call 'PLAYERS_UNDER_ACTIVE_SHOWER.contains(uuid)'.");
    }
}
