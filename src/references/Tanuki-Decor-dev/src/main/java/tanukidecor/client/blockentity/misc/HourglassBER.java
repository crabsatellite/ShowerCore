/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.client.blockentity.misc;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import tanukidecor.TanukiDecor;
import tanukidecor.block.entity.HourglassBlockEntity;

import java.util.Set;

public class HourglassBER implements BlockEntityRenderer<HourglassBlockEntity> {

    // TODO possible improvement: make 5 "one pixel tall" layers that gradually shrink until they disappear on top and appear on bottom
    public static final ResourceLocation SAND = ResourceLocation.fromNamespaceAndPath(TanukiDecor.MODID, "block/hourglass/sand");

    protected final BlockRenderDispatcher blockRenderer;

    public HourglassBER(BlockEntityRendererProvider.Context pContext) {
        this.blockRenderer = pContext.getBlockRenderDispatcher();
    }

    @Override
    public void render(HourglassBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        // prepare to render
        final Minecraft mc = Minecraft.getInstance();
        final BlockState blockState = pBlockEntity.getBlockState();
        final float percentage = pBlockEntity.getPercentageComplete(pPartialTick);
        double dy = 9.5D / 16.0D;
        float scale;

        final RenderType renderType = RenderType.solid();
        final VertexConsumer vertexConsumer = pBufferSource.getBuffer(renderType);
        final BakedModel model = mc.getModelManager().getModel(net.minecraft.client.resources.model.ModelResourceLocation.standalone(SAND));

        // start rendering
        pPoseStack.pushPose();


        if (pBlockEntity.isActive()) {
            // render top
            scale = 1.0F - percentage;
            pPoseStack.pushPose();
            pPoseStack.translate(0, dy, 0);
            pPoseStack.scale(1.0F, scale, 1.0F);
            pPoseStack.translate(0, -dy, 0);
            blockRenderer.getModelRenderer().renderModel(pPoseStack.last(), vertexConsumer, blockState, model,
                    1.0F, 1.0F, 1.0F, pPackedLight, pPackedOverlay, ModelData.EMPTY, renderType);
            pPoseStack.popPose();
        }

        // render bottom
        scale = percentage;
        pPoseStack.pushPose();
        pPoseStack.translate(0, dy, 0);
        pPoseStack.scale(1.0F, scale, 1.0F);
        pPoseStack.translate(0, -dy, 0);
        pPoseStack.translate(0, (-7.51D / 16.0D) / scale, 0);
        blockRenderer.getModelRenderer().renderModel(pPoseStack.last(), vertexConsumer, blockState, model,
                1.0F, 1.0F, 1.0F, pPackedLight, pPackedOverlay, ModelData.EMPTY, renderType);
        pPoseStack.popPose();


        // finish rendering
        pPoseStack.popPose();
    }

    public static void addSpecialModels(final Set<ResourceLocation> list) {
        list.add(SAND);
    }
}