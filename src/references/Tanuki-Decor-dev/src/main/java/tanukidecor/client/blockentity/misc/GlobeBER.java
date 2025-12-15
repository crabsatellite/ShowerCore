/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.client.blockentity.misc;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import tanukidecor.TanukiDecor;
import tanukidecor.block.RotatingTallBlock;
import tanukidecor.block.entity.GlobeBlockEntity;

import java.util.Set;

public class GlobeBER implements BlockEntityRenderer<GlobeBlockEntity> {

    public static final ResourceLocation GLOBE_MODEL = ResourceLocation.fromNamespaceAndPath(TanukiDecor.MODID, "block/globe/globe_model");

    protected final BlockRenderDispatcher blockRenderer;

    public GlobeBER(BlockEntityRendererProvider.Context pContext) {
        this.blockRenderer = pContext.getBlockRenderDispatcher();
    }

    @Override
    public void render(GlobeBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        final BlockState blockState = pBlockEntity.getBlockState();

        final float duration = pBlockEntity.getUsePercentage(pPartialTick);
        final Direction direction = blockState.getValue(RotatingTallBlock.FACING);
        final Direction targetDirection = pBlockEntity.getTargetDirection();

        // prepare to render
        final Minecraft mc = Minecraft.getInstance();

        final RenderType renderType = RenderType.cutout();
        final VertexConsumer vertexConsumer = pBufferSource.getBuffer(renderType);
        final BakedModel globeModel = mc.getModelManager().getModel(net.minecraft.client.resources.model.ModelResourceLocation.standalone(GLOBE_MODEL));

        double dx = 0.5D;
        double dy = 0.5D;
        double dz = 0.5D;
        float yRot = (direction.getOpposite().toYRot()) * Mth.DEG_TO_RAD;

        // start rendering
        pPoseStack.pushPose();

        pPoseStack.translate(dx, dy, dz);
        pPoseStack.mulPose(Axis.YN.rotation(yRot));
        pPoseStack.translate(-dx, -dy, -dz);

        // render globe model
        yRot = targetDirection.toYRot();
        if (pBlockEntity.isActive() && duration < 0.98F) {
            yRot += (100) * Math.pow(8.0D, -5.0D * (duration - 0.5F));
        }
        pPoseStack.translate(dx, dy, dz);
        pPoseStack.mulPose(Axis.YN.rotationDegrees(yRot));
        pPoseStack.translate(-dx, -dy, -dz);
        blockRenderer.getModelRenderer().renderModel(pPoseStack.last(), vertexConsumer, blockState, globeModel,
                1.0F, 1.0F, 1.0F, pPackedLight, pPackedOverlay, ModelData.EMPTY, renderType);

        // finish rendering
        pPoseStack.popPose();
    }

    public static void addSpecialModels(final Set<ResourceLocation> set) {
        set.add(GLOBE_MODEL);
    }
}