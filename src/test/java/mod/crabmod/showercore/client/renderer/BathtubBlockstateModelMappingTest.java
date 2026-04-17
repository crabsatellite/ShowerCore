package mod.crabmod.showercore.client.renderer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Validates that bathtub blockstate files map ALL fluid variants to empty/faucet
 * models (no fluid-containing models), with no {@code "textures"} overrides.
 *
 * <p>Architecture: fluid surfaces are rendered exclusively by the BER
 * ({@link BathtubBlockEntityRenderer}), so blockstate variants must only
 * reference wall-only models (_head_empty, _foot_empty, _head_faucet).
 *
 * <p>Past bugs:
 * <ul>
 *   <li>Blockstate {@code "textures"} overrides were silently ignored by Minecraft,
 *       causing classic fluids to render with white_concrete texture instead of
 *       their correct hotBath textures.</li>
 *   <li>Fluid-containing models (bathtub_*_head.json, _head_running.json) included
 *       a tinted fluid quad — but since BER now handles all fluid rendering, these
 *       old models must no longer be referenced from blockstates.</li>
 * </ul>
 */
class BathtubBlockstateModelMappingTest {

    private static final Path BLOCKSTATES_DIR = Paths.get(
            "src", "main", "resources", "assets", "showercore", "blockstates");

    /** Classic hotBath + water liquid types that use _head_faucet when running=true on head. */
    private static final String[] FAUCET_LIQUIDS = {
            "water", "hot_water", "herbal_bath", "honey_bath",
            "milk_bath", "peony_bath", "rose_bath"
    };

    @Test
    @DisplayName("No blockstate file has 'textures' overrides (they are silently ignored)")
    void noTextureOverridesInBlockstates() throws IOException {
        for (Path bs : collectBathtubBlockstates()) {
            String content = Files.readString(bs);
            JsonObject root = JsonParser.parseString(content).getAsJsonObject();
            JsonObject variants = root.getAsJsonObject("variants");
            for (Map.Entry<String, JsonElement> entry : variants.entrySet()) {
                JsonObject variant = entry.getValue().getAsJsonObject();
                assertFalse(variant.has("textures"),
                        bs.getFileName() + " variant '" + entry.getKey()
                                + "' has a 'textures' override which Minecraft silently ignores. "
                                + "Remove it — fluid rendering is handled by the BER.");
            }
        }
    }

    @Test
    @DisplayName("All foot variants use _foot_empty model (never fluid-containing)")
    void footVariantsUseEmptyModel() throws IOException {
        for (Path bs : collectBathtubBlockstates()) {
            JsonObject variants = parseVariants(bs);
            for (Map.Entry<String, JsonElement> entry : variants.entrySet()) {
                if (!entry.getKey().contains("part=foot")) continue;
                String model = entry.getValue().getAsJsonObject().get("model").getAsString();
                assertTrue(model.endsWith("_foot_empty"),
                        bs.getFileName() + " variant '" + entry.getKey()
                                + "' uses model '" + model + "' but foot variants must always "
                                + "use _foot_empty (BER renders the fluid surface).");
            }
        }
    }

    @Test
    @DisplayName("Non-running head variants use _head_empty (never fluid-containing)")
    void nonRunningHeadVariantsUseEmptyModel() throws IOException {
        for (Path bs : collectBathtubBlockstates()) {
            JsonObject variants = parseVariants(bs);
            for (Map.Entry<String, JsonElement> entry : variants.entrySet()) {
                String key = entry.getKey();
                if (!key.contains("part=head") || !key.contains("running=false")) continue;
                String model = entry.getValue().getAsJsonObject().get("model").getAsString();
                assertTrue(model.endsWith("_head_empty"),
                        bs.getFileName() + " variant '" + key
                                + "' uses model '" + model + "' but non-running head variants "
                                + "must use _head_empty (BER renders the fluid surface).");
            }
        }
    }

