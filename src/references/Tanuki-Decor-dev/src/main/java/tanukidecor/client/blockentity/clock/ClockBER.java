/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.client.blockentity.clock;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import tanukidecor.block.entity.ClockBlockEntity;

public class ClockBER implements BlockEntityRenderer<ClockBlockEntity> {

    public static final ResourceLocation EMPTY = ResourceLocation.fromNamespaceAndPath("minecraft", "block/air");
    public static final Vec3 ROOT_POSITION = new Vec3(8.0D / 16.0D, 8.0D / 16.0D, 0);
    public static final Vec3 ROOT_PIVOT_POINT = new Vec3(8.0D / 16.0D, 0, 8.0D / 16.0D);
    public static final Vec3 HANDS_POSITION = new Vec3(-8.0D / 16.0D, -8.0D / 16.0D, 0);

    protected final BlockRenderDispatcher blockRenderer;
    protected final ClockRenderHelper clockRenderHelper;
    protected final ModelResourceLocation shortHand;
    protected final ModelResourceLocation longHand;
    protected final Vec3 rootPosition;
    protected final Vec3 rootPivotPoint;
    protected final Vec3 handsPosition;
    protected final Vec3 handsPivotPoint;

    public ClockBER(BlockEntityRendererProvider.Context pContext,
                    ResourceLocation shortHand, ResourceLocation longHand,
                    Vec3 rootPosition, Vec3 rootPivotPoint,
                    Vec3 handsPosition, Vec3 handsPivotPoint) {
        this.shortHand = shortHand != null ? ModelResourceLocation.standalone(shortHand) : null;
        this.longHand = longHand != null ? ModelResourceLocation.standalone(longHand) : null;
        this.rootPosition = rootPosition;
        this.rootPivotPoint = rootPivotPoint;
        this.handsPosition = handsPosition;
        this.handsPivotPoint = handsPivotPoint;
        this.blockRenderer = pContext.getBlockRenderDispatcher();
        this.clockRenderHelper = new ClockRenderHelper();
    }

    @Override
    public void render(ClockBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        // prepare to render
        final Minecraft mc = Minecraft.getInstance();
        final BlockState blockState = pBlockEntity.getBlockState();
        final Direction direction = blockState.getValue(BlockStateProperties.HORIZONTAL_FACING);
        final Level level = pBlockEntity.getLevel();
        // query the day time with an offset to ensure 18000 is noon (etc)
        final long dayTime = (level.getDayTime() + 6000L) % 24000L;
        final float hourAngleInterval = Mth.TWO_PI / 12.0F;
        final float hourRotation = ClockBlockEntity.getHour(dayTime, pPartialTick) * hourAngleInterval;
        final float minuteRotation = ClockBlockEntity.getMinute(dayTime, 0.98F) * Mth.TWO_PI;

        // start rendering
        pPoseStack.pushPose();

        // prepare render helper
        this.clockRenderHelper
                .withPoseStack(pPoseStack)
                .withBlockState(blockState)
                .withRenderType(pBufferSource, RenderType.cutout())
                .withPackedLight(pPackedLight)
                .withPackedOverlay(pPackedOverlay)
                .withPartialTick(pPartialTick)
                .withRotationZ(0)
                .withRandom(level.getRandom());

        // rotate pose stack
        this.clockRenderHelper
                .withPosition(this.rootPosition)
                .withPivotPoint(this.rootPivotPoint)
                .rotateForDirection(direction);

        // render short hand
        if (this.shortHand != null) {
            this.clockRenderHelper
                    .withModel(mc.getModelManager().getModel(this.shortHand))
                    .withPosition(this.handsPosition)
                    .withPivotPoint(this.handsPivotPoint)
                    .withRotationZ(hourRotation)
                    .render(blockRenderer);
        }

        // render long hand
        if (this.longHand != null) {
            this.clockRenderHelper
                    .withModel(mc.getModelManager().getModel(this.longHand))
                    .withPosition(this.handsPosition)
                    .withPivotPoint(this.handsPivotPoint)
                    .withRotationZ(minuteRotation)
                    .render(blockRenderer);
        }
        renderAdditional(this.clockRenderHelper, pBlockEntity, pBufferSource);

        // finish rendering
        pPoseStack.popPose();
    }

    /**
     * Allows implementations to render additional models
     *
     * @param renderHelper the clock render helper
     * @param blockEntity  the block entity
     * @param bufferSource the buffer source
     */
    public void renderAdditional(ClockRenderHelper renderHelper, ClockBlockEntity blockEntity, MultiBufferSource bufferSource) {
        // do nothing
    }

    /**
     * @param speed        the pendulum speed factor
     * @param maximumAngle the maximum angle in radians
     * @param time         the current time to pass into {@link ClockBlockEntity#getSecond(long, float)}
     * @param partialTick  the partial tick
     * @return the pendulum angle in radians
     */
    public static float getPendulumRotation(final float speed, final float maximumAngle, final long time, final float partialTick) {
        return Mth.sin(ClockBlockEntity.getSecond(time % 24000L, partialTick) * speed * Mth.TWO_PI) * maximumAngle;
    }

    @Override
    public AABB getRenderBoundingBox(ClockBlockEntity blockEntity) {
        return new AABB(blockEntity.getBlockPos()).inflate(1);
    }
}