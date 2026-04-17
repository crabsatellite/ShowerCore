package mod.crabmod.showercore.mixin;

import mod.crabmod.showercore.testutil.TestSourceUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Resource-level regression test for {@code mixins.showercore.json} and the
 * mixin Java sources it references (1.20 Forge).
 *
 * <p>Invariants guarded:
 * <ul>
 *   <li>{@code mixins.showercore.json} lists every mixin class in the
 *       {@code mixin/} package, and vice versa.</li>
 *   <li>{@code required: true} — the mixin config must fail-fast.</li>
 *   <li>The {@code package} prefix matches the actual Java package.</li>
 * </ul>
 */
class MixinConfigIntegrityTest {

    private static final Path MIXINS_JSON = Paths.get(
            "src", "main", "resources", "mixins.showercore.json");
    private static final Path MIXIN_DIR = Paths.get(
            "src", "main", "java", "mod", "crabmod", "showercore", "mixin");
    private static final String EXPECTED_PACKAGE = "mod.crabmod.showercore.mixin";

    private static String mixinsJson;

    @BeforeAll
    static void loadMixinsJson() throws IOException {
        mixinsJson = TestSourceUtils.normalize(Files.readString(MIXINS_JSON));
    }

    @Test
    @DisplayName("mixins.showercore.json declares required: true (fail-fast semantics)")
    void mixinsJsonIsRequired() {
        assertTrue(mixinsJson.matches("(?s).*\"required\"\\s*:\\s*true.*"),
                "mixins.showercore.json must set \"required\": true. A non-required mixin config silently "
                        + "skips on apply failure — DirtinessHandler and CustomFluidHandler mixins are both "
                        + "load-bearing; if they can't apply, the mod MUST refuse to load rather than "
                        + "running broken.");
    }

    @Test
    @DisplayName("package field matches mod.crabmod.showercore.mixin")
    void packageFieldMatches() {
        assertTrue(mixinsJson.contains("\"package\": \"" + EXPECTED_PACKAGE + "\"")
                        || mixinsJson.contains("\"package\":\"" + EXPECTED_PACKAGE + "\""),
                "mixins.showercore.json 'package' field must be '" + EXPECTED_PACKAGE + "'.");
    }

    @Test
    @DisplayName("Every class listed in mixins.json exists as a .java file in the mixin package")
    void everyListedMixinHasSourceFile() throws IOException {
        for (String name : extractMixinList(mixinsJson)) {
            Path p = MIXIN_DIR.resolve(name + ".java");
            assertTrue(Files.isRegularFile(p),
                    "mixins.showercore.json lists '" + name + "' but no matching file exists at " + p
                            + ". Orphan entries fail at load time with ClassNotFoundException.");
        }
    }

    @Test
    @DisplayName("Every .java file in the mixin package is listed in mixins.json")
    void everyMixinSourceIsListedInJson() throws IOException {
        if (!Files.isDirectory(MIXIN_DIR)) return;
        try (var stream = Files.newDirectoryStream(MIXIN_DIR, "*Mixin.java")) {
            for (Path p : stream) {
                String name = p.getFileName().toString().replace(".java", "");
                assertTrue(mixinsJson.contains("\"" + name + "\""),
                        "Mixin class '" + name + "' exists under " + MIXIN_DIR + " but is NOT listed in "
                                + "mixins.showercore.json. An un-listed mixin is silently orphaned at "
                                + "runtime.");
            }
        }
    }

    private static java.util.List<String> extractMixinList(String json) {
        java.util.List<String> out = new java.util.ArrayList<>();
        int arrStart = json.indexOf("\"mixins\"");
        if (arrStart < 0) return out;
        int openBracket = json.indexOf('[', arrStart);
        int closeBracket = json.indexOf(']', openBracket);
        if (openBracket < 0 || closeBracket < 0) return out;
        String arr = json.substring(openBracket + 1, closeBracket);
        for (String part : arr.split(",")) {
            String trimmed = part.trim();
            if (trimmed.startsWith("\"") && trimmed.endsWith("\"") && trimmed.length() > 2) {
                out.add(trimmed.substring(1, trimmed.length() - 1));
            }
        }
        return out;
    }
}
