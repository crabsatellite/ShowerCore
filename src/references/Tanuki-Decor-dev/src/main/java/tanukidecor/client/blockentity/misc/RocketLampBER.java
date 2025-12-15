/*
 * Copyright (c) 2024 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.client.blockentity.misc;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
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
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.client.model.data.ModelData;
import tanukidecor.TanukiDecor;
import tanukidecor.block.entity.RocketLampBlockEntity;
import tanukidecor.block.misc.RocketLampBlock;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class RocketLampBER implements BlockEntityRenderer<RocketLampBlockEntity> {

    private final BlockRenderDispatcher blockRenderer;

    private static final Map<String, Map<WaxSize, ResourceLocation>> WAX_MODELS = new HashMap<>();

    public RocketLampBER(BlockEntityRendererProvider.Context pContext) {
        this.blockRenderer = pContext.getBlockRenderDispatcher();
    }

    protected static ResourceLocation getWaxModel(final WaxSize waxSize, final String color) {
        return WAX_MODELS
                .computeIfAbsent(color, s -> new EnumMap<>(WaxSize.class))
                .computeIfAbsent(waxSize, size -> ResourceLocation.fromNamespaceAndPath(TanukiDecor.MODID, "block/rocket_lamp/" + color + "/" + size.getSerializedName() + "_wax"));
    }

    @Override
    public void render(RocketLampBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {

        final BlockState blockState = blockEntity.getBlockState();
        final String color = blockEntity.getColor();
        final long seed = blockState.getSeed(blockEntity.getBlockPos());
        final float time = ((blockEntity.getLevel().getGameTime() + seed) % 96_000L) + partialTick;
        final Direction direction = Direction.from2DDataValue((int) (Math.abs(seed) % 4));

        // load baked models
        final Minecraft mc = Minecraft.getInstance();
        final RenderType renderType = RenderType.solid();
        final VertexConsumer vertexConsumer = bufferSource.getBuffer(renderType);
        BakedModel largeModel = mc.getModelManager().getModel(net.minecraft.client.resources.model.ModelResourceLocation.standalone(getWaxModel(WaxSize.LARGE, color)));
        BakedModel mediumModel = mc.getModelManager().getModel(net.minecraft.client.resources.model.ModelResourceLocation.standalone(getWaxModel(WaxSize.MEDIUM, color)));
        BakedModel smallModel = mc.getModelManager().getModel(net.minecraft.client.resources.model.ModelResourceLocation.standalone(getWaxModel(WaxSize.SMALL, color)));

        poseStack.pushPose();
        poseStack.translate(0.5F, 0.5F, 0.5F);
        poseStack.mulPose(Axis.YP.rotationDegrees(direction.toYRot()));
        poseStack.translate(-0.5F, -0.5F, -0.5F);

        renderWax(poseStack, vertexConsumer, blockState, largeModel, 3.0F, 0.6F, 0.4F, time, 0.51F, 0.245F, 0.0163F);
        renderWax(poseStack, vertexConsumer, blockState, largeModel, 3.0F, 0.475F, 0.58F, time + 773, 0.48F, 0.132F, 0.0115F);
        renderWax(poseStack, vertexConsumer, blockState, mediumModel, 2.0F, 0.64F, 0.59F, time + 559, 0.49F, 0.02F, 0.0134F);
        renderWax(poseStack, vertexConsumer, blockState, mediumModel, 2.0F, 0.5F, 0.4F, time + 79, 0.446F, 0.09F, 0.0194F);
        renderWax(poseStack, vertexConsumer, blockState, smallModel, 1.0F, 0.4F, 0.65F, time + 109, 0.39F, 0.111F, 0.0212F);

        poseStack.popPose();
    }

    /**
     * Renders the given baked model with transformations applied according to the given parameters
     *
     * @param poseStack       the pose stack
     * @param vertexConsumer  the vertex consumer
     * @param blockState      the block state
     * @param model           the baked model
     * @param sizeY           the height of the baked model
     * @param dx              the x offset
     * @param dz              the z offset
     * @param time            the current time. For best results, this value should be less than 10430
     * @param biasFactor      a number from 0 to 1 that determines which end of the height range to tend towards
     * @param amplitudeFactor a number greater than 0 that determines how much time is spent at the end of the height range
     * @param frequencyFactor a number greater than 0 that determines how quickly each cycle is completed
     */
    private void renderWax(final PoseStack poseStack, final VertexConsumer vertexConsumer, final BlockState blockState,
                           final BakedModel model, final float sizeY, final float dx, final float dz,
                           final float time, final float biasFactor, final float amplitudeFactor, final float frequencyFactor) {
        // calculate start position
        final float y = ((sizeY / 2.0F) - 8.0F) / 16.0F;
        // calculate height range
        final float range = (16.0F - sizeY) / 16.0F;
        // calculate y position
        final float dy = y + range * smoothstep(biasFactor + (0.5F + amplitudeFactor) * Mth.sin(time * frequencyFactor));
        // calculate scale
        float scaleFactor = 0.34F + 0.31F * (float) Mth.smoothstep(Mth.sin(time * 0.053F) * 0.9F + 0.5F);
        scaleFactor *= (0.5F + 0.5F * Math.abs(Mth.cos(time * frequencyFactor)));
        final float scaleX = 0.4F + 0.6F * (1.0F - scaleFactor);
        final float scaleY = 0.8F + 0.5F * scaleFactor;
        final float scaleZ = 0.4F + 0.6F * (1.0F - scaleFactor);
        poseStack.pushPose();
        poseStack.translate(dx, dy, dz);
        poseStack.scale(scaleX, scaleY, scaleZ);
        blockRenderer.getModelRenderer().renderModel(poseStack.last(), vertexConsumer, blockState, model,
                1.0F, 1.0F, 1.0F, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, RenderType.solid());
        poseStack.popPose();
    }

    /**
     * @param f a value, usually in the range [0,1]
     * @return the smoothed value between 0 and 1
     */
    private static float smoothstep(float f) {
        f = Mth.clamp(f, 0.0F, 1.0F);
        return f * f * (3.0F - 2.0F * f);
    }

    public static void addSpecialModels(final Set<ResourceLocation> set) {
        for (String color : RocketLampBlock.getColors().keySet()) {
            for (WaxSize size : WaxSize.values()) {
                set.add(getWaxModel(size, color));
            }
        }
    }

    /// / CLASSES ////

    private enum WaxSize implements StringRepresentable {
        SMALL("small"),
        MEDIUM("medium"),
        LARGE("large");

        private final String name;

        WaxSize(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }

    @Override
    public AABB getRenderBoundingBox(RocketLampBlockEntity blockEntity) {
        return new AABB(blockEntity.getBlockPos()).inflate(0.5F, 1.0F, 0.5F);
    }
}
