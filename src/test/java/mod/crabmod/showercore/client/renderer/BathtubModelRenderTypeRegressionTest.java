package mod.crabmod.showercore.client.renderer;

import mod.crabmod.showercore.testutil.TestSourceUtils;
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
 * Resource-level regression test for bathtub block model render types.
 *
 * <p>Bathtub models come in two families:
 * <ul>
 *   <li><b>Fluid-containing</b> ({@code bathtub_*_head.json}, {@code bathtub_*_foot.json},
 *       {@code bathtub_*_head_running.json}) — these include a fluid surface quad using
 *       {@code #fluid} / {@code water_still} with {@code tintindex: 0}. They MUST use
 *       {@code "render_type": "translucent"} so the fluid has proper alpha blending;
 *       {@code cutout} forces binary alpha and makes the fluid fully opaque.</li>
 *   <li><b>Empty</b> ({@code bathtub_*_head_empty.json}, {@code bathtub_*_foot_empty.json})
 *       — no fluid element. These use {@code "render_type": "cutout"} so the solid walls
 *       render in the opaque pass without depth-sort artifacts.</li>
 * </ul>
 *
 * <p>All 33 color/material variants inherit from the white root models, so only the
 * root models declare {@code render_type}; changing the root propagates everywhere.
 */
class BathtubModelRenderTypeRegressionTest {

    private static final Path MODELS_DIR = Paths.get(
            "src", "main", "resources", "assets", "showercore", "models", "block");

    private static final Pattern TRANSLUCENT_DECLARATION =
            Pattern.compile("\"render_type\"\\s*:\\s*\"translucent\"");

    private static final Pattern CUTOUT_DECLARATION =
            Pattern.compile("\"render_type\"\\s*:\\s*\"cutout\"");

    @Test
    @DisplayName("Fluid-containing root models (head/foot/head_running) declare translucent")
    void fluidModelsAreTranslucent() throws IOException {
        String[] fluidModels = {
                "bathtub_white_head.json",
                "bathtub_white_foot.json",
                "bathtub_white_head_running.json"
        };
        for (String name : fluidModels) {
            Path model = MODELS_DIR.resolve(name);
            if (!Files.isRegularFile(model)) {
                fail(name + " not found at " + model.toAbsolutePath());
            }
            String content = Files.readString(model);
            assertTrue(TRANSLUCENT_DECLARATION.matcher(content).find(),
                    name + " must declare \"render_type\": \"translucent\" so the fluid "
                            + "surface has proper alpha blending. Using cutout makes the fluid "
                            + "fully opaque.");
            assertFalse(CUTOUT_DECLARATION.matcher(content).find(),
                    name + " must NOT declare cutout — the fluid quad needs translucent.");
        }
    }

    @Test
    @DisplayName("Empty/faucet root models (head_empty/foot_empty/head_faucet) declare cutout")
    void emptyModelsAreCutout() throws IOException {
        String[] emptyModels = {
                "bathtub_white_head_empty.json",
                "bathtub_white_foot_empty.json",
                "bathtub_white_head_faucet.json"
        };
        for (String name : emptyModels) {
            Path model = MODELS_DIR.resolve(name);
            if (!Files.isRegularFile(model)) {
                fail(name + " not found at " + model.toAbsolutePath());
            }
            String content = Files.readString(model);
            assertTrue(CUTOUT_DECLARATION.matcher(content).find(),
                    name + " must declare \"render_type\": \"cutout\" — no fluid element, "
                            + "so walls should stay in the opaque pass.");
            assertFalse(TRANSLUCENT_DECLARATION.matcher(content).find(),
                    name + " must NOT declare translucent — wall-only models have no fluid "
                            + "surface and gain nothing from the translucent pass.");
        }
    }

    @Test
    @DisplayName("At least 100 bathtub_*.json models exist (sanity guard)")
    void bathtubModelCountSanity() throws IOException {
        List<Path> models = collectBathtubModels();
        assertTrue(models.size() >= 100,
                "Expected at least 100 bathtub_*.json model files under "
                        + MODELS_DIR.toAbsolutePath() + " — found " + models.size()
                        + ". If the count dropped, a variant was deleted.");
    }

    private static List<Path> collectBathtubModels() throws IOException {
        if (!Files.isDirectory(MODELS_DIR)) {
            fail("Bathtub models directory not found at " + MODELS_DIR.toAbsolutePath()
                    + " (cwd=" + Paths.get("").toAbsolutePath() + ")");
        }
        List<Path> results = new ArrayList<>();
        try (Stream<Path> stream = Files.walk(MODELS_DIR)) {
            stream.filter(Files::isRegularFile)
                    .filter(p -> {
                        String name = p.getFileName().toString();
                        return name.startsWith("bathtub_") && name.endsWith(".json");
                    })
                    .forEach(results::add);
        }
        return results;
    }
}
