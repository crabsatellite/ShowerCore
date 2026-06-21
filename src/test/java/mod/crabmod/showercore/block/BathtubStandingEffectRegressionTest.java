package mod.crabmod.showercore.block;

import mod.crabmod.showercore.testutil.TestSourceUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BathtubStandingEffectRegressionTest {

    private static final Path BATHTUB_SOURCE = Paths.get(
            "src", "main", "java", "mod", "crabmod", "showercore", "block", "BathtubBlock.java");
    private static final Path SEAT_SOURCE = Paths.get(
            "src", "main", "java", "mod", "crabmod", "showercore", "entity", "SeatEntity.java");
    private static final Path SHOWER_HEAD_SOURCE = Paths.get(
            "src", "main", "java", "mod", "crabmod", "showercore", "entity", "ShowerHeadContainerEntity.java");

    private static final Pattern ENTITY_INSIDE_SIG = Pattern.compile("\\bvoid\\s+entityInside\\s*\\(");
    private static final Pattern APPLY_BATH_EFFECTS_SIG = Pattern.compile(
            "public\\s+static\\s+void\\s+applyBathEffects\\s*\\(");

    private static String entityInsideBody;
    private static String applyBathEffectsBody;
    private static String showerHeadSource;

    @BeforeAll
    static void loadSource() throws IOException {
        entityInsideBody = TestSourceUtils.extractMethodBody(
                TestSourceUtils.readSource(BATHTUB_SOURCE), ENTITY_INSIDE_SIG, "BathtubBlock.entityInside");
        applyBathEffectsBody = TestSourceUtils.extractMethodBody(
                TestSourceUtils.readSource(SEAT_SOURCE), APPLY_BATH_EFFECTS_SIG, "SeatEntity.applyBathEffects");
        showerHeadSource = TestSourceUtils.readSource(SHOWER_HEAD_SOURCE);
    }

    @Test
    @DisplayName("SeatEntity exposes a shared applyBathEffects method")
    void seatEntityExposesSharedEffectMethod() throws IOException {
        String seatSource = TestSourceUtils.readSource(SEAT_SOURCE);
        assertTrue(APPLY_BATH_EFFECTS_SIG.matcher(seatSource).find(),
                "SeatEntity.applyBathEffects must be public static so BathtubBlock.entityInside can reuse it.");
    }

    @Test
    @DisplayName("Shared effect method covers all six built-in bath liquids")
    void sharedEffectMethodCoversAllBuiltInBathLiquids() {
        String[] liquids = {
                "HOT_WATER", "HERBAL_BATH", "HONEY_BATH",
                "MILK_BATH", "PEONY_BATH", "ROSE_BATH"
        };
        for (String liquid : liquids) {
            assertTrue(applyBathEffectsBody.contains(liquid),
                    "SeatEntity.applyBathEffects must handle " + liquid + ".");
        }
    }

    @Test
    @DisplayName("Standing entities call SeatEntity.applyBathEffects once per second on the server")
    void standingEntitiesCallSharedEffectMethod() {
        int call = entityInsideBody.indexOf("SeatEntity.applyBathEffects");
        assertNotEquals(-1, call,
                "BathtubBlock.entityInside must call SeatEntity.applyBathEffects for standing entities.");

        String guardWindow = entityInsideBody.substring(Math.max(0, call - 260), call);
        assertTrue(Pattern.compile("!\\s*level\\s*\\.\\s*isClientSide").matcher(guardWindow).find(),
                "The standing effect call must be server-side only.");
        assertTrue(Pattern.compile("instanceof\\s+LivingEntity").matcher(guardWindow).find(),
                "The standing effect call must only pass LivingEntity instances.");
        assertTrue(Pattern.compile("tickCount\\s*%\\s*20\\s*==\\s*0").matcher(guardWindow).find(),
                "The standing effect call must be gated to once per second.");
    }

    @Test
    @DisplayName("Standing effect call stays before hot-only interaction logic")
    void standingEffectsAreNotInsideHotOnlyBlock() {
        int call = entityInsideBody.indexOf("SeatEntity.applyBathEffects");
        int hotBlock = entityInsideBody.indexOf("Hot bathtub interactions");
        assertNotEquals(-1, call, "Missing SeatEntity.applyBathEffects call.");
        assertNotEquals(-1, hotBlock, "Missing Hot bathtub interactions marker.");
        assertTrue(call < hotBlock,
                "Standing bath effects must run before the hot-only interaction block, otherwise cool baths "
                        + "such as milk, peony, rose, honey, and herbal baths lose their effects.");
    }

    @Test
    @DisplayName("ShowerHead effect helpers are package-visible for SeatEntity reuse")
    void showerHeadHelpersArePackageVisible() {
        assertTrue(Pattern.compile("(?m)^\\s*static\\s+void\\s+addStackingEffect\\s*\\(")
                        .matcher(showerHeadSource).find(),
                "ShowerHeadContainerEntity.addStackingEffect must be package-visible, not private.");
        assertTrue(Pattern.compile("(?m)^\\s*static\\s+void\\s+cureNegativeEffects\\s*\\(")
                        .matcher(showerHeadSource).find(),
                "ShowerHeadContainerEntity.cureNegativeEffects must be package-visible, not private.");
        assertTrue(Pattern.compile("(?m)^\\s*static\\s+void\\s+cureNegativeEffectsExceptSlow\\s*\\(")
                        .matcher(showerHeadSource).find(),
                "Honey-bath logic must expose cureNegativeEffectsExceptSlow for SeatEntity reuse.");
    }
}
