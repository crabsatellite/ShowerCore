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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.neoforged.neoforge.client.model.data.ModelData;
import tanukidecor.TanukiDecor;
import tanukidecor.block.entity.PhonographBlockEntity;
import tanukidecor.block.misc.PhonographBlock;

import java.util.Set;

public class PhonographBER implements BlockEntityRenderer<PhonographBlockEntity> {

    public static final ResourceLocation DISC = ResourceLocation.fromNamespaceAndPath(TanukiDecor.MODID, "block/phonograph/disc");

    protected final BlockRenderDispatcher blockRenderer;

    public PhonographBER(BlockEntityRendererProvider.Context pContext) {
        this.blockRenderer = pContext.getBlockRenderDispatcher();
    }

    @Override
    public void render(PhonographBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        final BlockState blockState = pBlockEntity.getBlockState();
        // verify block entity has record
        if (!blockState.getValue(PhonographBlock.HAS_RECORD) || blockState.getValue(PhonographBlock.HALF) != DoubleBlockHalf.LOWER) {
            return;
        }
        // prepare to render
        final Minecraft mc = Minecraft.getInstance();
        final float time = pBlockEntity.getLevel().getGameTime() + pPartialTick;

        final RenderType renderType = RenderType.cutout();
        final VertexConsumer vertexConsumer = pBufferSource.getBuffer(renderType);
        final BakedModel model = mc.getModelManager().getModel(net.minecraft.client.resources.model.ModelResourceLocation.standalone(DISC));

        // start rendering
        pPoseStack.pushPose();

        // render disc
        double dx = 0.5D;
        double dy = 0.0D;
        double dz = 0.5D;
        float yRot = time * 0.085F;
        pPoseStack.pushPose();
        pPoseStack.translate(dx, dy, dz);
        pPoseStack.mulPose(Axis.YN.rotation(yRot));
        pPoseStack.translate(-dx, -dy, -dz);
        blockRenderer.getModelRenderer().renderModel(pPoseStack.last(), vertexConsumer, blockState, model,
                1.0F, 1.0F, 1.0F, pPackedLight, pPackedOverlay, ModelData.EMPTY, renderType);
        pPoseStack.popPose();


        // finish rendering
        pPoseStack.popPose();
    }

    public static void addSpecialModels(final Set<ResourceLocation> list) {
        list.add(DISC);
    }
}