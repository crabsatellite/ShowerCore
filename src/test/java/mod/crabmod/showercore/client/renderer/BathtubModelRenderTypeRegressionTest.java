package mod.crabmod.showercore.client.renderer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Resource-level regression test for Bug #6: bathtub block model render type.
 *
 * <p><b>Bug under regression:</b> Bathtub block models that contain a fluid
 * plane previously declared {@code "render_type": "translucent"} at the top
 * level of the model JSON. This caused the OPAQUE walls of the bathtub to
 * render in the translucent pass, producing a depth-sort bug where adjacent
 * water sources made the bathtub walls appear see-through.
 *
 * <p><b>Fix:</b> All non-empty bathtub model JSON files in
 * {@code assets/showercore/models/block/bathtub_*.json} were changed from
 * {@code "render_type": "translucent"} to {@code "render_type": "cutout"}.
 * The {@code _empty} variants were already {@code cutout}. Child models that
 * inherit from a parent (e.g. {@code bathtub_acacia.json} → parent
 * {@code bathtub_white.json}) do not declare {@code render_type} and inherit
 * from the parent.
 *
 * <p><b>Why file-content-based:</b> Minecraft classes (e.g. {@code BlockModel},
 * {@code RenderType}) are not on the unit-test classpath, so we can't load or
 * render the models behaviorally. This test enforces the fix as a textual
 * invariant over the model JSON resources: no bathtub model may declare the
 * translucent render type.
 */
class BathtubModelRenderTypeRegressionTest {

    private static final Path MODELS_DIR = Paths.get(
            "src", "main", "resources", "assets", "showercore", "models", "block");

    /** Whitespace-tolerant match for {@code "render_type": "translucent"}. */
    private static final Pattern TRANSLUCENT_DECLARATION =
            Pattern.compile("\"render_type\"\\s*:\\s*\"translucent\"");

    /** Whitespace-tolerant match for {@code "render_type": "cutout"}. */
    private static final Pattern CUTOUT_DECLARATION =
            Pattern.compile("\"render_type\"\\s*:\\s*\"cutout\"");

    @Test
    @DisplayName("No bathtub_*.json model declares render_type \"translucent\"")
    void noBathtubModelDeclaresTranslucentRenderType() throws IOException {
        List<Path> bathtubModels = collectBathtubModels();

        assertTrue(
                bathtubModels.size() >= 100,
                "Expected to find at least 100 bathtub_*.json model files under "
                        + MODELS_DIR.toAbsolutePath() + " (sanity guard against the resource "
                        + "directory being renamed or moved, which would make this test pass "
                        + "trivially) — found " + bathtubModels.size() + ".");

        List<String> offenders = new ArrayList<>();
        for (Path model : bathtubModels) {
            String content = Files.readString(model);
            if (TRANSLUCENT_DECLARATION.matcher(content).find()) {
                offenders.add(model.getFileName().toString());
            }
        }

        assertTrue(
                offenders.isEmpty(),
                "The following bathtub model JSON files declare \"render_type\": "
                        + "\"translucent\", which re-introduces Bug #6 (opaque bathtub walls "
                        + "rendered in the translucent pass appear see-through next to water): "
                        + offenders + ". Change them to \"cutout\".");
    }

    @Test
    @DisplayName("At least one bathtub model declares render_type \"cutout\" (positive sanity)")
    void atLeastOneBathtubModelDeclaresCutoutRenderType() throws IOException {
        List<Path> bathtubModels = collectBathtubModels();

        boolean anyCutout = false;
        for (Path model : bathtubModels) {
            String content = Files.readString(model);
            if (CUTOUT_DECLARATION.matcher(content).find()) {
                anyCutout = true;
                break;
            }
        }

        assertTrue(
                anyCutout,
                "Expected at least one bathtub_*.json to declare \"render_type\": \"cutout\" — "
                        + "none found. The Bug #6 fix requires bathtub models with fluid planes "
                        + "to use the cutout render type so opaque walls stay in the solid pass.");
    }

    @Test
    @DisplayName("bathtub_white_head.json (canonical known-bad sample) declares render_type \"cutout\"")
    void bathtubWhiteHeadDeclaresCutoutRenderType() throws IOException {
        Path canonical = MODELS_DIR.resolve("bathtub_white_head.json");
        if (!Files.isRegularFile(canonical)) {
            fail("Canonical sample bathtub_white_head.json not found at "
                    + canonical.toAbsolutePath() + " (cwd=" + Paths.get("").toAbsolutePath()
                    + "). This file was the known-bad sample for Bug #6 and must exist so the "
                    + "regression can be locked down.");
        }

        String content = Files.readString(canonical);

        assertFalse(
                TRANSLUCENT_DECLARATION.matcher(content).find(),
                "bathtub_white_head.json must NOT declare \"render_type\": \"translucent\" — "
                        + "this is the canonical known-bad sample for Bug #6.");
        assertTrue(
                CUTOUT_DECLARATION.matcher(content).find(),
                "bathtub_white_head.json must declare \"render_type\": \"cutout\" — this is "
                        + "the canonical known-bad sample for Bug #6 and its render type is "
                        + "the explicit fix being regression-tested.");
    }

    // ---- Helpers ----------------------------------------------------------

    /**
     * Walks {@link #MODELS_DIR} and returns every regular file whose file name
     * matches {@code bathtub_*.json}. Fails the test if the directory does not
     * exist.
     */
    private static List<Path> collectBathtubModels() throws IOException {
        if (!Files.isDirectory(MODELS_DIR)) {
            fail("Bathtub models directory not found at " + MODELS_DIR.toAbsolutePath()
                    + " (cwd=" + Paths.get("").toAbsolutePath() + ")");
        }

        List<Path> results = new ArrayList<>();
        try (Stream<Path> stream = Files.walk(MODELS_DIR)) {
            stream
                    .filter(Files::isRegularFile)
                    .filter(p -> {
                        String name = p.getFileName().toString();
                        return name.startsWith("bathtub_") && name.endsWith(".json");
                    })
                    .forEach(results::add);
        }
        return results;
    }
}
