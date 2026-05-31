package mod.crabmod.showercore.client.renderer;

import com.google.gson.JsonObject;
import mod.crabmod.showercore.testutil.ModelJsonTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Resource-level regression test for bathtub block model render types.
 * Models may now inherit render_type through shared texture templates, so tests
 * resolve parent chains before asserting the effective render pass.
 */
class BathtubModelRenderTypeRegressionTest {

    private static final Path MODELS_DIR = Paths.get(
            "src", "main", "resources", "assets", "showercore", "models", "block");

    @Test
    @DisplayName("Fluid-containing bathtub models resolve render_type=translucent")
    void fluidModelsAreTranslucent() throws IOException {
        for (Path model : collectBathtubModels()) {
            String name = model.getFileName().toString();
            if (!isFluidModel(name)) {
                continue;
            }
            JsonObject resolved = ModelJsonTestUtils.resolveBlockModel(MODELS_DIR, name);
            assertEquals("translucent", resolved.get("render_type").getAsString(),
                    name + " must resolve render_type=translucent so the fluid surface "
                            + "has proper alpha blending.");
        }
    }

    @Test
    @DisplayName("Empty/faucet bathtub models resolve render_type=cutout")
    void emptyModelsAreCutout() throws IOException {
        for (Path model : collectBathtubModels()) {
            String name = model.getFileName().toString();
            if (!isEmptyOrFaucetModel(name)) {
                continue;
            }
            JsonObject resolved = ModelJsonTestUtils.resolveBlockModel(MODELS_DIR, name);
            assertEquals("cutout", resolved.get("render_type").getAsString(),
                    name + " must resolve render_type=cutout because it has no fluid surface.");
        }
    }

    @Test
    @DisplayName("At least 100 bathtub_*.json direct model wrappers exist")
    void bathtubModelCountSanity() throws IOException {
        List<Path> models = collectBathtubModels();
        assertTrue(models.size() >= 100,
                "Expected at least 100 bathtub_*.json model files under "
                        + MODELS_DIR.toAbsolutePath() + ". Found " + models.size()
                        + ". If the count dropped, a variant was deleted.");
    }

    private static boolean isFluidModel(String name) {
        return !name.startsWith("bathtub_liquid_")
                && (name.matches("bathtub_.+_head\\.json")
                || name.matches("bathtub_.+_foot\\.json")
                || name.matches("bathtub_.+_head_running\\.json"));
    }

    private static boolean isEmptyOrFaucetModel(String name) {
        return name.matches("bathtub_.+_head_empty\\.json")
                || name.matches("bathtub_.+_foot_empty\\.json")
                || name.matches("bathtub_.+_head_faucet\\.json");
    }

    private static List<Path> collectBathtubModels() throws IOException {
        if (!Files.isDirectory(MODELS_DIR)) {
            fail("Bathtub models directory not found at " + MODELS_DIR.toAbsolutePath()
                    + " (cwd=" + Paths.get("").toAbsolutePath() + ")");
        }
        List<Path> results = new ArrayList<>();
        try (Stream<Path> stream = Files.walk(MODELS_DIR)) {
            stream.filter(Files::isRegularFile)
                    .filter(p -> MODELS_DIR.equals(p.getParent()))
                    .filter(p -> {
                        String name = p.getFileName().toString();
                        return name.startsWith("bathtub_") && name.endsWith(".json");
                    })
                    .forEach(results::add);
        }
        return results;
    }
}
