package mod.crabmod.showercore;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Regression test for Bug W (1.20 Forge port) — the "long" rain shower head's
 * lower-half geometry z-fought with the bathtub's back wall. Mirrors the 1.21
 * NeoForge test; all 33 variants share identical geometry (textures differ).
 *
 * <p>Fix applied to every {@code rain_shower_head_*.json}:
 *
 * <pre>
 * element 4 (horizontal flange, y=-4..-3)  from [3,-4,12.5] to [13,-3,14.5]
 *                                      →  from [5,-4,12.5] to [11,-3,14.5]
 * element 11 (big vertical stem, y=-16..-8) from [5,-16,14] to [11,-8,15.5]
 *                                      →  from [6,-16,14.5] to [10,-8,15.5]
 * element 12 (small flange, y=-3..-2)      from [6.01,-3,14] to [10.01,-2,15]
 *                                      →  from [6.01,-3,14.5] to [10.01,-2,15]
 * </pre>
 */
class ShowerHeadGeometryNarrowRegressionTest {

    private static final Path MODEL_DIR = Paths.get(
            "src", "main", "resources", "assets", "showercore", "models", "block");

    private static final String EXPECTED_ELEMENT_4 =
            "\"from\": [\n"
                    + "        5,\n"
                    + "        -4,\n"
                    + "        12.5\n"
                    + "      ],\n"
                    + "      \"to\": [\n"
                    + "        11,\n"
                    + "        -3,\n"
                    + "        14.5\n"
                    + "      ],";

    private static final String EXPECTED_ELEMENT_11 =
            "\"from\": [\n"
                    + "        6,\n"
                    + "        -16,\n"
                    + "        14.5\n"
                    + "      ],\n"
                    + "      \"to\": [\n"
                    + "        10,\n"
                    + "        -8,\n"
                    + "        15.5\n"
                    + "      ],";

    private static final String EXPECTED_ELEMENT_12 =
            "\"from\": [\n"
                    + "        6.01,\n"
                    + "        -3,\n"
                    + "        14.5\n"
                    + "      ],\n"
                    + "      \"to\": [\n"
                    + "        10.01,\n"
                    + "        -2,\n"
                    + "        15\n"
                    + "      ],";

    private static final String FORBIDDEN_ELEMENT_4_OLD =
            "\"from\": [\n"
                    + "        3,\n"
                    + "        -4,\n"
                    + "        12.5\n"
                    + "      ],\n"
                    + "      \"to\": [\n"
                    + "        13,";

    private static final String FORBIDDEN_ELEMENT_11_OLD =
            "\"from\": [\n"
                    + "        5,\n"
                    + "        -16,\n"
                    + "        14\n"
                    + "      ],";

    private static final String FORBIDDEN_ELEMENT_12_OLD =
            "\"from\": [\n"
                    + "        6.01,\n"
                    + "        -3,\n"
                    + "        14\n"
                    + "      ],";

    @Test
    @DisplayName("All rain_shower_head_*.json variants contain the narrowed element 4 (flange)")
    void allVariantsHaveNarrowedElement4() throws IOException {
        assertAllVariantsContain(EXPECTED_ELEMENT_4,
                "element 4 (flange y=-4..-3) narrowed from width 10 to width 6");
    }

    @Test
    @DisplayName("All rain_shower_head_*.json variants contain the narrowed element 11 (big stem)")
    void allVariantsHaveNarrowedElement11() throws IOException {
        assertAllVariantsContain(EXPECTED_ELEMENT_11,
                "element 11 (big stem y=-16..-8) narrowed from width 6 to 4, depth 1.5 to 1, front face z=14 → z=14.5");
    }

    @Test
    @DisplayName("All rain_shower_head_*.json variants contain the narrowed element 12 (small flange)")
    void allVariantsHaveNarrowedElement12() throws IOException {
        assertAllVariantsContain(EXPECTED_ELEMENT_12,
                "element 12 (small flange y=-3..-2) front face z=14 → z=14.5");
    }

    @Test
    @DisplayName("No variant still contains the OLD (pre-narrow) geometry for elements 4 / 11 / 12")
    void noVariantHasOldGeometry() throws IOException {
        List<Path> variants = listRainShowerHeadVariants();
        assertTrue(variants.size() >= 33,
                "Expected at least 33 rain_shower_head_*.json variants, found " + variants.size());

        List<String> failures = new ArrayList<>();
        for (Path f : variants) {
            String content = normalize(Files.readString(f));
            if (content.contains(normalize(FORBIDDEN_ELEMENT_4_OLD))) {
                failures.add(f.getFileName() + ": still has OLD element 4 ([3,-4,12.5] → [13,...])");
            }
            if (content.contains(normalize(FORBIDDEN_ELEMENT_11_OLD))) {
                failures.add(f.getFileName() + ": still has OLD element 11 ([5,-16,14])");
            }
            if (content.contains(normalize(FORBIDDEN_ELEMENT_12_OLD))) {
                failures.add(f.getFileName() + ": still has OLD element 12 ([6.01,-3,14])");
            }
        }
        assertEquals(0, failures.size(),
                "Bug W regression — some shower head variants still have pre-narrow geometry:\n"
                        + String.join("\n", failures));
    }

    @Test
    @DisplayName("At least 33 rain_shower_head_*.json variants exist")
    void expectedVariantCountPresent() throws IOException {
        List<Path> variants = listRainShowerHeadVariants();
        assertTrue(variants.size() >= 33,
                "Expected at least 33 rain_shower_head_*.json variants, found only " + variants.size());
    }

    // ---- Helpers ----------------------------------------------------------

    private static void assertAllVariantsContain(String expectedFragment, String human) throws IOException {
        String want = normalize(expectedFragment);
        List<Path> variants = listRainShowerHeadVariants();
        List<String> missing = new ArrayList<>();
        for (Path f : variants) {
            String content = normalize(Files.readString(f));
            if (!content.contains(want)) {
                missing.add(f.getFileName().toString());
            }
        }
        assertEquals(0, missing.size(),
                "Bug W regression — " + human + ". Variants missing the post-narrow fragment:\n"
                        + String.join("\n", missing)
                        + "\n\nExpected:\n" + expectedFragment);
    }

    private static List<Path> listRainShowerHeadVariants() throws IOException {
        Path dir = resolveModelDir();
        List<Path> out = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "rain_shower_head_*.json")) {
            for (Path p : stream) out.add(p);
        }
        if (out.isEmpty()) {
            fail("No rain_shower_head_*.json variants found under " + dir.toAbsolutePath());
        }
        return out;
    }

    private static Path resolveModelDir() {
        if (Files.isDirectory(MODEL_DIR)) return MODEL_DIR;
        Path cwd = Paths.get("").toAbsolutePath();
        for (int i = 0; i < 4; i++) {
            Path candidate = cwd.resolve(MODEL_DIR);
            if (Files.isDirectory(candidate)) return candidate;
            if (cwd.getParent() == null) break;
            cwd = cwd.getParent();
        }
        return MODEL_DIR.toAbsolutePath();
    }

    private static String normalize(String s) {
        return s.replace("\r\n", "\n");
    }
}
