package mod.crabmod.showercore;

import mod.crabmod.showercore.testutil.TestSourceUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Source-level regression test for the mod entry-point class (1.20 Forge).
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
 *   <li>Both {@code COMMON} and {@code CLIENT} config specs are registered.</li>
 *   <li>{@code ServerEvent} is explicitly registered to the Forge event bus —
 *       1.20 Forge does not auto-scan {@code @EventBusSubscriber} unless the
 *       annotation is present on the class, so this explicit registration is
 *       load-bearing.</li>
 * </ul>
 */
class ShowerCoreModRegistrationTest {

    private static final Path SHOWER_CORE_SOURCE = Paths.get(
            "src", "main", "java", "mod", "crabmod", "showercore", "ShowerCore.java");

    private static String source;

    @BeforeAll
    static void loadSource() throws IOException {
        source = TestSourceUtils.readSource(SHOWER_CORE_SOURCE);
    }

    @Test
    @DisplayName("@Mod(ShowerCore.MODID) annotation is present on the class")
    void modAnnotationPresent() {
        assertTrue(Pattern.compile("@Mod\\s*\\(\\s*ShowerCore\\s*\\.\\s*MODID\\s*\\)").matcher(source).find(),
                "ShowerCore.java must be annotated '@Mod(ShowerCore.MODID)'. Without it Forge does not "
                        + "discover the mod and nothing loads.");
    }

    @Test
    @DisplayName("MODID constant equals \"showercore\"")
    void modIdConstantIsShowercore() {
        assertTrue(Pattern.compile(
                        "public\\s+static\\s+final\\s+String\\s+MODID\\s*=\\s*\"showercore\"\\s*;")
                        .matcher(source).find(),
                "ShowerCore.MODID must equal \"showercore\". Changing this value invalidates every "
                        + "assets/showercore/ and data/showercore/ path in the resource pack, breaks the "
                        + "config filename, and silently disables all @Mod.EventBusSubscriber(modid = MODID) "
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
    @DisplayName("Both COMMON and CLIENT config specs are registered")
    void bothConfigSpecsRegistered() {
        assertTrue(Pattern.compile("ModConfig\\s*\\.\\s*Type\\s*\\.\\s*COMMON[^;]+\\bConfig\\s*\\.\\s*SPEC")
                        .matcher(source).find(),
                "ShowerCore must register Config.SPEC as ModConfig.Type.COMMON. Missing → bath-core block "
                        + "lists, steam fluids, duck-destroy fluids, and the mod-integration toggle are all "
                        + "left at their Java-static defaults instead of being loaded from disk.");
        assertTrue(Pattern.compile("ModConfig\\s*\\.\\s*Type\\s*\\.\\s*CLIENT[^;]+ClientConfig\\s*\\.\\s*SPEC")
                        .matcher(source).find(),
                "ShowerCore must register ClientConfig.SPEC as ModConfig.Type.CLIENT.");
    }

    @Test
    @DisplayName("ServerEvent is explicitly registered to the Forge event bus (1.20)")
    void serverEventRegisteredToForgeBus() {
        assertTrue(Pattern.compile(
                        "MinecraftForge\\s*\\.\\s*EVENT_BUS\\s*\\.\\s*register\\s*\\(\\s*"
                                + "(?:mod\\s*\\.\\s*crabmod\\s*\\.\\s*showercore\\s*\\.\\s*event\\s*\\.\\s*)?"
                                + "ServerEvent\\s*\\.\\s*class\\s*\\)")
                        .matcher(source).find(),
                "ShowerCore ctor must call 'MinecraftForge.EVENT_BUS.register(ServerEvent.class)'. The "
                        + "1.20 Forge ServerEvent class lacks @Mod.EventBusSubscriber — if this explicit "
                        + "registration is dropped, player logout/death cleanup handlers never run and "
                        + "PLAYERS_UNDER_ACTIVE_SHOWER leaks UUIDs for the lifetime of the JVM.");
    }
}
