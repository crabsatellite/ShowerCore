package mod.crabmod.showercore.block;

import mod.crabmod.showercore.testutil.TestSourceUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Source-level regression gates for dynamic bathtub material changes.
 *
 * <p>Material changes are item crafting, not placed-block mutation: players must
 * hold a bathtub and the material in opposite hands, then right-click air. Placed
 * bathtubs keep their saved material data for rendering/sound, but cannot be
 * reskinned by right-clicking them with a block.
 */
class BathtubMaterialChangeEconomyRegressionTest {

    private static final Path BATHTUB_SOURCE = Paths.get(
            "src", "main", "java", "mod", "crabmod", "showercore", "block", "BathtubBlock.java");
    private static final Path ENTITY_SOURCE = Paths.get(
            "src", "main", "java", "mod", "crabmod", "showercore", "block", "entity", "BathtubBlockEntity.java");
    private static final Path MODEL_SOURCE = Paths.get(
            "src", "main", "java", "mod", "crabmod", "showercore", "client", "model", "DynamicBathtubMaterialModel.java");
    private static final Path BATHTUB_ITEM_SOURCE = Paths.get(
            "src", "main", "java", "mod", "crabmod", "showercore", "item", "BathtubBlockItem.java");
    private static final Path SERVER_EVENT_SOURCE = Paths.get(
            "src", "main", "java", "mod", "crabmod", "showercore", "event", "ServerEvent.java");
    private static final Path LANG_DIR = Paths.get(
            "src", "main", "resources", "assets", "showercore", "lang");

    private static final Pattern ITEM_APPLY_SIG = Pattern.compile(
            "\\bInteractionResult\\s+applyMaterial\\s*\\(");
    private static final Pattern GET_MATERIAL_BLOCK_SIG = Pattern.compile(
            "\\bBlock\\s+getMaterialBlock\\s*\\(");

    private static String bathtubSource;
    private static String itemSource;
    private static String itemApplyBody;
    private static String serverEventSource;

    @BeforeAll
    static void loadSource() throws IOException {
        bathtubSource = TestSourceUtils.readSource(BATHTUB_SOURCE);
        itemSource = TestSourceUtils.readSource(BATHTUB_ITEM_SOURCE);
        itemApplyBody = TestSourceUtils.extractMethodBody(
                itemSource, ITEM_APPLY_SIG, "BathtubBlockItem.applyMaterial");
        serverEventSource = TestSourceUtils.readSource(SERVER_EVENT_SOURCE);
    }

    @Test
    @DisplayName("Placed bathtubs cannot be reskinned by right-clicking with a block")
    void placedBathtubSkinningIsDisabled() {
        assertFalse(bathtubSource.contains("tryApplyMaterialFromBlockItem"),
                "BathtubBlock must not expose a placed-block material change path.");
        assertFalse(bathtubSource.contains("setMaterialForConnectedParts"),
                "Placed bathtub halves must not be mutated by material block right-clicks.");
        assertFalse(serverEventSource.contains("PlayerInteractEvent.RightClickBlock"),
                "Held-bathtub skinning must not intercept right-click block events.");
        assertFalse(itemSource.contains("useOn(UseOnContext"),
                "BathtubBlockItem must not skin items from useOn; that would trigger while targeting blocks.");
    }

    @Test
    @DisplayName("Held bathtub material changes are air-right-click only")
    void heldBathtubSkinningIsAirRightClickOnly() {
        assertTrue(itemSource.contains("public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand)"),
                "Right-clicking air with a bathtub item must try item skinning.");
        assertTrue(serverEventSource.contains("PlayerInteractEvent.RightClickItem"),
                "Main-hand material plus offhand bathtub needs the air right-click item event.");
        assertTrue(serverEventSource.contains("BathtubBlockItem.tryApplyMaterialToHeldBathtub"),
                "The event path must reuse the same held-bathtub material helper as BathtubBlockItem.");
        assertTrue(serverEventSource.contains("event.setCancellationResult(result)")
                        && serverEventSource.contains("event.setCanceled(true)"),
                "Successful held-bathtub skinning must consume the air-use event.");
    }

    @Test
    @DisplayName("Material changes cost exactly six source blocks")
    void materialChangeCostIsSixBlocks() {
        assertTrue(itemSource.contains("private static final int MATERIAL_CHANGE_COST = 6;"),
                "Held bathtub material changes must stay aligned with the old bathtub recipes: "
                        + "survival players need and spend six matching source blocks.");
    }

