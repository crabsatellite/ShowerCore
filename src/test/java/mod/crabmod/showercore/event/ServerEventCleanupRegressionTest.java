package mod.crabmod.showercore.event;

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
 * Source-level regression test for {@code ServerEvent.java} (1.20 Forge).
 *
 * <p>ServerEvent is the single handler class that cleans up
 * {@code PLAYERS_UNDER_ACTIVE_SHOWER} (a static JVM-lifetime set of UUIDs) when
 * a player logs out or dies. Without this cleanup the set grows unboundedly —
 * a classic UUID-leak memory regression.
 *
 * <p>1.20 differs from 1.21: the class is NOT annotated with
 * {@code @Mod.EventBusSubscriber}; instead the listener is explicitly registered
 * via {@code MinecraftForge.EVENT_BUS.register(ServerEvent.class)} from the
 * {@code ShowerCore} constructor (guarded by a separate regression test in the
 * mod-registration test). Either approach works; they MUST NOT both be used
 * (double-firing handlers).
 *
 * <p>Invariants guarded:
 * <ul>
 *   <li>{@code onPlayerLogout} calls
 *       {@code ShowerHeadContainerEntity.cleanupPlayer(event.getEntity().getUUID())}.</li>
 *   <li>{@code onPlayerClone} calls cleanup ONLY when {@code event.isWasDeath()}
 *       — otherwise dimension-travel would wipe state every portal.</li>
 *   <li>Both handlers are {@code @SubscribeEvent} annotated.</li>
 * </ul>
 */
class ServerEventCleanupRegressionTest {

    private static final Path SERVER_EVENT_SOURCE = Paths.get(
            "src", "main", "java", "mod", "crabmod", "showercore", "event", "ServerEvent.java");

    private static final Pattern ON_CLONE_SIG = Pattern.compile(
            "void\\s+onPlayerClone\\s*\\(\\s*PlayerEvent\\s*\\.\\s*Clone\\s+\\w+\\s*\\)");

    private static String source;
    private static String onCloneBody;

    @BeforeAll
    static void loadSource() throws IOException {
        source = TestSourceUtils.readSource(SERVER_EVENT_SOURCE);
        onCloneBody = TestSourceUtils.extractMethodBody(source, ON_CLONE_SIG, "ServerEvent.onPlayerClone");
    }

    @Test
    @DisplayName("onPlayerLogout cleans up tracking set with event.getEntity().getUUID()")
    void onPlayerLogoutCallsCleanup() {
        assertTrue(Pattern.compile(
                        "void\\s+onPlayerLogout\\s*\\(\\s*PlayerEvent\\s*\\.\\s*PlayerLoggedOutEvent\\s+\\w+\\s*\\)")
                        .matcher(source).find(),
                "ServerEvent must declare 'void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event)'.");
        assertTrue(Pattern.compile(
                        "ShowerHeadContainerEntity\\s*\\.\\s*cleanupPlayer\\s*\\(\\s*"
                                + "\\w+\\s*\\.\\s*getEntity\\s*\\(\\s*\\)\\s*\\.\\s*getUUID\\s*\\(\\s*\\)\\s*\\)")
                        .matcher(source).find(),
                "onPlayerLogout must call 'ShowerHeadContainerEntity.cleanupPlayer(event.getEntity()."
                        + "getUUID())'. Passing the Player object directly would not match the stored UUID "
                        + "and leak.");
    }

    @Test
    @DisplayName("onPlayerClone cleanup is gated by event.isWasDeath()")
    void onPlayerCloneGuardsOnWasDeath() {
        assertTrue(Pattern.compile("\\w+\\s*\\.\\s*isWasDeath\\s*\\(\\s*\\)").matcher(onCloneBody).find(),
                "onPlayerClone must gate the cleanup on 'event.isWasDeath()'. Without this guard the "
                        + "PlayerEvent.Clone that fires on dimension travel (non-death) would also wipe "
                        + "the player's active-shower tracking every Nether/End portal.");
        assertTrue(Pattern.compile("ShowerHeadContainerEntity\\s*\\.\\s*cleanupPlayer\\s*\\(")
                        .matcher(onCloneBody).find(),
                "onPlayerClone must still call cleanupPlayer inside the isWasDeath guard.");
    }

    @Test
    @DisplayName("Both handlers are @SubscribeEvent annotated (>=2 annotations present)")
    void bothHandlersAreSubscribeEventAnnotated() {
        Matcher m = Pattern.compile("@SubscribeEvent\\b").matcher(source);
        int count = 0;
        while (m.find()) count++;
        assertTrue(count >= 2,
                "ServerEvent must have at least 2 @SubscribeEvent annotations (onPlayerLogout and "
                        + "onPlayerClone). Found " + count + ".");
    }
}
