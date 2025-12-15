/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.client.blockentity.clock;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.model.data.ModelData;

public class ClockRenderHelper {
    private RandomSource random;
    private PoseStack poseStack;
    private RenderType renderType;
    private VertexConsumer vertexConsumer;
    private BlockState blockState;
    private float partialTick;
    private int packedLight;
    private int packedOverlay;
    private BakedModel model;
    private Vec3 pivotPoint;
    private Vec3 position;
    private float rotZ;

    /// / CONSTRUCTOR ////

    public ClockRenderHelper() {
        this.pivotPoint = Vec3.ZERO;
        this.position = Vec3.ZERO;
    }

    /// / METHODS ////

    public void rotateForDirection(final Direction direction) {
        final float yRot = (direction.getOpposite().toYRot()) * Mth.DEG_TO_RAD;
        poseStack.translate(pivotPoint.x(), pivotPoint.y(), pivotPoint.z());
        poseStack.mulPose(Axis.YN.rotation(yRot));
        poseStack.translate(-pivotPoint.x(), -pivotPoint.y(), -pivotPoint.z());
        poseStack.translate(position.x(), position.y(), position.z());
    }

    public void render(BlockRenderDispatcher blockRenderer) {
        if (null == model) {
            return;
        }
        // render the model
        poseStack.pushPose();
        poseStack.translate(position.x(), position.y(), position.z());
        poseStack.translate(pivotPoint.x(), pivotPoint.y(), pivotPoint.z());
        poseStack.mulPose(Axis.ZP.rotation(rotZ));
        poseStack.translate(-pivotPoint.x(), -pivotPoint.y(), -pivotPoint.z());
        for (RenderType renderType : model.getRenderTypes(blockState, random, ModelData.EMPTY)) {
            blockRenderer.getModelRenderer().renderModel(poseStack.last(), vertexConsumer, blockState, model,
                    1.0F, 1.0F, 1.0F, packedLight, packedOverlay, ModelData.EMPTY, renderType);
        }
        poseStack.popPose();
    }

    /// / SETTERS ////

    public ClockRenderHelper withPoseStack(final PoseStack poseStack) {
        this.poseStack = poseStack;
        return this;
    }

    public ClockRenderHelper withRandom(final RandomSource random) {
        this.random = random;
        return this;
    }

    public ClockRenderHelper withBlockState(final BlockState blockState) {
        this.blockState = blockState;
        return this;
    }

    public ClockRenderHelper withModel(final BakedModel bakedModel) {
        this.model = bakedModel;
        return this;
    }

    public ClockRenderHelper withRenderType(final MultiBufferSource bufferSource, final RenderType renderType) {
        this.renderType = renderType;
        this.vertexConsumer = bufferSource.getBuffer(renderType);
        return this;
    }

    public ClockRenderHelper withPackedLight(final int packedLight) {
        this.packedLight = packedLight;
        return this;
    }

    public ClockRenderHelper withPackedOverlay(final int packedOverlay) {
        this.packedOverlay = packedOverlay;
        return this;
    }

    public ClockRenderHelper withPivotPoint(final Vec3 pivotPoint) {
        this.pivotPoint = pivotPoint;
        return this;
    }

    public ClockRenderHelper withPosition(final Vec3 position) {
        this.position = position;
        return this;
    }

    public ClockRenderHelper withPartialTick(final float partialTick) {
        this.partialTick = partialTick;
        return this;
    }

    public ClockRenderHelper withRotationZ(final float zRotation) {
        this.rotZ = zRotation;
        return this;
    }

    /// / GETTERS ////

    public PoseStack getPoseStack() {
        return poseStack;
    }

    public RenderType getRenderType() {
        return renderType;
    }

    public VertexConsumer getVertexConsumer() {
        return vertexConsumer;
    }

    public BlockState getBlockState() {
        return blockState;
    }

    public int getPackedLight() {
        return packedLight;
    }

    public int getPackedOverlay() {
        return packedOverlay;
    }

    public BakedModel getModel() {
        return model;
    }

    public Vec3 getPivotPoint() {
        return pivotPoint;
    }

    public Vec3 getPosition() {
        return position;
    }

    public float getPartialTick() {
        return partialTick;
    }

    public float getRotationZ() {
        return rotZ;
    }
}
