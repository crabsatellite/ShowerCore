package mod.crabmod.showercore.block;

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
 * with {@code level.setBlock(neighborPos, Blocks.AIR.defaultBlockState(), 35)}
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

    @Test
    @DisplayName("BathtubBlock.playerWillDestroy exists with the correct signature")
    void playerWillDestroyExists() throws IOException {
        String source = readSource(BATHTUB_SOURCE);
        Pattern sig = Pattern.compile(
                "public\\s+BlockState\\s+playerWillDestroy\\s*\\(\\s*Level\\s+\\w+\\s*,"
                        + "\\s*BlockPos\\s+\\w+\\s*,\\s*BlockState\\s+\\w+\\s*,\\s*Player\\s+\\w+\\s*\\)");
        assertTrue(sig.matcher(source).find(),
                "BathtubBlock.playerWillDestroy(Level, BlockPos, BlockState, Player) must exist. "
                        + "If this overload is missing, Bug V (double-drop when mining HEAD) will regress "
                        + "because the fix hooks into this method. Regex: " + sig.pattern());
    }

    @Test
    @DisplayName("playerWillDestroy clears the neighbor half with setBlock(AIR, flag=35)")
    void playerWillDestroyClearsNeighborWithFlag35() throws IOException {
        String body = extractPlayerWillDestroyBody(readSource(BATHTUB_SOURCE));
        Pattern call = Pattern.compile(
                "level\\s*\\.\\s*setBlock\\s*\\(\\s*neighborPos\\s*,"
                        + "\\s*Blocks\\s*\\.\\s*AIR\\s*\\.\\s*defaultBlockState\\s*\\(\\s*\\)\\s*,"
                        + "\\s*35\\s*\\)");
        assertTrue(call.matcher(body).find(),
                "playerWillDestroy must call 'level.setBlock(neighborPos, Blocks.AIR.defaultBlockState(), 35)'. "
                        + "Flag 35 = UPDATE_SUPPRESS_DROPS(32) | UPDATE_CLIENTS(2) | UPDATE_NEIGHBORS(1). "
                        + "If the flag is changed (e.g. to 3 without the suppress-drops bit), the neighbor "
                        + "half will drop a second bathtub when the vanilla cascade fires — exactly the "
                        + "Bug V regression this test locks against. Regex: " + call.pattern());
    }

    @Test
    @DisplayName("playerWillDestroy computes neighborPos via getNeighbourDirection(bedpart, FACING)")
    void playerWillDestroyUsesGetNeighbourDirection() throws IOException {
        String body = extractPlayerWillDestroyBody(readSource(BATHTUB_SOURCE));
        Pattern np = Pattern.compile(
                "BlockPos\\s+neighborPos\\s*=\\s*pos\\s*\\.\\s*relative\\s*\\(\\s*"
                        + "getNeighbourDirection\\s*\\(\\s*bedpart\\s*,");
        assertTrue(np.matcher(body).find(),
                "playerWillDestroy must compute the neighbor via pos.relative(getNeighbourDirection(bedpart, ...)). "
                        + "The BedBlock helper returns FOOT→HEAD when called on a HEAD and vice versa, so this "
                        + "correctly picks the OTHER half. Using the wrong direction (e.g. facing) would leave "
                        + "the real neighbor un-cleared and Bug V would regress.");
    }

    @Test
    @DisplayName("Neighbor AIR-set is guarded by neighborState.is(this) AND PART != bedpart")
    void playerWillDestroyGuardsNeighborState() throws IOException {
        String body = extractPlayerWillDestroyBody(readSource(BATHTUB_SOURCE));
        Pattern guard = Pattern.compile(
                "neighborState\\s*\\.\\s*is\\s*\\(\\s*this\\s*\\)\\s*&&\\s*"
                        + "neighborState\\s*\\.\\s*getValue\\s*\\(\\s*PART\\s*\\)\\s*!=\\s*bedpart");
        assertTrue(guard.matcher(body).find(),
                "The neighbor setBlock(AIR) must be guarded by 'neighborState.is(this) && "
                        + "neighborState.getValue(PART) != bedpart'. Without the 'is(this)' guard, "
                        + "an unrelated block next to a bathtub HEAD could be destroyed. Without the "
                        + "'PART != bedpart' guard, we'd try to clear a same-half block (impossible, "
                        + "but defensive).");
    }

    @Test
    @DisplayName("playerWillDestroy fires levelEvent 2001 for neighbor break particles/sound")
    void playerWillDestroyFiresBreakLevelEvent() throws IOException {
        String body = extractPlayerWillDestroyBody(readSource(BATHTUB_SOURCE));
        Pattern evt = Pattern.compile(
                "level\\s*\\.\\s*levelEvent\\s*\\(\\s*player\\s*,\\s*2001\\s*,\\s*neighborPos\\s*,\\s*"
                        + "Block\\s*\\.\\s*getId\\s*\\(\\s*neighborState\\s*\\)\\s*\\)");
        assertTrue(evt.matcher(body).find(),
                "playerWillDestroy must call 'level.levelEvent(player, 2001, neighborPos, "
                        + "Block.getId(neighborState))' to replay the break particles and sound for the "
                        + "silently-cleared neighbor half. Without this call the HEAD-break looks buggy — "
                        + "the FOOT simply disappears with no feedback.");
    }

    @Test
    @DisplayName("playerWillDestroy body is wrapped in !level.isClientSide")
    void playerWillDestroyIsServerSide() throws IOException {
        String body = extractPlayerWillDestroyBody(readSource(BATHTUB_SOURCE));
        Pattern guard = Pattern.compile("if\\s*\\(\\s*!\\s*level\\s*\\.\\s*isClientSide\\s*\\)");
        assertTrue(guard.matcher(body).find(),
                "playerWillDestroy must gate its neighbor-clearing logic behind '!level.isClientSide'. "
                        + "Clearing blocks on the client would desync the world and could cause ghost "
                        + "blocks or double-remove on rejoin.");
    }

    @Test
    @DisplayName("playerWillDestroy still returns super.playerWillDestroy(...) for vanilla drop path")
    void playerWillDestroyCallsSuper() throws IOException {
        String body = extractPlayerWillDestroyBody(readSource(BATHTUB_SOURCE));
        Pattern sup = Pattern.compile(
                "return\\s+super\\s*\\.\\s*playerWillDestroy\\s*\\(\\s*level\\s*,\\s*pos\\s*,\\s*state\\s*,\\s*player\\s*\\)");
        assertTrue(sup.matcher(body).find(),
                "playerWillDestroy must end with 'return super.playerWillDestroy(level, pos, state, player)' "
                        + "so the vanilla drop pipeline runs for the MINED half (respecting the player's tool "
                        + "and correctly invoking getDrops). Removing the super call would drop nothing at all.");
    }

    // ---- Helpers ----------------------------------------------------------

    private static String readSource(Path path) throws IOException {
        if (!Files.isRegularFile(path)) {
            fail(path.getFileName() + " not found at " + path.toAbsolutePath()
                    + " (cwd=" + Paths.get("").toAbsolutePath() + ")");
        }
        return Files.readString(path);
    }

    private static String extractPlayerWillDestroyBody(String source) {
        Pattern sig = Pattern.compile(
                "public\\s+BlockState\\s+playerWillDestroy\\s*\\(");
        Matcher m = sig.matcher(source);
        if (!m.find()) {
            fail("Could not locate 'public BlockState playerWillDestroy(' signature in BathtubBlock.java");
        }
        int sigIdx = m.start();

        int parenOpen = source.indexOf('(', sigIdx);
        int pDepth = 1;
        int j = parenOpen + 1;
        while (j < source.length() && pDepth > 0) {
            char c = source.charAt(j);
            if (c == '(') pDepth++;
            else if (c == ')') pDepth--;
            j++;
        }
        int openBrace = source.indexOf('{', j);
        assertNotEquals(-1, openBrace, "playerWillDestroy has no opening body brace");

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
