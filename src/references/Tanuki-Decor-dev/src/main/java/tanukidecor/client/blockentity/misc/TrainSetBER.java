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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.client.model.data.ModelData;
import tanukidecor.TanukiDecor;
import tanukidecor.block.entity.TrainSetBlockEntity;

import java.util.Set;

public class TrainSetBER implements BlockEntityRenderer<TrainSetBlockEntity> {

    public static final ResourceLocation TRAIN = ResourceLocation.fromNamespaceAndPath(TanukiDecor.MODID, "block/train_set/train");

    protected final BlockRenderDispatcher blockRenderer;

    public TrainSetBER(BlockEntityRendererProvider.Context pContext) {
        this.blockRenderer = pContext.getBlockRenderDispatcher();
    }

    @Override
    public void render(TrainSetBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        // prepare to render
        final Minecraft mc = Minecraft.getInstance();
        final BlockState blockState = pBlockEntity.getBlockState();
        final Direction direction = blockState.getValue(BlockStateProperties.HORIZONTAL_FACING);
        final Level level = pBlockEntity.getLevel();
        final float time = level.getGameTime() + (Mth.getSeed(pBlockEntity.getBlockPos()) % 4000L) + pPartialTick;
        final float yRot = (direction.getOpposite().toYRot()) * Mth.DEG_TO_RAD;
        final float rotation = 0.0625F * time;

        final RenderType renderType = RenderType.cutout();
        final VertexConsumer vertexConsumer = pBufferSource.getBuffer(renderType);
        final BakedModel model = mc.getModelManager().getModel(net.minecraft.client.resources.model.ModelResourceLocation.standalone(TRAIN));

        // start rendering
        pPoseStack.pushPose();

        // rotate for direction
        pPoseStack.translate(0.5D, 0, 0.5D);
        pPoseStack.mulPose(Axis.YN.rotation(yRot));
        pPoseStack.translate(-0.5D, 0, -0.5D);

        pPoseStack.translate(0, 0, 1.0D);
        pPoseStack.translate(0.5D, 0, 0.5D);
        pPoseStack.mulPose(Axis.YP.rotation(rotation));
        pPoseStack.translate(-0.5D, 0, -0.5D);
        blockRenderer.getModelRenderer().renderModel(pPoseStack.last(), vertexConsumer, blockState, model,
                1.0F, 1.0F, 1.0F, pPackedLight, pPackedOverlay, ModelData.EMPTY, renderType);

        // finish rendering
        pPoseStack.popPose();
    }

    @Override
    public AABB getRenderBoundingBox(TrainSetBlockEntity blockEntity) {
        return new AABB(blockEntity.getBlockPos()).inflate(2.0D);
    }

    public static void addSpecialModels(final Set<ResourceLocation> list) {
        list.add(TRAIN);
    }
}