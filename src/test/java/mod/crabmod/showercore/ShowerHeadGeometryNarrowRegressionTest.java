package mod.crabmod.showercore;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import mod.crabmod.showercore.testutil.ModelJsonTestUtils;
import mod.crabmod.showercore.testutil.TestSourceUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Regression test for the rain shower head geometry that previously z-fought
 * with the bathtub back wall. The production models are now texture wrappers
 * over shared templates, so these checks resolve the parent chain first.
 */
class ShowerHeadGeometryNarrowRegressionTest {

    private static final Path MODEL_DIR = Paths.get(
            "src", "main", "resources", "assets", "showercore", "models", "block");

    private static final double[] EXPECTED_ELEMENT_4_FROM = {5, -4, 12.5};
    private static final double[] EXPECTED_ELEMENT_4_TO = {11, -3, 14.5};
    private static final double[] EXPECTED_ELEMENT_11_FROM = {6, -16, 14.5};
    private static final double[] EXPECTED_ELEMENT_11_TO = {10, -8, 15.5};
    private static final double[] EXPECTED_ELEMENT_12_FROM = {6.01, -3, 14.49};
    private static final double[] EXPECTED_ELEMENT_12_TO = {10.01, -2, 15};

    private static final double[] FORBIDDEN_ELEMENT_4_OLD_FROM = {3, -4, 12.5};
    private static final double[] FORBIDDEN_ELEMENT_4_OLD_TO = {13, -3, 14.5};
    private static final double[] FORBIDDEN_ELEMENT_11_OLD_FROM = {5, -16, 14};
    private static final double[] FORBIDDEN_ELEMENT_11_OLD_TO = {11, -8, 15.5};
    private static final double[] FORBIDDEN_ELEMENT_12_OLD_FROM = {6.01, -3, 14};
    private static final double[] FORBIDDEN_ELEMENT_12_OLD_TO = {10.01, -2, 15};

    private static Map<Path, JsonObject> variantModels;

    @BeforeAll
    static void loadVariants() throws IOException {
        Path dir = TestSourceUtils.resolveAgainstCwdAncestors(MODEL_DIR, 4);
        List<Path> variants = TestSourceUtils.listByGlob(dir, "rain_shower_head_*.json");
        variantModels = new LinkedHashMap<>();
        for (Path p : variants) {
            variantModels.put(p, ModelJsonTestUtils.resolveBlockModel(dir, p.getFileName().toString()));
        }
    }

    @Test
    @DisplayName("All rain_shower_head_*.json variants resolve the narrowed element 4")
    void allVariantsHaveNarrowedElement4() {
        assertAllVariantsContain(EXPECTED_ELEMENT_4_FROM, EXPECTED_ELEMENT_4_TO,
                "element 4 (flange y=-4..-3) narrowed from width 10 to width 6");
    }

    @Test
    @DisplayName("All rain_shower_head_*.json variants resolve the narrowed element 11")
    void allVariantsHaveNarrowedElement11() {
        assertAllVariantsContain(EXPECTED_ELEMENT_11_FROM, EXPECTED_ELEMENT_11_TO,
                "element 11 (big stem y=-16..-8) narrowed from width 6 to 4, depth 1.5 to 1");
    }

    @Test
    @DisplayName("All rain_shower_head_*.json variants resolve the narrowed element 12")
    void allVariantsHaveNarrowedElement12() {
        assertAllVariantsContain(EXPECTED_ELEMENT_12_FROM, EXPECTED_ELEMENT_12_TO,
                "element 12 (small flange y=-3..-2) front face z=14 -> z=14.49");
    }

    @Test
    @DisplayName("No variant resolves the old pre-narrow geometry for elements 4 / 11 / 12")
    void noVariantHasOldGeometry() {
        List<String> failures = new ArrayList<>();
        for (Map.Entry<Path, JsonObject> e : variantModels.entrySet()) {
            String name = e.getKey().getFileName().toString();
            JsonObject model = e.getValue();
            if (hasElement(model, FORBIDDEN_ELEMENT_4_OLD_FROM, FORBIDDEN_ELEMENT_4_OLD_TO)) {
                failures.add(name + ": still has old element 4");
            }
            if (hasElement(model, FORBIDDEN_ELEMENT_11_OLD_FROM, FORBIDDEN_ELEMENT_11_OLD_TO)) {
                failures.add(name + ": still has old element 11");
            }
            if (hasElement(model, FORBIDDEN_ELEMENT_12_OLD_FROM, FORBIDDEN_ELEMENT_12_OLD_TO)) {
                failures.add(name + ": still has old element 12");
            }
        }
        assertEquals(0, failures.size(),
                "Bug W regression: some shower head variants still resolve pre-narrow geometry:\n"
                        + String.join("\n", failures));
    }

    @Test
    @DisplayName("At least 33 rain_shower_head_*.json variants exist")
    void expectedVariantCountPresent() {
        assertTrue(variantModels.size() >= 33,
                "Expected at least 33 rain_shower_head_*.json variants. Found only "
                        + variantModels.size() + ".");
    }

    private static void assertAllVariantsContain(double[] from, double[] to, String human) {
        List<String> missing = new ArrayList<>();
        for (Map.Entry<Path, JsonObject> e : variantModels.entrySet()) {
            if (!hasElement(e.getValue(), from, to)) {
                missing.add(e.getKey().getFileName().toString());
            }
        }
        assertEquals(0, missing.size(),
                "Bug W regression: " + human + ". Variants missing the resolved geometry:\n"
                        + String.join("\n", missing)
                        + "\n\nExpected from/to: " + vector(from) + " -> " + vector(to));
    }

    private static boolean hasElement(JsonObject model, double[] from, double[] to) {
        JsonArray elements = model.getAsJsonArray("elements");
        if (elements == null) {
            return false;
        }
        for (JsonElement element : elements) {
            JsonObject object = element.getAsJsonObject();
            if (sameVector(object.getAsJsonArray("from"), from)
                    && sameVector(object.getAsJsonArray("to"), to)) {
                return true;
            }
        }
        return false;
    }

    private static boolean sameVector(JsonArray actual, double[] expected) {
        if (actual == null || actual.size() != expected.length) {
            return false;
        }
        for (int i = 0; i < expected.length; i++) {
            if (Math.abs(actual.get(i).getAsDouble() - expected[i]) > 0.0001) {
                return false;
            }
        }
        return true;
    }

    private static String vector(double[] values) {
        List<String> out = new ArrayList<>();
        for (double value : values) {
            out.add(Double.toString(value));
        }
        return "[" + String.join(",", out) + "]";
    }
}
