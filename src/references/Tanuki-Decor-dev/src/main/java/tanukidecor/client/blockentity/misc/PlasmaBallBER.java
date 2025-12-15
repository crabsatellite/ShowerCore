/*
 * Copyright (c) 2024 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.client.blockentity.misc;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import tanukidecor.block.entity.PlasmaBallBlockEntity;
import tanukidecor.block.misc.PlasmaBallBlock;

public class PlasmaBallBER implements BlockEntityRenderer<PlasmaBallBlockEntity> {

    private static final Vector4f ONE = new Vector4f(1.0F, 1.0F, 1.0F, 1.0F);
    private static final Vector3f HALF = new Vector3f(0.5F, 0.5F, 0.5F);

    public PlasmaBallBER(BlockEntityRendererProvider.Context pContext) {
    }

    @Override
    public void render(PlasmaBallBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        // validate arcs can render
        final BlockState blockState = blockEntity.getBlockState();
        if (!blockState.getValue(PlasmaBallBlock.ENABLED)) {
            return;
        }
        // prepare to render
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        final int segments = 6;
        final float size = 1.0F / 16.0F;
        final float mul = 1.0F - size * 2;
        long time = blockEntity.getLevel().getGameTime() % 24000L;

        // render each arc
        for (PlasmaBallBlockEntity.Arc arc : blockEntity.getArcs()) {
            // increase time for randomness
            time += 10;
            // determine start position
            Vector3f start = new Vector3f();
            Vector3f end = new Vector3f();

            // determine start position
            start = arc.getPosition(0, time, partialTick);
            start.mul(mul);
            start.add(HALF);
            // render each segment
            for (int i = 0; i < segments; i++) {
                float startPercent = (float) i / (float) (segments);
                float endPercent = (float) (i + 1) / (float) (segments);
                // determine end position
                end = arc.getPosition(endPercent, time, partialTick);
                end.mul(mul);
                end.add(HALF);
                // render segment
                renderRect(poseStack, arc.getColor(startPercent), arc.getColor(endPercent), start, end, size);
                // update start position for next segment
                start.set(end.x(), end.y(), end.z());
            }
        }

        RenderSystem.disableBlend();
    }

    protected static void renderRect(final PoseStack poseStack, final Vector4f startColor, final Vector4f endColor,
                                     final Vector3f start, final Vector3f end, final float size) {
        final float dsize = size / 2.0F;
        PoseStack.Pose lastPose = poseStack.last();
        Matrix4f matrix4f = lastPose.pose();
        Tesselator tesselator = Tesselator.getInstance();

        // draw front side
        BufferBuilder buffer = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        buffer.addVertex(matrix4f, start.x() + dsize, start.y() - dsize, start.z() + dsize)
                .setColor(startColor.x(), startColor.y(), startColor.z(), startColor.w());
        buffer.addVertex(matrix4f, start.x() + dsize, start.y() + dsize, start.z() + dsize)
                .setColor(startColor.x(), startColor.y(), startColor.z(), startColor.w());
        buffer.addVertex(matrix4f, end.x() + dsize, end.y() + dsize, end.z() + dsize)
                .setColor(endColor.x(), endColor.y(), endColor.z(), endColor.w());
        buffer.addVertex(matrix4f, end.x() + dsize, end.y() - dsize, end.z() + dsize)
                .setColor(endColor.x(), endColor.y(), endColor.z(), endColor.w());
        BufferUploader.drawWithShader(buffer.buildOrThrow());

        // draw back side
        buffer = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        buffer.addVertex(matrix4f, end.x() - dsize, end.y() - dsize, end.z() - dsize)
                .setColor(endColor.x(), endColor.y(), endColor.z(), endColor.w());
        buffer.addVertex(matrix4f, end.x() - dsize, end.y() + dsize, end.z() - dsize)
                .setColor(endColor.x(), endColor.y(), endColor.z(), endColor.w());
        buffer.addVertex(matrix4f, start.x() - dsize, start.y() + dsize, start.z() - dsize)
                .setColor(startColor.x(), startColor.y(), startColor.z(), startColor.w());
        buffer.addVertex(matrix4f, start.x() - dsize, start.y() - dsize, start.z() - dsize)
                .setColor(startColor.x(), startColor.y(), startColor.z(), startColor.w());
        BufferUploader.drawWithShader(buffer.buildOrThrow());

        // draw bottom side
        buffer = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        buffer.addVertex(matrix4f, start.x() - dsize, start.y() - dsize, start.z() - dsize)
                .setColor(startColor.x(), startColor.y(), startColor.z(), startColor.w());
        buffer.addVertex(matrix4f, start.x() + dsize, start.y() - dsize, start.z() + dsize)
                .setColor(startColor.x(), startColor.y(), startColor.z(), startColor.w());
        buffer.addVertex(matrix4f, end.x() + dsize, end.y() - dsize, end.z() + dsize)
                .setColor(endColor.x(), endColor.y(), endColor.z(), endColor.w());
        buffer.addVertex(matrix4f, end.x() - dsize, end.y() - dsize, end.z() - dsize)
                .setColor(endColor.x(), endColor.y(), endColor.z(), endColor.w());
        BufferUploader.drawWithShader(buffer.buildOrThrow());

        // draw top side
        buffer = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        buffer.addVertex(matrix4f, end.x() - dsize, end.y() + dsize, end.z() - dsize)
                .setColor(endColor.x(), endColor.y(), endColor.z(), endColor.w());
        buffer.addVertex(matrix4f, end.x() + dsize, end.y() + dsize, end.z() + dsize)
                .setColor(endColor.x(), endColor.y(), endColor.z(), endColor.w());
        buffer.addVertex(matrix4f, start.x() + dsize, start.y() + dsize, start.z() + dsize)
                .setColor(startColor.x(), startColor.y(), startColor.z(), startColor.w());
        buffer.addVertex(matrix4f, start.x() - dsize, start.y() + dsize, start.z() - dsize)
                .setColor(startColor.x(), startColor.y(), startColor.z(), startColor.w());
        BufferUploader.drawWithShader(buffer.buildOrThrow());
    }
}
