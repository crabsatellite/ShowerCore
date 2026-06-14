package mod.crabmod.showercore.block;

import mod.crabmod.showercore.testutil.TestSourceUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Source-level regression gates for dynamic bathtub material changes.
 *
 * <p>The material system is economy-sensitive: it lets any block skin a bathtub,
 * so the server-side interaction must not make expensive appearances cheaper
 * than the old six-block recipes, must not double-charge through offhand/mainhand
 * corner cases, and must not refund old material on replacement.
 */
class BathtubMaterialChangeEconomyRegressionTest {

    private static final Path BATHTUB_SOURCE = Paths.get(
            "src", "main", "java", "mod", "crabmod", "showercore", "block", "BathtubBlock.java");
    private static final Path ENTITY_SOURCE = Paths.get(
            "src", "main", "java", "mod", "crabmod", "showercore", "block", "entity", "BathtubBlockEntity.java");
    private static final Path MODEL_SOURCE = Paths.get(
            "src", "main", "java", "mod", "crabmod", "showercore", "client", "model", "DynamicBathtubMaterialModel.java");

    private static final Pattern TRY_APPLY_SIG = Pattern.compile(
            "\\bItemInteractionResult\\s+tryApplyMaterialFromBlockItem\\s*\\(");
    private static final Pattern GET_MATERIAL_BLOCK_SIG = Pattern.compile(
            "\\bBlock\\s+getMaterialBlock\\s*\\(");

    private static String bathtubSource;
    private static String materialBody;

    @BeforeAll
    static void loadSource() throws IOException {
        bathtubSource = TestSourceUtils.readSource(BATHTUB_SOURCE);
        materialBody = TestSourceUtils.extractMethodBody(
                bathtubSource, TRY_APPLY_SIG, "BathtubBlock.tryApplyMaterialFromBlockItem");
    }

    @Test
    @DisplayName("Material changes cost exactly six source blocks")
    void materialChangeCostIsSixBlocks() {
        assertTrue(
                bathtubSource.contains("private static final int MATERIAL_CHANGE_COST = 6;"),
                "Dynamic material changes must stay aligned with the old bathtub recipes: "
                        + "survival players need and spend six matching source blocks.");
    }

    @Test
    @DisplayName("Only the main hand may trigger a material change")
    void materialChangeIsMainHandOnly() {
        int handGuard = materialBody.indexOf("hand != InteractionHand.MAIN_HAND");
        int blockItem = materialBody.indexOf("itemstack.getItem() instanceof BlockItem");
        assertNotEquals(-1, handGuard,
                "tryApplyMaterialFromBlockItem must reject offhand calls. This prevents a "
                        + "main-hand bathtub item plus offhand material from triggering a hidden "
                        + "second interaction path.");
        assertNotEquals(-1, blockItem,
                "The material interaction must still gate on a held BlockItem.");
        assertTrue(handGuard < blockItem,
                "The main-hand guard must run before inspecting/charging the held block item.");
    }

    @Test
    @DisplayName("Bathtub items cannot be used as material sources")
    void bathtubItemsAreNotMaterialSources() {
        assertTrue(
                materialBody.contains("materialBlock instanceof BathtubBlock"),
                "A bathtub BlockItem must never count as a source material. Otherwise players "
                        + "could create confusing bathtub-on-bathtub interaction paths.");
    }

    @Test
    @DisplayName("Reapplying the same material is a no-op before any cost check or mutation")
    void reapplyingSameMaterialIsFreeNoOp() {
        int sameMaterial = materialBody.indexOf("materialMatchesConnectedParts");
        int countCheck = materialBody.indexOf("itemstack.getCount() < MATERIAL_CHANGE_COST");
        int setMaterial = materialBody.indexOf("setMaterialForConnectedParts");
        assertNotEquals(-1, sameMaterial,
                "The material path must detect when both bathtub halves already have the "
                        + "requested material.");
        assertNotEquals(-1, countCheck,
                "The material path must check the held stack count before mutation.");
        assertNotEquals(-1, setMaterial,
                "The material path must mutate both connected bathtub halves together.");
        assertTrue(sameMaterial < countCheck,
                "Reapplying the same material should be a no-op and should not demand six "
                        + "blocks just to do nothing.");
        assertTrue(sameMaterial < setMaterial,
                "The same-material no-op must happen before setMaterialForConnectedParts so it "
                        + "cannot consume items or resend block updates.");
    }

