/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.client.blockentity.misc;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import tanukidecor.block.entity.DisplayBlockEntity;

public class HandcartBER extends DisplayCaseBER {

    public HandcartBER(BlockEntityRendererProvider.Context pContext) {
        super(pContext);
    }

    @Override
    public void render(DisplayBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        // load values
        final Level level = pBlockEntity.getLevel();
        final BlockState blockState = pBlockEntity.getBlockState();
        final BlockPos blockPos = pBlockEntity.getBlockPos();
        final ItemStack itemStack = pBlockEntity.getItem(0);
        // validate item stack
        if (itemStack.isEmpty()) {
            return;
        }
        // prepare to render
        double dx = 0.5D;
        double dy = 0.5D;
        double dz = 0.5D;
        pPoseStack.pushPose();
        pPoseStack.translate(dx, dy, dz);
        pPoseStack.mulPose(Axis.YN.rotationDegrees(blockState.getValue(HorizontalDirectionalBlock.FACING).toYRot()));
        pPoseStack.translate(-dx, -dy, -dz);
        pPoseStack.translate(11.0F / 16.0F, 0, 0);

        // determine render passes
        int renderCount = (int) Math.ceil((float) itemStack.getCount() / Math.max(1.0F, itemStack.getMaxStackSize() / 16.0F));
        // render each render pass
        for (int renderPass = 0; renderPass < renderCount; renderPass++) {

            // determine translation and rotation
            Vector3f translation = new Vector3f(0.5F, 0.5F, 0.5F);
            translation.add(pBlockEntity.getDisplayTranslation(level, blockState, blockPos, itemStack, renderPass, pPartialTick));
            Vector3f rotation = pBlockEntity.getDisplayRotation(level, blockState, blockPos, itemStack, renderPass, pPartialTick);
            Vector3f scale = pBlockEntity.getDisplayScale(level, blockState, blockPos, itemStack, renderPass, pPartialTick);

            // start rendering
            pPoseStack.pushPose();

            pPoseStack.translate(translation.x(), translation.y(), translation.z());
            Quaternionf quaternion = Axis.XP.rotationDegrees(rotation.x());
            quaternion.mul(Axis.YP.rotationDegrees(rotation.y()));
            quaternion.mul(Axis.ZP.rotationDegrees(rotation.z()));
            pPoseStack.mulPose(quaternion);
            pPoseStack.scale(scale.x(), scale.y(), scale.z());

            // render item
            this.itemRenderer.renderStatic(itemStack, ItemDisplayContext.FIXED, pPackedLight, OverlayTexture.NO_OVERLAY, pPoseStack, pBufferSource, level, 0);

            // finish rendering
            pPoseStack.popPose();
        }

        pPoseStack.popPose();

        // render nametag
        if (pBlockEntity.hasCustomName() && shouldRenderNameTag(pBlockEntity)) {
            renderNameTag(pBlockEntity, pBlockEntity.getCustomName(), pPoseStack, pBufferSource, pPackedLight);
        }
    }
}
