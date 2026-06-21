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

class BathtubCustomFluidBucketPickupRegressionTest {

    private static final Path BATHTUB_SOURCE = Paths.get(
            "src", "main", "java", "mod", "crabmod", "showercore", "block", "BathtubBlock.java");
    private static final Pattern USE_SIG = Pattern.compile(
            "(?<![A-Za-z0-9_.])InteractionResult\\s+use\\s*\\(");

    private static String useBody;

    @BeforeAll
    static void loadSource() throws IOException {
        useBody = TestSourceUtils.extractMethodBody(
                TestSourceUtils.readSource(BATHTUB_SOURCE), USE_SIG, "BathtubBlock.use");
    }

    @Test
    @DisplayName("Empty bucket pickup has a direct branch before Forge FluidUtil")
    void emptyBucketBranchRunsBeforeFluidUtil() {
        int bucketBranch = useBody.indexOf("itemstack.getItem() == Items.BUCKET");
        int fluidUtil = useBody.indexOf("FluidUtil.interactWithFluidHandler");

        assertNotEquals(-1, bucketBranch, "BathtubBlock.use must special-case empty bucket pickup.");
        assertNotEquals(-1, fluidUtil, "BathtubBlock.use must still keep the generic FluidUtil path.");
        assertTrue(bucketBranch < fluidUtil,
                "Empty bucket pickup must run before FluidUtil. The generic Forge path cannot "
                        + "reconstruct a hotBath custom-fluid bucket from the shared dynamic FluidStack.");
    }

    @Test
    @DisplayName("Custom fluid pickup recreates the exact hotBath bucket from the saved fluid id")
    void customFluidPickupCreatesBucketFromSavedId() {
        assertTrue(Pattern.compile("ResourceLocation\\s+customId\\s*=\\s*bathtubBe\\.getCustomFluidId\\s*\\(")
                        .matcher(useBody).find(),
                "The pickup path must read BathtubBlockEntity#getCustomFluidId().");
        assertTrue(Pattern.compile("CustomFluidAPI\\s*\\.\\s*createBucket\\s*\\(\\s*customId\\s*\\)")
                        .matcher(useBody).find(),
                "Custom-fluid pickup must call CustomFluidAPI.createBucket(customId).");
    }

    @Test
    @DisplayName("Successful pickup clears both bathtub halves and liquid blockstate")
    void pickupClearsTankCustomIdOtherHalfAndState() {
        String[] required = {
                "bathtubBe.getFluidTank().setFluid(FluidStack.EMPTY)",
                "bathtubBe.setCustomFluidId(null)",
                "syncFluidToOtherPart(level, pos, state, FluidStack.EMPTY)",
                "updateLiquidState(level, pos, state, FluidStack.EMPTY)"
        };
        for (String snippet : required) {
            assertTrue(useBody.contains(snippet),
                    "Empty bucket pickup must contain '" + snippet + "'.");
        }
    }

    @Test
    @DisplayName("Successful pickup consumes the click with sidedSuccess")
    void pickupReturnsSidedSuccess() {
        int branch = useBody.indexOf("itemstack.getItem() == Items.BUCKET");
        int ret = useBody.indexOf("return InteractionResult.sidedSuccess(level.isClientSide)", branch);
        assertTrue(ret > branch,
                "Successful empty-bucket pickup must return InteractionResult.sidedSuccess(level.isClientSide) "
                        + "so vanilla BucketItem.use does not run afterward.");
    }
}