    @Test
    @DisplayName("Held bathtub items can be skinned from either hand")
    void heldBathtubItemsCanBeSkinnedFromEitherHand() {
        assertTrue(itemSource.contains("tryApplyMaterialToHeldBathtub"),
                "BathtubBlockItem must expose a shared item-skinning helper for direct use and event interception.");
        assertTrue(itemSource.contains("activeHand == InteractionHand.MAIN_HAND"),
                "The helper must inspect the opposite hand so either main-hand or offhand bathtub use works.");
        assertTrue(itemSource.contains("applyMaterial(level, player, activeHand, otherHand)"),
                "A main-hand bathtub plus offhand material must work.");
        assertTrue(itemSource.contains("applyMaterial(level, player, otherHand, activeHand)"),
                "An offhand bathtub plus main-hand material must work.");
        assertTrue(itemSource.contains("ItemStack materialStack = player.getItemInHand(materialHand)"),
                "The material stack must be read from the resolved material hand, including offhand material.");
        assertTrue(itemSource.contains("InteractionResultHolder.sidedSuccess(player.getItemInHand(hand)"),
                "BathtubBlockItem.use must return the current hand stack after NBT mutation or stack splitting.");
        assertTrue(itemSource.contains("applyMaterialToOneBathtub"),
                "Held item skinning must apply NBT to one bathtub item, not an entire stack.");
    }

    @Test
    @DisplayName("Bathtub items cannot be used as material sources")
    void bathtubItemsAreNotMaterialSources() {
        assertTrue(itemApplyBody.contains("materialBlock instanceof BathtubBlock"),
                "A bathtub BlockItem must never count as a source material. Otherwise players "
                        + "could create confusing bathtub-on-bathtub interaction paths.");
    }

    @Test
    @DisplayName("Reapplying the same material is a no-op before any cost check or mutation")
    void reapplyingSameMaterialIsFreeNoOp() {
        int sameMaterial = itemApplyBody.indexOf("Objects.equals(getMaterialBlockId(bathtubStack), materialBlockId)");
        int countCheck = itemApplyBody.indexOf("materialStack.getCount() < MATERIAL_CHANGE_COST");
        int setMaterial = itemApplyBody.indexOf("applyMaterialToOneBathtub");
        assertNotEquals(-1, sameMaterial,
                "The held item path must detect when the bathtub item already has the requested material.");
        assertNotEquals(-1, countCheck,
                "The held item path must check the live material stack count before mutation.");
        assertNotEquals(-1, setMaterial,
                "The held item path must mutate the bathtub item NBT.");
        assertTrue(sameMaterial < countCheck,
                "Reapplying the same material should be a no-op and should not demand six blocks.");
        assertTrue(sameMaterial < setMaterial,
                "The same-material no-op must happen before item NBT mutation.");
    }

    @Test
    @DisplayName("Insufficient stacks consume the air click before any material mutation")
    void insufficientStackStopsBeforeMutation() {
        int countCheck = itemApplyBody.indexOf("materialStack.getCount() < MATERIAL_CHANGE_COST");
        int setMaterial = itemApplyBody.indexOf("applyMaterialToOneBathtub");
        assertTrue(countCheck >= 0 && setMaterial >= 0 && countCheck < setMaterial,
                "The server must check the live ItemStack count before changing bathtub item NBT.");
        assertTrue(itemApplyBody.contains("message.showercore.bathtub.material.not_enough"),
                "Insufficient material should produce a clear player-facing message.");
        assertTrue(itemApplyBody.contains("return InteractionResult.sidedSuccess"),
                "Insufficient material must consume the air interaction so a later vanilla use does not run.");
    }

    @Test
    @DisplayName("Survival item shrink is gated by an actual material change")
    void shrinkOnlyWhenMaterialActuallyChanges() {
        int setMaterial = itemApplyBody.indexOf("applyMaterialToOneBathtub");
        int shrink = itemApplyBody.indexOf("materialStack.shrink(MATERIAL_CHANGE_COST)");
        int creativeGuard = itemApplyBody.indexOf("if (!player.isCreative())");
        assertTrue(setMaterial >= 0 && shrink >= 0 && setMaterial < shrink,
                "The source stack must shrink only after the bathtub item NBT is changed.");
        assertTrue(creativeGuard >= 0 && creativeGuard < shrink,
                "Creative mode must not be charged material blocks.");
    }

