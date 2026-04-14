package mod.crabmod.showercore.block;

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
 * Source-level regression test for Bug V — mining the HEAD of a two-part
 * bathtub dropped TWO bathtub items, and mining HEAD without the correct tool
 * still dropped one at the FOOT.
 *
 * <p>Root cause: when one half is destroyed, vanilla {@code Block.updateShape}
 * on the other half returns AIR, which triggers {@code Block.updateOrDestroy}
 * to call {@code level.destroyBlock(pos, drop=true, player=null)}. That both
 * duplicates the drop AND bypasses the correct-tool check because {@code
 * player} is null.
 *
 * <p>Fix: {@code BathtubBlock#playerWillDestroy} pre-clears the neighbor half
 * with {@code level.setBlock(<neighbor>, Blocks.AIR.defaultBlockState(), 35)}
 * where flag 35 = UPDATE_SUPPRESS_DROPS | UPDATE_CLIENTS | UPDATE_NEIGHBORS.
 * This runs BEFORE the vanilla cascade, so the cascade sees the neighbor
 * already gone and never drops.
 *
 * <p>Minecraft classes are not on the unit-test classpath, so this test
 * enforces the fix as a textual invariant on the source file.
 */
class BathtubDoubleDropRegressionTest {

    private static final Path BATHTUB_SOURCE = Paths.get(
            "src", "main", "java", "mod", "crabmod", "showercore", "block", "BathtubBlock.java");

    private static final Pattern PLAYER_WILL_DESTROY_SIG = Pattern.compile(
            "public\\s+BlockState\\s+playerWillDestroy\\s*\\(");

    private static String source;
    private static String body;

    @BeforeAll
    static void loadSource() throws IOException {
        source = TestSourceUtils.readSource(BATHTUB_SOURCE);
        body = TestSourceUtils.extractMethodBody(
                source, PLAYER_WILL_DESTROY_SIG, "BathtubBlock.playerWillDestroy");
    }

    @Test
    @DisplayName("BathtubBlock.playerWillDestroy exists with the correct signature")
    void playerWillDestroyExists() {
        Pattern fullSig = Pattern.compile(
                "public\\s+BlockState\\s+playerWillDestroy\\s*\\(\\s*Level\\s+\\w+\\s*,"
                        + "\\s*BlockPos\\s+\\w+\\s*,\\s*BlockState\\s+\\w+\\s*,\\s*Player\\s+\\w+\\s*\\)");
        assertTrue(fullSig.matcher(source).find(),
                "BathtubBlock.playerWillDestroy(Level, BlockPos, BlockState, Player) must exist. "
                        + "If this overload is missing, Bug V (double-drop when mining HEAD) will regress "
                        + "because the fix hooks into this method.");
    }

    @Test
    @DisplayName("playerWillDestroy clears the neighbor half with setBlock(AIR, flag=35)")
    void playerWillDestroyClearsNeighborWithFlag35() {
        Pattern call = Pattern.compile(
                "level\\s*\\.\\s*setBlock\\s*\\(\\s*\\w+\\s*,"
                        + "\\s*Blocks\\s*\\.\\s*AIR\\s*\\.\\s*defaultBlockState\\s*\\(\\s*\\)\\s*,"
                        + "\\s*35\\s*\\)");
        assertTrue(call.matcher(body).find(),
                "playerWillDestroy must call 'level.setBlock(<neighborPos>, "
                        + "Blocks.AIR.defaultBlockState(), 35)'. Flag 35 = UPDATE_SUPPRESS_DROPS(32) | "
                        + "UPDATE_CLIENTS(2) | UPDATE_NEIGHBORS(1). If the flag is changed (e.g. to 3 "
                        + "without the suppress-drops bit), the neighbor half will drop a second bathtub "
                        + "when the vanilla cascade fires — Bug V regression.");
    }

    @Test
    @DisplayName("playerWillDestroy computes the neighbor via pos.relative(getNeighbourDirection(part, FACING))")
    void playerWillDestroyUsesGetNeighbourDirection() {
        Pattern np = Pattern.compile(
                "BlockPos\\s+\\w+\\s*=\\s*pos\\s*\\.\\s*relative\\s*\\(\\s*"
                        + "getNeighbourDirection\\s*\\(\\s*\\w+\\s*,");
        assertTrue(np.matcher(body).find(),
                "playerWillDestroy must compute the neighbor via "
                        + "'BlockPos <x> = pos.relative(getNeighbourDirection(<part>, ...))'. The BedBlock "
                        + "helper returns FOOT→HEAD when called on a HEAD and vice versa, so this "
                        + "correctly picks the OTHER half. Using the wrong direction (e.g. facing) would "
                        + "leave the real neighbor un-cleared and Bug V would regress.");
    }

    @Test
    @DisplayName("Neighbor AIR-set is guarded by neighbor.is(this) AND getValue(PART) != <bedpart>")
    void playerWillDestroyGuardsNeighborState() {
        Pattern guard = Pattern.compile(
                "\\w+\\s*\\.\\s*is\\s*\\(\\s*this\\s*\\)\\s*&&\\s*"
                        + "\\w+\\s*\\.\\s*getValue\\s*\\(\\s*PART\\s*\\)\\s*!=\\s*\\w+");
        assertTrue(guard.matcher(body).find(),
                "The neighbor setBlock(AIR) must be guarded by '<neighborState>.is(this) && "
                        + "<neighborState>.getValue(PART) != <bedpart>'. Without the 'is(this)' guard, "
                        + "an unrelated block next to a bathtub HEAD could be destroyed. Without the "
                        + "'PART != <bedpart>' guard, we'd try to clear a same-half block (impossible, "
                        + "but defensive).");
    }

    @Test
    @DisplayName("playerWillDestroy fires levelEvent 2001 for neighbor break particles/sound")
    void playerWillDestroyFiresBreakLevelEvent() {
        Pattern evt = Pattern.compile(
                "level\\s*\\.\\s*levelEvent\\s*\\(\\s*player\\s*,\\s*2001\\s*,\\s*\\w+\\s*,\\s*"
                        + "Block\\s*\\.\\s*getId\\s*\\(\\s*\\w+\\s*\\)\\s*\\)");
        assertTrue(evt.matcher(body).find(),
                "Must call 'level.levelEvent(player, 2001, <neighborPos>, Block.getId(<neighborState>))' "
                        + "to replay the break particles and sound for the silently-cleared neighbor half. "
                        + "Without this call the HEAD-break looks buggy — the FOOT simply disappears with "
                        + "no feedback.");
    }

    @Test
    @DisplayName("playerWillDestroy body is wrapped in !level.isClientSide")
    void playerWillDestroyIsServerSide() {
        Pattern guard = Pattern.compile("if\\s*\\(\\s*!\\s*level\\s*\\.\\s*isClientSide\\s*\\)");
        assertTrue(guard.matcher(body).find(),
                "playerWillDestroy must gate its neighbor-clearing logic behind '!level.isClientSide'. "
                        + "Clearing blocks on the client would desync the world and could cause ghost "
                        + "blocks or double-remove on rejoin.");
    }

    @Test
    @DisplayName("playerWillDestroy still returns super.playerWillDestroy(...) for vanilla drop path")
    void playerWillDestroyCallsSuper() {
        Pattern sup = Pattern.compile(
                "return\\s+super\\s*\\.\\s*playerWillDestroy\\s*\\(\\s*\\w+\\s*,\\s*\\w+\\s*,\\s*\\w+\\s*,\\s*\\w+\\s*\\)");
        assertTrue(sup.matcher(body).find(),
                "playerWillDestroy must end with 'return super.playerWillDestroy(...)' (4 args) so the "
                        + "vanilla drop pipeline runs for the MINED half (respecting the player's tool and "
                        + "correctly invoking getDrops). Removing the super call would drop nothing at all.");
    }
}
