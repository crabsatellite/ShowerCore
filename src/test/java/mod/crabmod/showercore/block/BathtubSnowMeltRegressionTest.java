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
 * Source-level regression test for Bug U (the 1.20 Forge counterpart of Bug S)
 * — snow layers / snow blocks within 3 blocks of a hot bathtub must melt.
 *
 * <p>The mechanism is identical to the 1.21 fix: {@code BathtubBlock#getTicker}
 * returns a server-tick lambda; {@code serverTick} runs every 80 ticks
 * (staggered by {@code pos.asLong()}) and removes SNOW/SNOW_BLOCK/POWDER_SNOW
 * within radius 3 whenever the bath is hot.
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
                        + "Without this override no serverTick ever fires and Bug U regresses.");

        String body = extractMethodBody(source, sig, "BathtubBlock.getTicker");
        assertTrue(Pattern.compile("level\\s*\\.\\s*isClientSide").matcher(body).find(),
                "getTicker must early-return for level.isClientSide.");
        assertTrue(Pattern.compile("BATHTUB_BLOCK_ENTITY").matcher(body).find(),
                "getTicker must guard on BATHTUB_BLOCK_ENTITY type.");
    }

    @Test
    @DisplayName("serverTick runs every 80 ticks, staggered by pos.asLong()")
    void serverTickRunsEvery80TicksStaggered() throws IOException {
        String body = extractServerTickBody();
        Pattern gate = Pattern.compile(
                "\\(\\s*level\\s*\\.\\s*getGameTime\\s*\\(\\s*\\)\\s*\\+\\s*pos\\s*\\.\\s*asLong\\s*\\(\\s*\\)\\s*\\)"
                        + "\\s*%\\s*80L?\\s*!=\\s*0");
        assertTrue(gate.matcher(body).find(),
                "serverTick must be gated by '(level.getGameTime() + pos.asLong()) % 80 != 0'.");
    }

    @Test
    @DisplayName("serverTick skips EMPTY and WATER liquids")
    void serverTickSkipsEmptyAndWater() throws IOException {
        String body = extractServerTickBody();
        Pattern liquidSkip = Pattern.compile(
                "liquid\\s*==\\s*LiquidType\\s*\\.\\s*EMPTY\\s*\\|\\|\\s*liquid\\s*==\\s*LiquidType\\s*\\.\\s*WATER");
        assertTrue(liquidSkip.matcher(body).find(),
                "serverTick must early-return when the liquid is EMPTY or WATER.");
    }

    @Test
    @DisplayName("CUSTOM liquids only melt snow when CoreUtils.isCustomFluidHotAt reports hot")
    void customLiquidMeltsOnlyWhenHot() throws IOException {
        String body = extractServerTickBody();
        assertTrue(Pattern.compile("liquid\\s*!=\\s*LiquidType\\s*\\.\\s*CUSTOM").matcher(body).find(),
                "serverTick must test 'liquid != LiquidType.CUSTOM' as part of the hot check.");
        assertTrue(Pattern.compile("isCustomFluidHotAt\\s*\\(\\s*level\\s*,\\s*pos\\s*\\)").matcher(body).find(),
                "serverTick must delegate custom-fluid temperature to CoreUtils.isCustomFluidHotAt(level, pos).");
    }

    @Test
    @DisplayName("serverTick scans radius 3 in all three axes and skips the origin")
    void serverTickScansRadius3() throws IOException {
        String body = extractServerTickBody();

        assertTrue(Pattern.compile("radius\\s*=\\s*3").matcher(body).find(),
                "serverTick must declare 'radius = 3'.");

        assertTrue(Pattern.compile("dx\\s*=\\s*-\\s*radius\\s*;\\s*dx\\s*<=\\s*radius").matcher(body).find(),
                "serverTick must loop dx from -radius to radius");
        assertTrue(Pattern.compile("dy\\s*=\\s*-\\s*radius\\s*;\\s*dy\\s*<=\\s*radius").matcher(body).find(),
                "serverTick must loop dy from -radius to radius");
        assertTrue(Pattern.compile("dz\\s*=\\s*-\\s*radius\\s*;\\s*dz\\s*<=\\s*radius").matcher(body).find(),
                "serverTick must loop dz from -radius to radius");

        assertTrue(Pattern.compile("dx\\s*==\\s*0\\s*&&\\s*dy\\s*==\\s*0\\s*&&\\s*dz\\s*==\\s*0").matcher(body).find(),
                "serverTick must skip the origin.");
    }

    @Test
    @DisplayName("serverTick removes SNOW, SNOW_BLOCK, and POWDER_SNOW via setBlock(AIR, 3)")
    void serverTickRemovesAllThreeSnowTypes() throws IOException {
        String body = extractServerTickBody();
        assertTrue(Pattern.compile("Blocks\\s*\\.\\s*SNOW\\b").matcher(body).find(),
                "serverTick must check Blocks.SNOW.");
        assertTrue(Pattern.compile("Blocks\\s*\\.\\s*SNOW_BLOCK").matcher(body).find(),
                "serverTick must check Blocks.SNOW_BLOCK.");
        assertTrue(Pattern.compile("Blocks\\s*\\.\\s*POWDER_SNOW").matcher(body).find(),
                "serverTick must check Blocks.POWDER_SNOW.");

        Pattern setAir = Pattern.compile(
                "level\\s*\\.\\s*setBlock\\s*\\(\\s*check\\s*,\\s*"
                        + "Blocks\\s*\\.\\s*AIR\\s*\\.\\s*defaultBlockState\\s*\\(\\s*\\)\\s*,\\s*3\\s*\\)");
        Matcher sm = setAir.matcher(body);
        int count = 0;
        while (sm.find()) count++;
        assertTrue(count >= 1,
                "serverTick must call 'level.setBlock(check, Blocks.AIR.defaultBlockState(), 3)' at least "
                        + "once to actually remove the snow block. Found " + count + ".");
    }

    // ---- Helpers ----------------------------------------------------------

    private static String extractServerTickBody() throws IOException {
        String source = readSource(BATHTUB_SOURCE);
        Pattern sig = Pattern.compile("private\\s+static\\s+void\\s+serverTick\\s*\\(");
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
