package mod.crabmod.showercore.gametest;

import mod.crabmod.showercore.ShowerCore;
import mod.crabmod.showercore.block.BathtubBlock;
import mod.crabmod.showercore.entity.FaucetInteractionEntity;
import mod.crabmod.showercore.registers.BlocksRegister;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestAssertException;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

import java.util.List;

@GameTestHolder(ShowerCore.MODID)
@PrefixGameTestTemplate(false)
public final class ShowerCoreGameTests {
    private static final String EMPTY_TEMPLATE = "empty";
    private static final BlockPos BATHTUB_FOOT_POS = new BlockPos(2, 1, 2);

    private ShowerCoreGameTests() {
    }

    @GameTest(template = EMPTY_TEMPLATE, timeoutTicks = 40)
    public static void clawfoot_bathtub_places_three_head_bound_faucet_targets(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        Direction facing = Direction.NORTH;
        BlockPos footPos = helper.absolutePos(BATHTUB_FOOT_POS);
        BlockPos headPos = footPos.relative(facing);
        BlockState footState = BlocksRegister.BATHTUB_CLAWFOOT_WHITE.get().defaultBlockState()
                .setValue(BathtubBlock.FACING, facing)
                .setValue(BathtubBlock.PART, BedPart.FOOT)
                .setValue(BathtubBlock.LIQUID, BathtubBlock.LiquidType.WATER)
                .setValue(BathtubBlock.RUNNING, false);

        level.setBlock(footPos, footState, 3);
        ((BathtubBlock) footState.getBlock()).setPlacedBy(level, footPos, footState, null, ItemStack.EMPTY);

        AABB searchBox = new AABB(headPos).inflate(1.0D).expandTowards(0.0D, 1.0D, 0.0D);
        List<FaucetInteractionEntity> faucets = level.getEntitiesOfClass(FaucetInteractionEntity.class, searchBox);
        assertEquals(3, faucets.size(), "clawfoot faucet target count");
        for (FaucetInteractionEntity faucet : faucets) {
            assertEquals(headPos, faucet.blockPosition(), "clawfoot faucet target block position");
        }
        helper.succeed();
    }

    private static void assertEquals(Object expected, Object actual, String message) {
        if (!expected.equals(actual)) {
            throw new GameTestAssertException(message + ": expected " + expected + ", got " + actual);
        }
    }
}