    @Test
    @DisplayName("Running head + classic liquid variants use _head_faucet model")
    void runningHeadClassicLiquidsUseFaucetModel() throws IOException {
        for (Path bs : collectBathtubBlockstates()) {
            JsonObject variants = parseVariants(bs);
            for (String liquid : FAUCET_LIQUIDS) {
                for (Map.Entry<String, JsonElement> entry : variants.entrySet()) {
                    String key = entry.getKey();
                    if (!key.contains("part=head") || !key.contains("running=true")
                            || !key.contains("liquid=" + liquid)) continue;
                    String model = entry.getValue().getAsJsonObject().get("model").getAsString();
                    assertTrue(model.endsWith("_head_faucet"),
                            bs.getFileName() + " variant '" + key
                                    + "' uses model '" + model + "' but running head with liquid="
                                    + liquid + " must use _head_faucet (faucet spout geometry).");
                }
            }
        }
    }

    @Test
    @DisplayName("CUSTOM liquid head variants always use _head_empty (drop rendered by BER)")
    void customLiquidHeadUsesEmptyModel() throws IOException {
        for (Path bs : collectBathtubBlockstates()) {
            JsonObject variants = parseVariants(bs);
            for (Map.Entry<String, JsonElement> entry : variants.entrySet()) {
                String key = entry.getKey();
                if (!key.contains("liquid=custom") || !key.contains("part=head")) continue;
                String model = entry.getValue().getAsJsonObject().get("model").getAsString();
                assertTrue(model.endsWith("_head_empty"),
                        bs.getFileName() + " variant '" + key
                                + "' uses model '" + model + "' but CUSTOM liquid head must use "
                                + "_head_empty (the BER renders both the fluid surface and the "
                                + "running drop for CUSTOM fluids).");
            }
        }
    }

    @Test
    @DisplayName("No variant references old fluid-containing models (_head.json, _head_running.json)")
    void noFluidContainingModelReferences() throws IOException {
        for (Path bs : collectBathtubBlockstates()) {
            JsonObject variants = parseVariants(bs);
            for (Map.Entry<String, JsonElement> entry : variants.entrySet()) {
                String model = entry.getValue().getAsJsonObject().get("model").getAsString();
                String modelName = model.substring(model.lastIndexOf('/') + 1);
                // Must not end with _head, _foot, or _head_running (the old fluid models)
                assertFalse(modelName.matches("bathtub_\\w+_head$"),
                        bs.getFileName() + " variant '" + entry.getKey()
                                + "' references old fluid-containing model '" + model + "'.");
                assertFalse(modelName.contains("_head_running"),
                        bs.getFileName() + " variant '" + entry.getKey()
                                + "' references old _head_running model '" + model + "'.");
                assertFalse(modelName.matches("bathtub_\\w+_foot$"),
                        bs.getFileName() + " variant '" + entry.getKey()
                                + "' references old fluid-containing foot model '" + model + "'.");
            }
        }
    }

    @Test
    @DisplayName("All 33 bathtub blockstate files exist")
    void allBlockstateFilesExist() throws IOException {
        List<Path> files = collectBathtubBlockstates();
        assertTrue(files.size() >= 33,
                "Expected at least 33 bathtub_*.json blockstate files — found " + files.size());
    }

    // ---- Helpers ----

    private static JsonObject parseVariants(Path blockstateFile) throws IOException {
        String content = Files.readString(blockstateFile);
        return JsonParser.parseString(content).getAsJsonObject().getAsJsonObject("variants");
    }

    private static List<Path> collectBathtubBlockstates() throws IOException {
        Path dir = BLOCKSTATES_DIR;
        if (!Files.isDirectory(dir)) {
            fail("Blockstates directory not found at " + dir.toAbsolutePath()
                    + " (cwd=" + Paths.get("").toAbsolutePath() + ")");
        }
        List<Path> results = new ArrayList<>();
        try (Stream<Path> stream = Files.list(dir)) {
            stream.filter(p -> {
                String name = p.getFileName().toString();
                return name.startsWith("bathtub_") && name.endsWith(".json");
            }).forEach(results::add);
        }
        return results;
    }
}
