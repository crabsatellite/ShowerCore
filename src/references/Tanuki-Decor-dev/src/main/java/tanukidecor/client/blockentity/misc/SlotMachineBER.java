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
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.client.model.data.ModelData;
import tanukidecor.TanukiDecor;
import tanukidecor.block.RotatingTallBlock;
import tanukidecor.block.entity.SlotMachineBlockEntity;

import java.util.Set;

public class SlotMachineBER implements BlockEntityRenderer<SlotMachineBlockEntity> {

    public static final ResourceLocation SLOT = ResourceLocation.fromNamespaceAndPath(TanukiDecor.MODID, "block/slot_machine/slot");
    public static final ResourceLocation LEVER = ResourceLocation.fromNamespaceAndPath(TanukiDecor.MODID, "block/slot_machine/lever");

    protected final BlockRenderDispatcher blockRenderer;

    public SlotMachineBER(BlockEntityRendererProvider.Context pContext) {
        this.blockRenderer = pContext.getBlockRenderDispatcher();
    }

    public static void addSpecialModels(final Set<ResourceLocation> list) {
        list.add(SLOT);
        list.add(LEVER);
    }

    @Override
    public void render(SlotMachineBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        final BlockState blockState = pBlockEntity.getBlockState();
        // verify block entity
        if (blockState.getValue(RotatingTallBlock.HALF) != DoubleBlockHalf.UPPER) {
            return;
        }
        final float duration = pBlockEntity.getUsePercentage(pPartialTick);
        final Vec3i rotations = pBlockEntity.getSlotRotations();
        final Direction direction = blockState.getValue(RotatingTallBlock.FACING);

        // prepare to render
        final Minecraft mc = Minecraft.getInstance();

        final RenderType renderType = RenderType.cutout();
        final VertexConsumer vertexConsumer = pBufferSource.getBuffer(renderType);
        final BakedModel slotModel = mc.getModelManager().getModel(net.minecraft.client.resources.model.ModelResourceLocation.standalone(SLOT));
        final BakedModel leverModel = mc.getModelManager().getModel(net.minecraft.client.resources.model.ModelResourceLocation.standalone(LEVER));

        double dx = 0.5D;
        double dy = 0.0D;
        double dz = 0.5D;
        float xRot = 0;

        // start rendering
        pPoseStack.pushPose();

        final float yRot = (direction.getOpposite().toYRot()) * Mth.DEG_TO_RAD;
        pPoseStack.translate(dx, dy, dz);
        pPoseStack.mulPose(Axis.YN.rotation(yRot));
        pPoseStack.translate(-dx, -dy, -dz);
        pPoseStack.translate(0, 0, 0);

        // render lever
        dy = 3.0D / 16.0D;
        xRot = Math.max(0, 1.0F - Math.abs((duration - 0.025F) * 40.0F)) * 67.5F;
        pPoseStack.pushPose();
        pPoseStack.translate(dx, dy, dz);
        pPoseStack.mulPose(Axis.XN.rotationDegrees(xRot));
        pPoseStack.translate(-dx, -dy, -dz);
        blockRenderer.getModelRenderer().renderModel(pPoseStack.last(), vertexConsumer, blockState, leverModel,
                1.0F, 1.0F, 1.0F, pPackedLight, pPackedOverlay, ModelData.EMPTY, renderType);
        pPoseStack.popPose();

        // render slot 0
        dy = 3.5D / 16.0D;
        dz = 4.0D / 16.0D;
        xRot = 90.0F * rotations.getX();
        if (pBlockEntity.isActive() && duration < 0.58F) {
            xRot += (360 * 10) * Math.pow(8.0D, -10.0D * (duration - 0.18D));
        }
        pPoseStack.pushPose();
        pPoseStack.translate(0, 0, 0);
        pPoseStack.translate(dx, dy, dz);
        pPoseStack.mulPose(Axis.XP.rotationDegrees(xRot));
        pPoseStack.translate(-dx, -dy, -dz);
        blockRenderer.getModelRenderer().renderModel(pPoseStack.last(), vertexConsumer, blockState, slotModel,
                1.0F, 1.0F, 1.0F, pPackedLight, pPackedOverlay, ModelData.EMPTY, renderType);
        pPoseStack.popPose();

        // render slot 1
        xRot = 90.0F * rotations.getY();
        if (pBlockEntity.isActive() && duration < 0.78F) {
            xRot += (360 * 20) * Math.pow(8.0D, -10.0D * (duration - 0.35D));
        }
        pPoseStack.pushPose();
        pPoseStack.translate(-2.0D / 16.0D, 0, 0);
        pPoseStack.translate(dx, dy, dz);
        pPoseStack.mulPose(Axis.XP.rotationDegrees(xRot));
        pPoseStack.translate(-dx, -dy, -dz);
        blockRenderer.getModelRenderer().renderModel(pPoseStack.last(), vertexConsumer, blockState, slotModel,
                1.0F, 1.0F, 1.0F, pPackedLight, pPackedOverlay, ModelData.EMPTY, renderType);
        pPoseStack.popPose();

        // render slot 2
        xRot = 90.0F * rotations.getZ();
        if (pBlockEntity.isActive() && duration < 0.99F) {
            xRot += (360 * 30) * Math.pow(8.0D, -10.0D * (duration - 0.56D));
        }
        pPoseStack.translate(-4.0D / 16.0D, 0, 0);
        pPoseStack.pushPose();
        pPoseStack.translate(dx, dy, dz);
        pPoseStack.mulPose(Axis.XP.rotationDegrees(xRot));
        pPoseStack.translate(-dx, -dy, -dz);
        blockRenderer.getModelRenderer().renderModel(pPoseStack.last(), vertexConsumer, blockState, slotModel,
                1.0F, 1.0F, 1.0F, pPackedLight, pPackedOverlay, ModelData.EMPTY, renderType);
        pPoseStack.popPose();

        // finish rendering
        pPoseStack.popPose();
    }

    @Override
    public AABB getRenderBoundingBox(SlotMachineBlockEntity blockEntity) {
        return new AABB(blockEntity.getBlockPos()).inflate(1);
    }
}