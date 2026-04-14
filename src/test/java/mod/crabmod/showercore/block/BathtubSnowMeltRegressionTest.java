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
 * Source-level regression test for Bug S — snow layers / snow blocks within 3
 * blocks of a hot bathtub must melt, so that a hot bath stays usable in a
 * Serene Seasons winter (where snow accumulates rapidly on top of every
 * exposed block, including the bathtub itself).
 *
 * <p>Fix: {@code BathtubBlock#getTicker} returns a server-tick lambda that,
 * every 80 ticks (4 seconds) and staggered by {@code pos.asLong()} to spread
 * load across chunks, scans the 7×7×7 neighbourhood around the bathtub and
 * removes any {@code Blocks.SNOW}, {@code Blocks.SNOW_BLOCK}, or
 * {@code Blocks.POWDER_SNOW}. The scan is gated to non-EMPTY, non-WATER
 * liquids; CUSTOM liquids additionally require
 * {@code CoreUtils.isCustomFluidHotAt(level, pos)} to be true so cold custom
 * fluids don't melt snow.
 */
class BathtubSnowMeltRegressionTest {

    private static final Path BATHTUB_SOURCE = Paths.get(
            "src", "main", "java", "mod", "crabmod", "showercore", "block", "BathtubBlock.java");

    @Test
    @DisplayName("BathtubBlock.getTicker is overridden and returns a ticker for BATHTUB_BLOCK_ENTITY only")
    void getTickerIsOverridden() throws IOException {
        String source = readSource(BATHTUB_SOURCE);
        Pattern sig = Pattern.compile(
                "public\\s+<T\\s+extends\\s+BlockEntity>\\s+BlockEntityTicker<T>\\s+getTicker\\s*\\(");
        assertTrue(sig.matcher(source).find(),
                "BathtubBlock must override 'public <T extends BlockEntity> BlockEntityTicker<T> getTicker(...)'. "
                        + "Without this override no serverTick ever fires and snow never melts (Bug S regresses).");

        String body = extractMethodBody(source, sig, "BathtubBlock.getTicker");
        Pattern clientGuard = Pattern.compile("level\\s*\\.\\s*isClientSide");
        assertTrue(clientGuard.matcher(body).find(),
                "getTicker must early-return for level.isClientSide to avoid running melt logic client-side.");
        Pattern typeGuard = Pattern.compile("BATHTUB_BLOCK_ENTITY");
        assertTrue(typeGuard.matcher(body).find(),
                "getTicker must guard on BATHTUB_BLOCK_ENTITY so we don't try to cast unrelated block "
                        + "entities to BathtubBlockEntity.");
    }

    @Test
    @DisplayName("serverTick runs every 80 ticks, staggered by pos.asLong()")
    void serverTickRunsEvery80TicksStaggered() throws IOException {
        String source = readSource(BATHTUB_SOURCE);
        Pattern sig = Pattern.compile(
                "private\\s+static\\s+void\\s+serverTick\\s*\\(\\s*Level\\s+\\w+\\s*,"
                        + "\\s*BlockPos\\s+\\w+\\s*,\\s*BlockState\\s+\\w+\\s*,"
                        + "\\s*BathtubBlockEntity\\s+\\w+\\s*\\)");
        assertTrue(sig.matcher(source).find(),
                "private static void serverTick(Level, BlockPos, BlockState, BathtubBlockEntity) must exist. "
                        + "Regex: " + sig.pattern());

        String body = extractMethodBody(source, sig, "BathtubBlock.serverTick");
        Pattern gate = Pattern.compile(
                "\\(\\s*level\\s*\\.\\s*getGameTime\\s*\\(\\s*\\)\\s*\\+\\s*pos\\s*\\.\\s*asLong\\s*\\(\\s*\\)\\s*\\)"
                        + "\\s*%\\s*80L?\\s*!=\\s*0");
        assertTrue(gate.matcher(body).find(),
                "serverTick must be gated by '(level.getGameTime() + pos.asLong()) % 80 != 0' so the melt "
                        + "scan runs once every 4 seconds per bathtub and is staggered across positions to "
                        + "avoid synchronized spikes. Changing the period from 80 makes the melt visibly "
                        + "sluggish (higher) or wastes ticks (lower).");
    }

    @Test
    @DisplayName("serverTick skips EMPTY and WATER liquids (cold baths don't melt snow)")
    void serverTickSkipsEmptyAndWater() throws IOException {
        String body = extractServerTickBody();
        Pattern liquidSkip = Pattern.compile(
                "liquid\\s*==\\s*LiquidType\\s*\\.\\s*EMPTY\\s*\\|\\|\\s*liquid\\s*==\\s*LiquidType\\s*\\.\\s*WATER");
        assertTrue(liquidSkip.matcher(body).find(),
                "serverTick must early-return when the liquid is EMPTY or WATER — an empty tub or one "
                        + "filled with cold water must not melt surrounding snow.");
    }

    @Test
    @DisplayName("CUSTOM liquids only melt snow when CoreUtils.isCustomFluidHotAt reports hot")
    void customLiquidMeltsOnlyWhenHot() throws IOException {
        String body = extractServerTickBody();
        Pattern custom = Pattern.compile(
                "liquid\\s*!=\\s*LiquidType\\s*\\.\\s*CUSTOM");
        assertTrue(custom.matcher(body).find(),
                "serverTick must test 'liquid != LiquidType.CUSTOM' as part of the hot check — non-custom "
                        + "hot liquids (HOT_WATER, HERBAL, HONEY, MILK, PEONY, ROSE) are always hot enough "
                        + "to melt, but CUSTOM needs a per-pos temperature check.");

        Pattern hotCheck = Pattern.compile(
                "CoreUtils\\s*\\.\\s*isCustomFluidHotAt\\s*\\(\\s*level\\s*,\\s*pos\\s*\\)");
        assertTrue(hotCheck.matcher(body).find(),
                "serverTick must delegate custom-fluid temperature to "
                        + "'CoreUtils.isCustomFluidHotAt(level, pos)'. Skipping this check would make "
                        + "cold custom fluids (e.g. ice baths from modpacks) erroneously melt surrounding "
                        + "snow — a correctness bug.");
    }

    @Test
    @DisplayName("serverTick scans radius 3 in all three axes and skips the origin")
    void serverTickScansRadius3() throws IOException {
        String body = extractServerTickBody();

        Pattern radius = Pattern.compile("radius\\s*=\\s*3");
        assertTrue(radius.matcher(body).find(),
                "serverTick must declare 'radius = 3' (scan a 7×7×7 cube). A smaller radius leaves snow "
                        + "on the bathtub itself; larger wastes CPU.");

        Pattern dx = Pattern.compile("dx\\s*=\\s*-\\s*radius\\s*;\\s*dx\\s*<=\\s*radius");
        Pattern dy = Pattern.compile("dy\\s*=\\s*-\\s*radius\\s*;\\s*dy\\s*<=\\s*radius");
        Pattern dz = Pattern.compile("dz\\s*=\\s*-\\s*radius\\s*;\\s*dz\\s*<=\\s*radius");
        assertTrue(dx.matcher(body).find(), "serverTick must loop dx from -radius to radius");
        assertTrue(dy.matcher(body).find(), "serverTick must loop dy from -radius to radius");
        assertTrue(dz.matcher(body).find(), "serverTick must loop dz from -radius to radius");

        Pattern skipOrigin = Pattern.compile(
                "dx\\s*==\\s*0\\s*&&\\s*dy\\s*==\\s*0\\s*&&\\s*dz\\s*==\\s*0");
        assertTrue(skipOrigin.matcher(body).find(),
                "serverTick must skip the origin (dx==dy==dz==0) so the bathtub itself isn't sampled.");
    }

    @Test
    @DisplayName("serverTick removes SNOW, SNOW_BLOCK, and POWDER_SNOW via setBlock(AIR, 3)")
    void serverTickRemovesAllThreeSnowTypes() throws IOException {
        String body = extractServerTickBody();
        Pattern snow = Pattern.compile("Blocks\\s*\\.\\s*SNOW\\b");
        Pattern snowBlock = Pattern.compile("Blocks\\s*\\.\\s*SNOW_BLOCK");
        Pattern powder = Pattern.compile("Blocks\\s*\\.\\s*POWDER_SNOW");
        assertTrue(snow.matcher(body).find(),
                "serverTick must check Blocks.SNOW (snow layer). Missing this means snow layers persist "
                        + "around hot baths.");
        assertTrue(snowBlock.matcher(body).find(),
                "serverTick must check Blocks.SNOW_BLOCK. Missing this means full snow blocks persist.");
        assertTrue(powder.matcher(body).find(),
                "serverTick must check Blocks.POWDER_SNOW. Missing this means powder snow persists.");

        Pattern setAir = Pattern.compile(
                "level\\s*\\.\\s*setBlock\\s*\\(\\s*check\\s*,\\s*"
                        + "Blocks\\s*\\.\\s*AIR\\s*\\.\\s*defaultBlockState\\s*\\(\\s*\\)\\s*,\\s*3\\s*\\)");
        Matcher sm = setAir.matcher(body);
        int count = 0;
        while (sm.find()) count++;
        assertTrue(count >= 2,
                "serverTick must call 'level.setBlock(check, Blocks.AIR.defaultBlockState(), 3)' for the "
                        + "snow-remove path at least twice (one for snow/snow-block, one for powder snow). "
                        + "Found only " + count + " occurrences.");
    }

    // ---- Helpers ----------------------------------------------------------

    private static String extractServerTickBody() throws IOException {
        String source = readSource(BATHTUB_SOURCE);
        Pattern sig = Pattern.compile(
                "private\\s+static\\s+void\\s+serverTick\\s*\\(");
        return extractMethodBody(source, sig, "BathtubBlock.serverTick");
    }

    private static String readSource(Path path) throws IOException {
        if (!Files.isRegularFile(path)) {
            fail(path.getFileName() + " not found at " + path.toAbsolutePath());
        }
        return Files.readString(path);
    }

    private static String extractMethodBody(String source, Pattern sig, String humanName) {
        Matcher m = sig.matcher(source);
        if (!m.find()) {
            fail("Could not locate signature for '" + humanName + "' (pattern: " + sig.pattern() + ")");
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
        assertNotEquals(-1, openBrace,
                "Signature for '" + humanName + "' has no opening body brace");

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
