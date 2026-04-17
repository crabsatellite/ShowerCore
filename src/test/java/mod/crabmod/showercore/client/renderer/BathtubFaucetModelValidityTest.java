package mod.crabmod.showercore.client.renderer;

import com.google.gson.JsonArray;
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
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Validates the structural integrity of bathtub _head_faucet model files.
 *
 * <p>The _head_faucet model contains bathtub walls + faucet spout geometry but
 * NO fluid surface — fluid rendering is exclusively handled by the BER.
 * All 33 color variants must exist, be valid JSON, and inherit correctly from
 * the white root model.
 *
 * <p>Past bugs:
 * <ul>
 *   <li>16 variant _head_faucet files had truncated JSON ({@code "textures": })
 *       causing MalformedJsonException and cascading FileNotFoundException.</li>
 * </ul>
 */
class BathtubFaucetModelValidityTest {

    private static final Path MODELS_DIR = Paths.get(
            "src", "main", "resources", "assets", "showercore", "models", "block");

    private static final String ROOT_MODEL = "bathtub_white_head_faucet.json";
    private static final String ROOT_PARENT_REF = "showercore:block/bathtub_white_head_faucet";

    /** All 33 color/material variants. */
    private static final String[] ALL_VARIANTS = {
            "white", "black", "orange", "magenta", "light_blue", "yellow", "lime",
            "pink", "gray", "light_gray", "cyan", "purple", "blue", "brown", "green", "red",
            "oak", "spruce", "birch", "jungle", "acacia", "dark_oak", "mangrove",
            "cherry", "bamboo", "crimson", "warped",
            "stone", "cobblestone", "iron", "gold", "copper", "diamond"
    };

    @Test
    @DisplayName("All 33 _head_faucet model files exist and are valid JSON")
    void allFaucetModelsExistAndAreValidJson() throws IOException {
        for (String variant : ALL_VARIANTS) {
            String filename = "bathtub_" + variant + "_head_faucet.json";
            Path model = MODELS_DIR.resolve(filename);
            assertTrue(Files.isRegularFile(model),
                    filename + " must exist at " + model.toAbsolutePath());
            String content = Files.readString(model);
            try {
                JsonParser.parseString(content).getAsJsonObject();
            } catch (Exception e) {
                fail(filename + " is not valid JSON: " + e.getMessage());
            }
        }
    }

    @Test
    @DisplayName("Root _head_faucet model uses render_type=cutout (no fluid elements)")
    void rootModelUsesCutout() throws IOException {
        Path root = MODELS_DIR.resolve(ROOT_MODEL);
        String content = Files.readString(root);
        JsonObject json = JsonParser.parseString(content).getAsJsonObject();
        assertEquals("cutout", json.get("render_type").getAsString(),
                ROOT_MODEL + " must use render_type=cutout since it has no fluid elements "
                        + "(the BER renders the fluid surface via RenderType.translucent).");
    }

    @Test
    @DisplayName("Root _head_faucet model has exactly 6 elements (4 walls + faucet handle + spout)")
    void rootModelHasSixElements() throws IOException {
        Path root = MODELS_DIR.resolve(ROOT_MODEL);
        String content = Files.readString(root);
        JsonObject json = JsonParser.parseString(content).getAsJsonObject();
        JsonArray elements = json.getAsJsonArray("elements");
        assertNotNull(elements, ROOT_MODEL + " must have an 'elements' array.");
        assertEquals(6, elements.size(),
                ROOT_MODEL + " must have exactly 6 elements: 4 walls + faucet handle + spout. "
                        + "Found " + elements.size());
    }

    @Test
    @DisplayName("Root _head_faucet model does NOT contain fluid elements (no tintindex, no #fluid)")
    void rootModelHasNoFluidElements() throws IOException {
        Path root = MODELS_DIR.resolve(ROOT_MODEL);
        String content = Files.readString(root);
        assertFalse(content.contains("tintindex"),
                ROOT_MODEL + " must NOT have tintindex — fluid tinting is done by the BER.");
        assertFalse(content.contains("#fluid"),
                ROOT_MODEL + " must NOT reference #fluid texture — no fluid in this model.");
    }

    @Test
    @DisplayName("Root _head_faucet model has faucet spout element at [7,11,3]..[9,12,4]")
    void rootModelHasFaucetSpoutElement() throws IOException {
        Path root = MODELS_DIR.resolve(ROOT_MODEL);
        String content = Files.readString(root);
        JsonObject json = JsonParser.parseString(content).getAsJsonObject();
        JsonArray elements = json.getAsJsonArray("elements");
        JsonObject spout = elements.get(5).getAsJsonObject();
        JsonArray from = spout.getAsJsonArray("from");
        JsonArray to = spout.getAsJsonArray("to");
        assertEquals(7, from.get(0).getAsInt(), "spout from[x]");
        assertEquals(11, from.get(1).getAsInt(), "spout from[y]");
        assertEquals(3, from.get(2).getAsInt(), "spout from[z]");
        assertEquals(9, to.get(0).getAsInt(), "spout to[x]");
        assertEquals(12, to.get(1).getAsInt(), "spout to[y]");
        assertEquals(4, to.get(2).getAsInt(), "spout to[z]");
    }

    @Test
    @DisplayName("Non-white variant models inherit from the white root _head_faucet")
    void variantModelsInheritFromWhiteRoot() throws IOException {
        for (String variant : ALL_VARIANTS) {
            if ("white".equals(variant)) continue;
            String filename = "bathtub_" + variant + "_head_faucet.json";
            Path model = MODELS_DIR.resolve(filename);
            String content = Files.readString(model);
            JsonObject json = JsonParser.parseString(content).getAsJsonObject();
            assertTrue(json.has("parent"),
                    filename + " must have a 'parent' field.");
            assertEquals(ROOT_PARENT_REF, json.get("parent").getAsString(),
                    filename + " must inherit from '" + ROOT_PARENT_REF + "'.");
        }
    }

    @Test
    @DisplayName("Non-white variant models have texture #0 and particle overrides")
    void variantModelsHaveTextureOverrides() throws IOException {
        for (String variant : ALL_VARIANTS) {
            if ("white".equals(variant)) continue;
            String filename = "bathtub_" + variant + "_head_faucet.json";
            Path model = MODELS_DIR.resolve(filename);
            String content = Files.readString(model);
            JsonObject json = JsonParser.parseString(content).getAsJsonObject();
            assertTrue(json.has("textures"),
                    filename + " must have a 'textures' block with color overrides.");
            JsonObject textures = json.getAsJsonObject("textures");
            assertTrue(textures.has("0"),
                    filename + " must override texture #0 (bathtub wall material).");
            assertTrue(textures.has("particle"),
                    filename + " must override particle texture.");
            assertEquals(textures.get("0").getAsString(), textures.get("particle").getAsString(),
                    filename + " texture #0 and particle must match.");
            assertFalse(textures.get("0").getAsString().contains("white_concrete"),
                    filename + " must NOT use white_concrete — it should use the " + variant + " texture.");
        }
    }

    @Test
    @DisplayName("Variant models do NOT define their own elements (inherited from root)")
    void variantModelsHaveNoElements() throws IOException {
        for (String variant : ALL_VARIANTS) {
            if ("white".equals(variant)) continue;
            String filename = "bathtub_" + variant + "_head_faucet.json";
            Path model = MODELS_DIR.resolve(filename);
            String content = Files.readString(model);
            JsonObject json = JsonParser.parseString(content).getAsJsonObject();
            assertFalse(json.has("elements"),
                    filename + " must NOT define its own elements — they are inherited "
                            + "from the white root model via parent.");
        }
    }
}
