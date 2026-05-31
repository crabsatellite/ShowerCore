package mod.crabmod.showercore.testutil;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.fail;

public final class ModelJsonTestUtils {

    private ModelJsonTestUtils() {}

    public static JsonObject readJson(Path path) throws IOException {
        return JsonParser.parseString(TestSourceUtils.readSource(path)).getAsJsonObject();
    }

    public static JsonObject resolveBlockModel(Path blockModelsDir, String filename) throws IOException {
        Path resolvedBlockModelsDir = TestSourceUtils.resolveAgainstCwdAncestors(blockModelsDir, 4);
        String modelName = filename.endsWith(".json")
                ? filename.substring(0, filename.length() - ".json".length())
                : filename;
        return resolveModel(resolvedBlockModelsDir.getParent(), "showercore:block/" + modelName, new HashSet<>());
    }

    public static JsonObject resolveModel(Path modelsRoot, String modelId, Set<String> seen) throws IOException {
        if (!seen.add(modelId)) {
            fail("Model parent cycle detected at " + modelId);
        }

        Path modelPath = resolveModelPath(modelsRoot, modelId);
        JsonObject child = readJson(modelPath);
        JsonObject merged = new JsonObject();

        if (child.has("parent")) {
            String parent = child.get("parent").getAsString();
            if (parent.startsWith("showercore:")) {
                merged = resolveModel(modelsRoot, parent, seen);
            }
        }

        for (Map.Entry<String, JsonElement> entry : child.entrySet()) {
            String key = entry.getKey();
            if ("parent".equals(key)) {
                continue;
            }
            if ("textures".equals(key) && merged.has("textures")) {
                JsonObject textures = merged.getAsJsonObject("textures").deepCopy();
                for (Map.Entry<String, JsonElement> texture : entry.getValue().getAsJsonObject().entrySet()) {
                    textures.add(texture.getKey(), texture.getValue().deepCopy());
                }
                merged.add("textures", textures);
                continue;
            }
            merged.add(key, entry.getValue().deepCopy());
        }

        seen.remove(modelId);
        return merged;
    }

    private static Path resolveModelPath(Path modelsRoot, String modelId) {
        String path = modelId.contains(":") ? modelId.substring(modelId.indexOf(':') + 1) : modelId;
        return modelsRoot.resolve(path + ".json");
    }
}