    @Test
    @DisplayName("Replacing material does not refund the previous material")
    void replacingMaterialDoesNotRefundOldMaterial() {
        assertFalse(itemApplyBody.contains("popResource("),
                "Material replacement must not drop the old material; otherwise players could farm materials.");
        assertFalse(itemApplyBody.contains("spawnAtLocation("),
                "Material replacement must not spawn the old material as an item.");
        assertFalse(itemApplyBody.contains("Containers.dropItemStack"),
                "Material replacement must not drop container/item-stack refunds.");
        assertFalse(itemApplyBody.contains("player.addItem"),
                "Material replacement must not add the previous material back to the player.");
    }

    @Test
    @DisplayName("Removed source mods fall back visually and keep inert material ids")
    void missingMaterialModFallsBackWithoutPurgingNbt() throws IOException {
        String getMaterialBlockBody = TestSourceUtils.extractMethodBody(
                bathtubSource, GET_MATERIAL_BLOCK_SIG, "BathtubBlock.getMaterialBlock");
        assertTrue(getMaterialBlockBody.contains("orElse(null)") || getMaterialBlockBody.contains("return null;"),
                "Missing material blocks must fall back to the bathtub's normal sound/model behavior.");
        assertFalse(getMaterialBlockBody.contains("setMaterialBlockId(null)"),
                "A missing material block should not purge MaterialBlockId. Keeping the id lets "
                        + "the skin come back if the source mod is reinstalled.");

        String entitySource = TestSourceUtils.readSource(ENTITY_SOURCE);
        assertTrue(entitySource.contains("tag.putString(TAG_MATERIAL_BLOCK_ID"),
                "BathtubBlockEntity must continue saving MaterialBlockId even if the current "
                        + "client cannot resolve that block id.");
        assertFalse(entitySource.contains("getValue(materialBlockId"),
                "BathtubBlockEntity load/save should not validate MaterialBlockId against the "
                        + "current registry; registry validation would delete skins from mods "
                        + "that are temporarily absent.");

        String modelSource = TestSourceUtils.readSource(MODEL_SOURCE);
        assertTrue(modelSource.contains("Optional<TextureAtlasSprite>"),
                "The dynamic model should cache failed material sprite lookups as Optional.empty.");
        assertTrue(modelSource.contains("return resolved.orElse(null);"),
                "Unresolved material sprites must return null so the original bathtub model is used.");
    }

    @Test
    @DisplayName("Bathtub item rendering reads MaterialBlockId from ItemStack NBT")
    void bathtubItemRenderingReadsStackMaterial() throws IOException {
        String modelSource = TestSourceUtils.readSource(MODEL_SOURCE);
        assertTrue(modelSource.contains("getOverrides()"),
                "DynamicBathtubMaterialModel must override item model resolution.");
        assertTrue(modelSource.contains("public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand)"),
                "Item and dropped-item rendering use the vanilla three-argument getQuads path.");
        assertTrue(modelSource.contains("replaceMaterialQuads(super.getQuads(state, side, rand), state, itemMaterialBlockId)"),
                "The vanilla item getQuads path must apply the same material sprite replacement.");
        assertTrue(modelSource.contains("BathtubBlockItem.getMaterialBlockId(stack)"),
                "Item rendering must read MaterialBlockId from the ItemStack, not only from block entities.");
        assertTrue(modelSource.contains("itemMaterialBlockId"),
                "Resolved item models must carry the stack material id into getQuads.");
    }

    @Test
    @DisplayName("All language tooltips describe the air-right-click rule")
    void materialTooltipDescribesAirRightClickInEveryLanguage() throws IOException {
        List<Path> langFiles = TestSourceUtils.listByGlob(LANG_DIR, "*.json");
        List<String> obsoletePhrases = List.of(
                "Main Hand with 6+ Blocks",
                "主手手持 6",
                "メインハンド",
                "주 손에 블록",
                "Haupthand mit 6+",
                "Mano principal con 6+",
                "Main principale avec 6+",
                "Mão principal com 6+",
                "В основной руке 6+"
        );
        for (Path langFile : langFiles) {
            String source = TestSourceUtils.readSource(langFile);
            assertTrue(source.contains("\"tooltip.showercore.bathtub.usage.material\""),
                    langFile.getFileName() + " must define the material usage tooltip.");
            for (String obsolete : obsoletePhrases) {
                assertFalse(source.contains(obsolete),
                        langFile.getFileName() + " still describes the old placed/main-hand material rule.");
            }
        }
    }
}
