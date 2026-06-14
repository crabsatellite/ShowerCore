package mod.crabmod.showercore.client.renderer;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BathtubLegacyModelRegressionTest {

    private static final Path TEMPLATE_DIR = Paths.get(
            "src", "main", "resources", "assets", "showercore", "models", "block", "template");

    @Test
    @DisplayName("Legacy bathtub foot base has a textured outer tail face")
    void legacyFootBaseHasOuterTailFace() throws IOException {
        for (String template : new String[] {"bathtub_foot_empty.json", "bathtub_foot.json"}) {
            JsonObject faces = readTemplate(template)
                    .getAsJsonArray("elements")
                    .get(0)
                    .getAsJsonObject()
                    .getAsJsonObject("faces");

            assertTrue(faces.has("south"),
                    template + " base element must include the outer tail face; otherwise the foot bottom is transparent.");
            assertEquals("#0", faces.getAsJsonObject("south").get("texture").getAsString(),
                    template + " south face texture");
            assertEquals(faces.getAsJsonObject("north").getAsJsonArray("uv").toString(),
                    faces.getAsJsonObject("south").getAsJsonArray("uv").toString(),
                    template + " south face UV should match the existing base side strip.");
        }
    }

    private static JsonObject readTemplate(String filename) throws IOException {
        return JsonParser.parseString(Files.readString(TEMPLATE_DIR.resolve(filename))).getAsJsonObject();
    }
}
