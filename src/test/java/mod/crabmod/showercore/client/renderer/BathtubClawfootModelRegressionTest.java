package mod.crabmod.showercore.client.renderer;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BathtubClawfootModelRegressionTest {

    private static final Path TEMPLATE_DIR = Paths.get(
            "src", "main", "resources", "assets", "showercore", "models", "block", "template");

    private static final String[] CLAWFOOT_TEMPLATES = {
            "bathtub_clawfoot.json",
            "bathtub_clawfoot_head_empty.json",
            "bathtub_clawfoot_head_faucet.json",
            "bathtub_clawfoot_foot_empty.json"
    };

    @Test
    @DisplayName("Split clawfoot templates follow BathtubBlock head/foot orientation")
    void splitTemplatesFollowBathtubPartOrientation() throws IOException {
        JsonObject head = readTemplate("bathtub_clawfoot_head_empty.json");
        JsonObject headFaucet = readTemplate("bathtub_clawfoot_head_faucet.json");
        JsonObject foot = readTemplate("bathtub_clawfoot_foot_empty.json");

        assertHasBox(head, "#body", 2, 4, 0, 14, 16, 2,
                "head must have its closed end at local north (z=0)");
        assertHasBox(headFaucet, "#body", 2, 4, 0, 14, 16, 2,
                "head_faucet must have its closed end at local north (z=0)");
        assertHasBox(foot, "#body", 2, 4, 14, 14, 16, 16,
                "foot must have its closed end at local south (z=14..16)");

        assertNoBox(head, "#body", 2, 4, 14, 14, 16, 16,
                "head was mirrored backwards into the foot orientation");
        assertNoBox(headFaucet, "#body", 2, 4, 14, 14, 16, 16,
                "head_faucet was mirrored backwards into the foot orientation");
        assertNoBox(foot, "#body", 2, 4, 0, 14, 16, 2,
                "foot was mirrored backwards into the head orientation");
    }

    @Test
    @DisplayName("Clawfoot decorative pieces use fixed texture slots, not body")
    void decorativePiecesUseFixedTextureSlots() throws IOException {
        JsonObject head = readTemplate("bathtub_clawfoot_head_empty.json");
        JsonObject foot = readTemplate("bathtub_clawfoot_foot_empty.json");
        JsonObject full = readTemplate("bathtub_clawfoot.json");

        assertBaseElementUsesBodyInteriorFloor(head);
        assertElementTexture(head, 1, "#faucet");
        assertElementTexture(head, 2, "#feet");
        assertElementTexture(head, 3, "#feet");
        assertElementTexture(head, 9, "#button_base");
        assertElementTexture(head, 10, "#hot_button");
        assertElementTexture(head, 11, "#cold_button");

        assertBaseElementUsesBodyInteriorFloor(foot);
        assertElementTexture(foot, 1, "#feet");
        assertElementTexture(foot, 2, "#feet");

        assertBaseElementUsesBodyInteriorFloor(full);
        assertElementTexture(full, 1, "#faucet");
        for (int i = 2; i <= 5; i++) {
            assertElementTexture(full, i, "#feet");
        }
        assertElementTexture(full, 14, "#button_base");
        assertElementTexture(full, 15, "#hot_button");
        assertElementTexture(full, 16, "#cold_button");
    }

    @Test
    @DisplayName("Split clawfoot corner rotations stay anchored to their own boxes")
    void splitCornerRotationsStayAnchoredToTheirOwnBoxes() throws IOException {
        for (String template : new String[] {
                "bathtub_clawfoot_head_empty.json",
                "bathtub_clawfoot_head_faucet.json",
                "bathtub_clawfoot_foot_empty.json"
        }) {
            JsonObject model = readTemplate(template);
            for (JsonElement element : model.getAsJsonArray("elements")) {
                JsonObject box = element.getAsJsonObject();
                if (!box.has("rotation")) {
                    continue;
                }
                JsonObject rotation = box.getAsJsonObject("rotation");
                if (!"y".equals(rotation.get("axis").getAsString())
                        || Math.abs(rotation.get("angle").getAsDouble()) < 0.0001) {
                    continue;
                }
                double originZ = rotation.getAsJsonArray("origin").get(2).getAsDouble();
                double fromZ = box.getAsJsonArray("from").get(2).getAsDouble();
                double toZ = box.getAsJsonArray("to").get(2).getAsDouble();
                assertTrue(originZ >= fromZ - 0.0001 && originZ <= toZ + 0.0001,
                        template + " has a rotated corner whose origin is outside its own z range.");
            }
        }
    }

    @Test
    @DisplayName("Clawfoot templates define vanilla fixed decoration textures")
    void templatesDefineFixedDecorationTextures() throws IOException {
        for (String template : CLAWFOOT_TEMPLATES) {
            JsonObject textures = readTemplate(template).getAsJsonObject("textures");
            assertEquals("minecraft:block/polished_deepslate", textures.get("base").getAsString(), template);
            assertEquals("minecraft:block/blackstone", textures.get("feet").getAsString(), template);
            assertEquals("minecraft:block/polished_blackstone", textures.get("faucet").getAsString(), template);
            assertEquals("minecraft:block/andesite", textures.get("button_base").getAsString(), template);
            assertEquals("minecraft:block/redstone_block", textures.get("hot_button").getAsString(), template);
            assertEquals("minecraft:block/lapis_block", textures.get("cold_button").getAsString(), template);
        }
    }

    @Test
    @DisplayName("Clawfoot templates no longer use the old shared metal slot")
    void templatesDoNotUseOldMetalSlot() throws IOException {
        for (String template : CLAWFOOT_TEMPLATES) {
            assertFalse(readTemplate(template).toString().contains("#metal"),
                    template + " must split decorative pieces into fixed texture slots.");
        }
    }

    private static JsonObject readTemplate(String filename) throws IOException {
        return JsonParser.parseString(Files.readString(TEMPLATE_DIR.resolve(filename))).getAsJsonObject();
    }

    private static void assertHasBox(JsonObject model, String texture,
                                     double fromX, double fromY, double fromZ,
                                     double toX, double toY, double toZ,
                                     String message) {
        assertTrue(hasBox(model, texture, fromX, fromY, fromZ, toX, toY, toZ), message);
    }

    private static void assertNoBox(JsonObject model, String texture,
                                    double fromX, double fromY, double fromZ,
                                    double toX, double toY, double toZ,
                                    String message) {
        assertFalse(hasBox(model, texture, fromX, fromY, fromZ, toX, toY, toZ), message);
    }

    private static boolean hasBox(JsonObject model, String texture,
                                  double fromX, double fromY, double fromZ,
                                  double toX, double toY, double toZ) {
        for (JsonElement element : model.getAsJsonArray("elements")) {
            JsonObject box = element.getAsJsonObject();
            if (coordsEqual(box.getAsJsonArray("from"), fromX, fromY, fromZ)
                    && coordsEqual(box.getAsJsonArray("to"), toX, toY, toZ)
                    && elementTextures(box).contains(texture)) {
                return true;
            }
        }
        return false;
    }

    private static void assertElementTexture(JsonObject model, int elementIndex, String texture) {
        JsonObject element = model.getAsJsonArray("elements").get(elementIndex).getAsJsonObject();
        assertEquals(1, elementTextures(element).size(),
                "Element " + elementIndex + " should use a single texture slot.");
        assertTrue(elementTextures(element).contains(texture),
                "Element " + elementIndex + " must use " + texture + ".");
    }

    private static void assertBaseElementUsesBodyInteriorFloor(JsonObject model) {
        assertFaceTexture(model, 0, "up", "#body");
        for (String face : new String[] { "north", "east", "south", "west", "down" }) {
            assertFaceTexture(model, 0, face, "#base");
        }
    }

    private static void assertFaceTexture(JsonObject model, int elementIndex, String face, String texture) {
        JsonObject element = model.getAsJsonArray("elements").get(elementIndex).getAsJsonObject();
        assertEquals(texture, element.getAsJsonObject("faces").getAsJsonObject(face).get("texture").getAsString(),
                "Element " + elementIndex + " " + face + " face texture");
    }

    private static boolean coordsEqual(JsonArray actual, double x, double y, double z) {
        return coordEquals(actual.get(0).getAsDouble(), x)
                && coordEquals(actual.get(1).getAsDouble(), y)
                && coordEquals(actual.get(2).getAsDouble(), z);
    }

    private static boolean coordEquals(double actual, double expected) {
        return Math.abs(actual - expected) < 0.0001;
    }

    private static Set<String> elementTextures(JsonObject element) {
        Set<String> textures = new LinkedHashSet<>();
        JsonObject faces = element.getAsJsonObject("faces");
        for (String face : faces.keySet()) {
            textures.add(faces.getAsJsonObject(face).get("texture").getAsString());
        }
        return textures;
    }
}
