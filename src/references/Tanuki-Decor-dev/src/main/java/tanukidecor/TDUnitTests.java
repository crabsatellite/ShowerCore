/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import tanukidecor.util.MultiblockHandler;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings("ALL")
public final class TDUnitTests {

    private static final StringBuilder debug = new StringBuilder();

    public static void run() {
        runMultiblockHandlerTests();
    }

    private static void runMultiblockHandlerTests() {
        MultiblockHandler handler = MultiblockHandler.MULTIBLOCK_3X3X3;
        Direction dir = MultiblockHandler.ORIGIN_DIRECTION;
        BlockPos center = BlockPos.ZERO;
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

        // helper method tests
        debug("Testing helper methods");
        debug("[MultiblockHandler#getCenterPos]");
        pos.set(center);
        testEquals("assert center from 0, 0, 0", center, MultiblockHandler.getCenterPos(pos, new Vec3i(0, 0, 0), dir));
        pos.setWithOffset(center, new BlockPos(1, -1, -1));
        testEquals("assert center from -1, -1, -1", center, MultiblockHandler.getCenterPos(pos, new Vec3i(-1, -1, -1), dir));
        pos.setWithOffset(center, new BlockPos(0, 0, -1));
        testEquals("assert center from 0, 0, -1", center, MultiblockHandler.getCenterPos(pos, new Vec3i(0, 0, -1), dir));
        pos.setWithOffset(center, new BlockPos(-1, 1, 0));
        testEquals("assert center from 1, 1, 0", center, MultiblockHandler.getCenterPos(pos, new Vec3i(1, 1, 0), dir));

        // 3x3x3 tests
        debug("============================");
        debug("Testing MultiblockHandler 3x3x3");
        debug("dimensions=" + handler.getDimensions());
        debug("bounds=" + handler.getBounds(dir));
        debug("boundsCenterPos=" + handler.getBounds(dir).getCenter());
        debug("[properties]");
        testEquals("assert width = [0,2]", handler.getWidthProperty(), MultiblockHandler.getWidthProperty(3));
        testEquals("assert height = [0,2]", handler.getHeightProperty(), MultiblockHandler.getHeightProperty(3));
        testEquals("assert depth = [0,2]", handler.getDepthProperty(), MultiblockHandler.getDepthProperty(3));
        debug("[index]");
        testEquals("assert min index is -1, -1, -1", handler.getMinIndex(), new Vec3i(-1, -1, -1));
        testEquals("assert max index is 1, 1, 1", handler.getMaxIndex(), new Vec3i(1, 1, 1));
        debug("[getBounds]");
        testEquals("assert min is (1, -1, -1)", handler.getMin(center, dir), new BlockPos(1, -1, -1));
        testEquals("assert max is (-1, 1, 1)", handler.getMax(center, dir), new BlockPos(-1, 1, 1));
        AtomicInteger i = new AtomicInteger();
        i.set(0);
        handler.getPositions(center, dir).forEach(s -> i.getAndAdd(1));
        testEquals("assert 27 positions", i.get(), 27);

        // 3x3x1 tests
        handler = MultiblockHandler.MULTIBLOCK_3X3X1;
        debug("============================");
        debug("Testing MultiblockHandler 3x3x1");
        debug("dimensions=" + handler.getDimensions());
        debug("bounds=" + handler.getBounds(dir));
        debug("boundsCenterPos=" + handler.getBounds(dir).getCenter());
        debug("[properties]");
        testEquals("assert width = [0,2]", handler.getWidthProperty(), MultiblockHandler.getWidthProperty(3));
        testEquals("assert height = [0,2]", handler.getHeightProperty(), MultiblockHandler.getHeightProperty(3));
        testEquals("assert depth = null", handler.getDepthProperty(), MultiblockHandler.getDepthProperty(1));
        debug("[index]");
        testEquals("assert min index is -1, -1, 0", handler.getMinIndex(), new Vec3i(-1, -1, 0));
        testEquals("assert max index is 1, 1, 0", handler.getMaxIndex(), new Vec3i(1, 1, 0));
        debug("[getBounds]");
        testEquals("assert min is (1, -1, 0)", handler.getMin(center, dir), new BlockPos(1, -1, 0));
        testEquals("assert max is (-1, 1, 0)", handler.getMax(center, dir), new BlockPos(-1, 1, 0));
        i.set(0);
        handler.getPositions(center, dir).forEach(s -> i.getAndAdd(1));
        testEquals("assert 9 positions", i.get(), 9);

        // 2x3x1 tests
        handler = MultiblockHandler.MULTIBLOCK_2X3X1;
        debug("============================");
        debug("Testing MultiblockHandler 2x3x1");
        debug("dimensions=" + handler.getDimensions());
        debug("bounds=" + handler.getBounds(dir));
        debug("boundsCenterPos=" + handler.getBounds(dir).getCenter());
        debug("[properties]");
        testEquals("assert width = [0,1]", handler.getWidthProperty(), MultiblockHandler.getWidthProperty(2));
        testEquals("assert height = [0,2]", handler.getHeightProperty(), MultiblockHandler.getHeightProperty(3));
        testEquals("assert depth = null", handler.getDepthProperty(), MultiblockHandler.getDepthProperty(1));
        debug("[index]");
        testEquals("assert min index is 0, -1, 0", handler.getMinIndex(), new Vec3i(0, -1, 0));
        testEquals("assert max index is 1, 1, 0", handler.getMaxIndex(), new Vec3i(1, 1, 0));
        debug("[getBounds]");
        testEquals("assert min is (0, -1, 0)", handler.getMin(center, dir), new BlockPos(0, -1, 0));
        testEquals("assert max is (-1, 1, 0)", handler.getMax(center, dir), new BlockPos(-1, 1, 0));
        i.set(0);
        handler.getPositions(center, dir).forEach(s -> i.getAndAdd(1));
        testEquals("assert 6 positions", i.get(), 6);

        TanukiDecor.LOGGER.debug(debug.toString());
    }

    private static void debug(final String message) {
        debug.append(message).append("\n");
    }

    private static boolean testEquals(final String message, final Object o1, final Object o2) {
        if (Objects.equals(o1, o2)) {
            debug("  PASS " + message + " " + (o1 != null ? o1 : "null") + " = " + (o2 != null ? o2 : "null"));
            return true;
        } else {
            debug("  FAIL " + message + " " + (o1 != null ? o1 : "null") + " = " + (o2 != null ? o2 : "null"));
            return false;
        }
    }
}
