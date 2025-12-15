/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.client.blockentity.clock;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import tanukidecor.TanukiDecor;
import tanukidecor.block.clock.IChimeProvider;
import tanukidecor.block.entity.ClockBlockEntity;

import java.util.Set;

public class CuckooClockBER extends ClockBER {

    public static final ResourceLocation LONG_HAND = ResourceLocation.fromNamespaceAndPath(TanukiDecor.MODID, "block/cuckoo_clock/long_hand");
    public static final ResourceLocation SHORT_HAND = ResourceLocation.fromNamespaceAndPath(TanukiDecor.MODID, "block/cuckoo_clock/short_hand");
    public static final ResourceLocation CUCKOO = ResourceLocation.fromNamespaceAndPath(TanukiDecor.MODID, "block/cuckoo_clock/cuckoo");
    private static final ModelResourceLocation CUCKOO_MODEL = ModelResourceLocation.standalone(CUCKOO);

    public CuckooClockBER(BlockEntityRendererProvider.Context pContext) {
        super(pContext, SHORT_HAND, LONG_HAND,
                ROOT_POSITION,
                ROOT_PIVOT_POINT,
                HANDS_POSITION,
                new Vec3(8.0D / 16.0D, 5.0D / 16.0D, 0));
    }

    @Override
    public void render(ClockBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        super.render(pBlockEntity, pPartialTick, pPoseStack, pBufferSource, pPackedLight, pPackedOverlay);
    }

    @Override
    public void renderAdditional(ClockRenderHelper renderHelper, ClockBlockEntity blockEntity, MultiBufferSource bufferSource) {
        // validate block
        if (!(blockEntity.getBlockState().getBlock() instanceof IChimeProvider chimeProvider)) {
            return;
        }
        // validate time
        final int duration = 20;
        final long dayTime = (blockEntity.getLevel().getDayTime()) % 24000L;
        final long adjustedTime = duration * (dayTime / duration);
        if (!chimeProvider.isTimeToChime(renderHelper.getBlockState(), adjustedTime)) {
            return;
        }

        // prepare to render
        final BakedModel model = Minecraft.getInstance().getModelManager().getModel(CUCKOO_MODEL);
        final float cuckooPercent = (dayTime % duration + renderHelper.getPartialTick()) / ((float) duration);
        // the percent of the animation to spend at the apex
        final float apex = 0.15F;
        // the factor to transform to a quadratic equation
        final float factor = ((2.0F + apex) * cuckooPercent - (1.0F + apex * 0.5F));
        final float percent = 1.0F - Math.max(0.0F, factor * factor * factor * factor - apex);
        final double zPos = (-8.0D * percent) / 16.0D;
        final Vec3 position = new Vec3(-8.0D / 16.0D, -8.0D / 16.0D, zPos);

        // render model
        this.clockRenderHelper
                .withModel(model)
                .withPosition(position)
                .withPivotPoint(ROOT_PIVOT_POINT)
                .withRotationZ(0)
                .render(blockRenderer);
    }

    public static void addSpecialModels(final Set<ResourceLocation> list) {
        list.add(LONG_HAND);
        list.add(SHORT_HAND);
        list.add(CUCKOO);
    }

}