    @Test
    @DisplayName("Insufficient stacks consume the click before any material mutation")
    void insufficientStackStopsBeforeMutation() {
        int countCheck = materialBody.indexOf("itemstack.getCount() < MATERIAL_CHANGE_COST");
        int setMaterial = materialBody.indexOf("setMaterialForConnectedParts");
        assertTrue(countCheck >= 0 && setMaterial >= 0 && countCheck < setMaterial,
                "The server must check the live ItemStack count before changing block entity "
                        + "NBT. If the player dropped or otherwise lost items before the server "
                        + "handles the click, the change must not go through.");
        assertTrue(
                materialBody.contains("message.showercore.bathtub.material.not_enough"),
                "Insufficient material should produce a clear player-facing message.");
        assertTrue(
                materialBody.contains("return ItemInteractionResult.sidedSuccess(level.isClientSide);"),
                "Insufficient material must consume the interaction so vanilla/offhand placement "
                        + "does not run after the failed skin attempt.");
    }

    @Test
    @DisplayName("Survival item shrink is gated by an actual material change")
    void shrinkOnlyWhenMaterialActuallyChanges() {
        Pattern shrinkGate = Pattern.compile(
                "if\\s*\\(\\s*changed\\s*&&\\s*!player\\.isCreative\\(\\)\\s*\\)\\s*\\{\\s*"
                        + "itemstack\\.shrink\\(MATERIAL_CHANGE_COST\\)\\s*;",
                Pattern.DOTALL);
        assertTrue(shrinkGate.matcher(materialBody).find(),
                "The source stack must shrink by MATERIAL_CHANGE_COST only after "
                        + "setMaterialForConnectedParts reports a real change, and never in "
                        + "creative mode.");
    }

    @Test
    @DisplayName("Replacing material does not refund the previous material")
    void replacingMaterialDoesNotRefundOldMaterial() {
        assertFalse(materialBody.contains("popResource("),
                "Material replacement must not drop the old material; otherwise players could "
                        + "farm materials by swapping skins.");
        assertFalse(materialBody.contains("spawnAtLocation("),
                "Material replacement must not spawn the old material as an item.");
        assertFalse(materialBody.contains("Containers.dropItemStack"),
                "Material replacement must not drop container/item-stack refunds.");
        assertFalse(materialBody.contains("player.addItem"),
                "Material replacement must not add the previous material back to the player.");
    }

    @Test
    @DisplayName("Removed source mods fall back visually and keep inert material ids")
    void missingMaterialModFallsBackWithoutPurgingNbt() throws IOException {
        String getMaterialBlockBody = TestSourceUtils.extractMethodBody(
                bathtubSource, GET_MATERIAL_BLOCK_SIG, "BathtubBlock.getMaterialBlock");
        assertTrue(
                getMaterialBlockBody.contains("orElse(null)") || getMaterialBlockBody.contains("return null;"),
                "Missing material blocks must fall back to the bathtub's normal sound/model behavior.");
        assertFalse(
                getMaterialBlockBody.contains("setMaterialBlockId(null)"),
                "A missing material block should not purge MaterialBlockId. Keeping the id lets "
                        + "the skin come back if the source mod is reinstalled.");

        String entitySource = TestSourceUtils.readSource(ENTITY_SOURCE);
        assertTrue(entitySource.contains("tag.putString(TAG_MATERIAL_BLOCK_ID"),
                "BathtubBlockEntity must continue saving MaterialBlockId even if the current "
                        + "client cannot resolve that block id.");
        assertFalse(entitySource.contains("getOptional(materialBlockId"),
                "BathtubBlockEntity load/save should not validate MaterialBlockId against the "
                        + "current registry; registry validation would delete skins from mods "
                        + "that are temporarily absent.");

        String modelSource = TestSourceUtils.readSource(MODEL_SOURCE);
        assertTrue(modelSource.contains("Optional<TextureAtlasSprite>"),
                "The dynamic model should cache failed material sprite lookups as Optional.empty.");
        assertTrue(modelSource.contains("return resolved.orElse(null);"),
                "Unresolved material sprites must return null so the original bathtub model is used.");
    }
}
