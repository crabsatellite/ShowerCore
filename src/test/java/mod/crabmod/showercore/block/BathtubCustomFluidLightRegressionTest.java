package mod.crabmod.showercore.block;

import mod.crabmod.showercore.testutil.TestSourceUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertTrue;

class BathtubCustomFluidLightRegressionTest {

    private static final Path BATHTUB_SOURCE = Paths.get(
            "src", "main", "java", "mod", "crabmod", "showercore", "block", "BathtubBlock.java");
    private static final Path BLOCK_ENTITY_SOURCE = Paths.get(
            "src", "main", "java", "mod", "crabmod", "showercore", "block", "entity", "BathtubBlockEntity.java");

    private static String bathtubSource;
    private static String blockEntitySource;
    private static String refreshBody;

    @BeforeAll
    static void loadSource() throws IOException {
        bathtubSource = TestSourceUtils.readSource(BATHTUB_SOURCE);
        blockEntitySource = TestSourceUtils.readSource(BLOCK_ENTITY_SOURCE);
        refreshBody = TestSourceUtils.extractMethodBody(
                blockEntitySource,
                Pattern.compile("private\\s+void\\s+refreshCustomFluidLighting\\s*\\("),
                "BathtubBlockEntity.refreshCustomFluidLighting");
    }

    @Test
    @DisplayName("Bathtub custom-fluid light level comes from CustomFluidDefinition.luminosity")
    void customFluidLightUsesDefinitionLuminosity() {
        String getLightBody = TestSourceUtils.extractMethodBody(
                bathtubSource,
                Pattern.compile("public\\s+int\\s+getLightEmission\\s*\\("),
                "BathtubBlock.getLightEmission");
        assertTrue(getLightBody.contains("state.getValue(LIQUID) != LiquidType.CUSTOM"),
                "Only CUSTOM bathtub liquids should use dynamic custom-fluid light.");
        assertTrue(getLightBody.contains("CustomFluidDefinition::luminosity"),
                "Custom bathtub light emission must read CustomFluidDefinition::luminosity.");
    }

    @Test
    @DisplayName("Every saved/synced custom fluid path refreshes light")
    void customFluidSyncPathsRefreshLight() {
        String[] methods = {
                "setCustomFluidId", "onLoad", "handleUpdateTag", "onDataPacket"
        };
        for (String method : methods) {
            String body = TestSourceUtils.extractMethodBody(
                    blockEntitySource,
                    Pattern.compile("\\b" + method + "\\s*\\("),
                    "BathtubBlockEntity." + method);
            assertTrue(body.contains("refreshCustomFluidLighting"),
                    method + " must call refreshCustomFluidLighting so emissive custom fluids keep "
                            + "their light after live updates, relog, or dimension changes.");
        }
    }

    @Test
    @DisplayName("Light refresh updates client rendering, light engine, and server clients")
    void refreshUpdatesDirtyBlocksLightEngineAndClients() {
        assertTrue(refreshBody.contains("setBlocksDirty"),
                "Client refresh must call setBlocksDirty so cached chunk render/light data is invalidated.");
        assertTrue(refreshBody.contains("getLightEngine().checkBlock"),
                "Refresh must ask the light engine to recheck the bathtub position.");
        assertTrue(refreshBody.contains("sendBlockUpdated"),
                "Server refresh must send a block update so clients receive the custom fluid id.");
        assertTrue(refreshBody.contains("notifyServerClients && !level.isClientSide"),
                "sendBlockUpdated must be gated to explicit server notifications only.");
    }
}
