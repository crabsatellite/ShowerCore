package mod.crabmod.showercore;

import mod.crabmod.showercore.testutil.TestSourceUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Source-level regression test for the mod entry-point class.
 *
 * <p>Guards the load-bearing wiring in {@code ShowerCore.java}:
 * <ul>
 *   <li>Class annotated with {@code @Mod(ShowerCore.MODID)}.</li>
 *   <li>{@code MODID} constant equals {@code "showercore"} — every
 *       {@code assets/showercore/} and {@code data/showercore/} resource path is
 *       derived from this string.</li>
 *   <li>Constructor registers every {@code DeferredRegister} owner: Items,
 *       Blocks, BlockEntities, Entities, Particles, Sounds, and
 *       {@code ModEffects}. A missing register call silently removes an entire
 *       content category at runtime.</li>
 *   <li>Both {@code COMMON} and {@code CLIENT} config specs are registered so
 *       bath-core block lists and client-side flags actually load.</li>
 * </ul>
 */
class ShowerCoreModRegistrationTest {

    private static final Path SHOWER_CORE_SOURCE = Paths.get(
            "src", "main", "java", "mod", "crabmod", "showercore", "ShowerCore.java");
    private static final Path CREATIVE_TAB_REGISTER_SOURCE = Paths.get(
            "src", "main", "java", "mod", "crabmod", "showercore", "registers", "CreativeTabRegister.java");

    private static String source;

    @BeforeAll
    static void loadSource() throws IOException {
        source = TestSourceUtils.readSource(SHOWER_CORE_SOURCE);
    }

    @Test
    @DisplayName("@Mod(ShowerCore.MODID) annotation is present on the class")
    void modAnnotationPresent() {
        assertTrue(Pattern.compile("@Mod\\s*\\(\\s*ShowerCore\\s*\\.\\s*MODID\\s*\\)").matcher(source).find(),
                "ShowerCore.java must be annotated '@Mod(ShowerCore.MODID)'. Without it the mod loader does "
                        + "not discover the mod and nothing loads.");
    }

    @Test
    @DisplayName("MODID constant equals \"showercore\"")
    void modIdConstantIsShowercore() {
        assertTrue(Pattern.compile(
                        "public\\s+static\\s+final\\s+String\\s+MODID\\s*=\\s*\"showercore\"\\s*;")
                        .matcher(source).find(),
                "ShowerCore.MODID must equal \"showercore\". Changing this value invalidates every "
                        + "assets/showercore/ and data/showercore/ path in the resource pack, breaks the "
                        + "config filename, and silently disables all @EventBusSubscriber(modid = MODID) "
                        + "classes.");
    }

    @Test
    @DisplayName("Constructor registers all 7 DeferredRegister owners")
    void constructorRegistersAllRegisters() {
        for (String name : new String[] {
                "ItemRegister", "BlocksRegister", "BlockEntitiesRegister",
                "EntityRegister", "ParticleRegister", "SoundRegister", "ModEffects"}) {
            assertTrue(Pattern.compile(name + "\\s*\\.\\s*register\\s*\\(").matcher(source).find(),
                    "ShowerCore constructor must call '" + name + ".register(modEventBus)'. Forgetting "
                            + "any one of these silently removes an entire content category at runtime.");
        }
    }

    @Test
    @DisplayName("No separate ShowerCore creative tab is registered")
    void noSeparateShowerCoreCreativeTabRegistered() {
        assertFalse(source.contains("CreativeTabRegister"),
                "ShowerCore must not register its own creative tab. Items are added to the existing "
                        + "Hot Bath tab so creative mode does not show a second ShowerCore page.");
        assertFalse(Files.exists(CREATIVE_TAB_REGISTER_SOURCE),
                "CreativeTabRegister.java must stay deleted; otherwise a second ShowerCore creative "
                        + "tab can be reintroduced by one constructor call.");
    }

    @Test
    @DisplayName("Both COMMON and CLIENT config specs are registered")
    void bothConfigSpecsRegistered() {
        assertTrue(Pattern.compile("ModConfig\\s*\\.\\s*Type\\s*\\.\\s*COMMON[^;]+\\bConfig\\s*\\.\\s*SPEC")
                        .matcher(source).find(),
                "ShowerCore must register Config.SPEC as ModConfig.Type.COMMON. Missing → bath-core block "
                        + "lists, steam fluids, duck-destroy fluids, and the mod-integration toggle are all "
                        + "left at their Java-static defaults instead of being loaded from disk.");
        assertTrue(Pattern.compile("ModConfig\\s*\\.\\s*Type\\s*\\.\\s*CLIENT[^;]+ClientConfig\\s*\\.\\s*SPEC")
                        .matcher(source).find(),
                "ShowerCore must register ClientConfig.SPEC as ModConfig.Type.CLIENT so client-only "
                        + "render/sound toggles load from disk.");
    }
}
