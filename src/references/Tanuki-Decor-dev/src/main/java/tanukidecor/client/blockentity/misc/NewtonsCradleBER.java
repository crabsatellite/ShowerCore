/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.client.blockentity.misc;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import tanukidecor.TanukiDecor;
import tanukidecor.block.clock.IChimeProvider;
import tanukidecor.block.entity.NewtonsCradleBlockEntity;
import tanukidecor.block.misc.NewtonsCradleBlock;

import java.util.Set;

public class NewtonsCradleBER implements BlockEntityRenderer<NewtonsCradleBlockEntity> {

    public static final ResourceLocation BALL = ResourceLocation.fromNamespaceAndPath(TanukiDecor.MODID, "block/newtons_cradle/ball");
    protected static final Vector3f LINE_START = new Vector3f(4.0F / 16.0F, 8.0F / 16.0F, 5.0F / 16.0F);
    protected static final Vector3f LINE_MID = new Vector3f(4.0F / 16.0F, 4.0F / 16.0F, 8.0F / 16.0F);
    protected static final Vector3f LINE_END = new Vector3f(4.0F / 16.0F, 8.0F / 16.0F, 11.0F / 16.0F);

    protected final BlockRenderDispatcher blockRenderer;

    public NewtonsCradleBER(BlockEntityRendererProvider.Context pContext) {
        this.blockRenderer = pContext.getBlockRenderDispatcher();
    }

    @Override
    public void render(NewtonsCradleBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        // load values
        final BlockState blockState = pBlockEntity.getBlockState();
        final Direction direction = blockState.getValue(BlockStateProperties.HORIZONTAL_FACING);
        final float time = (int) (pBlockEntity.getLevel().getGameTime() % 24000L) + pPartialTick;
        final IChimeProvider chimeProvider = (IChimeProvider) pBlockEntity.getBlockState().getBlock();

        // load baked models
        final Minecraft mc = Minecraft.getInstance();
        final BakedModel ballModel = mc.getModelManager().getModel(net.minecraft.client.resources.model.ModelResourceLocation.standalone(BALL));

        final float timeFactor = Mth.PI / chimeProvider.getTickSoundInterval(blockState);
        final float maxAngle = 40.0F * Mth.DEG_TO_RAD;
        final float bias = Mth.HALF_PI * Mth.sign(pBlockEntity.getBias());
        final float enabledMultiplier = blockState.getValue(NewtonsCradleBlock.ENABLED) ? 1.0F : 0.0F;
        final float angle = enabledMultiplier * maxAngle * Mth.sin(time * timeFactor + bias);
        final float westBallAngle = Math.max(0, angle);
        final float eastBallAngle = -Math.min(0, angle);

        final RenderType renderType = RenderType.solid();
        final VertexConsumer modelVertexConsumer = pBufferSource.getBuffer(renderType);

        float dx = 0.5F;
        float dy = 0.5F;
        float dz = 0.5F;
        float zRotation = 0;
        // start rendering
        pPoseStack.pushPose();
        pPoseStack.translate(dx, dy, dz);
        pPoseStack.mulPose(Axis.YP.rotationDegrees(direction.toYRot()));
        pPoseStack.translate(-dx, -dy, -dz);

        // render west ball
        dx = 8.0F / 16.0F;
        dy = 8.0F / 16.0F;
        dz = 7.5F / 16.0F;
        zRotation = westBallAngle;
        pPoseStack.pushPose();
        pPoseStack.translate(4.0F / 16.0F, 0.0F / 16.0F, 0.0F / 16.0F);
        pPoseStack.translate(dx, dy, dz);
        pPoseStack.mulPose(Axis.ZP.rotation(zRotation));
        pPoseStack.translate(-dx, -dy, -dz);
        blockRenderer.getModelRenderer().renderModel(pPoseStack.last(), modelVertexConsumer, blockState, ballModel,
                1.0F, 1.0F, 1.0F, pPackedLight, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, renderType);
        pPoseStack.popPose();

        // render east ball
        zRotation = eastBallAngle;
        pPoseStack.pushPose();
        pPoseStack.translate(-4.0F / 16.0F, 0.0F / 16.0F, 0.0F / 16.0F);
        pPoseStack.translate(dx, dy, dz);
        pPoseStack.mulPose(Axis.ZN.rotation(zRotation));
        pPoseStack.translate(-dx, -dy, -dz);
        blockRenderer.getModelRenderer().renderModel(pPoseStack.last(), modelVertexConsumer, blockState, ballModel,
                1.0F, 1.0F, 1.0F, pPackedLight, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, renderType);
        pPoseStack.popPose();

        // prepare to render lines
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        final VertexConsumer lineVertexConsumer = pBufferSource.getBuffer(RenderType.LINES);

        // render stationary lines
        for (int i = 0; i < 3; i++) {
            float startX = LINE_START.x() + (i + 1) * (2.0F / 16.0F);
            cradleLineStrip(pPoseStack, lineVertexConsumer,
                    startX, LINE_START.y(), LINE_START.z(),
                    startX, LINE_MID.y(), LINE_MID.z(),
                    startX, LINE_END.y(), LINE_END.z());
        }
        // render west lines
        float radius = 4.0F / 16.0F;
        float startX = LINE_START.x() + 4 * (2.0F / 16.0F);
        float midX = startX + radius * Mth.sin(westBallAngle);
        float midY = LINE_MID.y() + radius - radius * Mth.cos(westBallAngle);
        cradleLineStrip(pPoseStack, lineVertexConsumer,
                startX, LINE_START.y(), LINE_START.z(),
                midX, midY, LINE_MID.z(),
                startX, LINE_END.y(), LINE_END.z());

        // render east lines
        startX = LINE_START.x();
        midX = startX - radius * Mth.sin(eastBallAngle);
        midY = LINE_MID.y() + radius - radius * Mth.cos(eastBallAngle);
        cradleLineStrip(pPoseStack, lineVertexConsumer,
                startX, LINE_START.y(), LINE_START.z(),
                midX, midY, LINE_MID.z(),
                startX, LINE_END.y(), LINE_END.z());

        // finish rendering
        pPoseStack.popPose();
    }

    public static void addSpecialModels(final Set<ResourceLocation> list) {
        list.add(BALL);
    }

    private static void cradleLineStrip(PoseStack poseStack, VertexConsumer vertexConsumer,
                                        float startX, float startY, float startZ,
                                        float midX, float midY, float midZ,
                                        float endX, float endY, float endZ) {
        final PoseStack.Pose lastPose = poseStack.last();
        final Matrix4f matrix4f = lastPose.pose();

        vertexConsumer.addVertex(matrix4f, startX, startY, startZ)
                .setColor(10, 10, 10, 255)
                .setNormal(lastPose, 0.0F, 1.0F, 0.0F);
        vertexConsumer.addVertex(matrix4f, midX, midY, midZ)
                .setColor(10, 10, 10, 255)
                .setNormal(lastPose, 0.0F, 1.0F, 0.0F);

        vertexConsumer.addVertex(matrix4f, midX, midY, midZ)
                .setColor(10, 10, 10, 255)
                .setNormal(lastPose, 0.0F, 1.0F, 0.0F);
        vertexConsumer.addVertex(matrix4f, endX, endY, endZ)
                .setColor(10, 10, 10, 255)
                .setNormal(lastPose, 0.0F, 1.0F, 0.0F);
    }
}
