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
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import tanukidecor.TanukiDecor;
import tanukidecor.block.entity.ClockBlockEntity;

import java.util.Set;

public class CarriageClockBER extends ClockBER {

    public static final ResourceLocation LONG_HAND = ResourceLocation.fromNamespaceAndPath(TanukiDecor.MODID, "block/carriage_clock/long_hand");
    public static final ResourceLocation SHORT_HAND = ResourceLocation.fromNamespaceAndPath(TanukiDecor.MODID, "block/carriage_clock/short_hand");
    public static final ResourceLocation GEAR = ResourceLocation.fromNamespaceAndPath(TanukiDecor.MODID, "block/carriage_clock/gear");
    private static final ModelResourceLocation GEAR_MODEL = ModelResourceLocation.standalone(GEAR);

    public CarriageClockBER(BlockEntityRendererProvider.Context pContext) {
        super(pContext, SHORT_HAND, LONG_HAND,
                ROOT_POSITION,
                ROOT_PIVOT_POINT,
                HANDS_POSITION,
                new Vec3(8.0D / 16.0D, 7.0D / 16.0D, 0));
    }

    @Override
    public void render(ClockBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        super.render(pBlockEntity, pPartialTick, pPoseStack, pBufferSource, pPackedLight, pPackedOverlay);
    }

    @Override
    public void renderAdditional(ClockRenderHelper renderHelper, ClockBlockEntity blockEntity, MultiBufferSource bufferSource) {
        final float time = blockEntity.getLevel().getGameTime() + renderHelper.getPartialTick();
        final float angle = 0.00625F * time * Mth.PI;

        final BakedModel gear = Minecraft.getInstance().getModelManager().getModel(GEAR_MODEL);
        final Vec3 gearPosition = new Vec3(-8.0D / 16.0D, -8.0D / 16.0D, 0);

        this.clockRenderHelper
                .withModel(gear)
                .withPosition(gearPosition)
                .withPivotPoint(new Vec3(7.5D / 16.0D, 4.5D / 16.0D, 0))
                .withRotationZ(angle)
                .render(blockRenderer);

        this.clockRenderHelper
                .withPosition(gearPosition.add(-1.0D / 16.0D, 4.0D / 16.0D, 0))
                .withRotationZ(-angle)
                .render(blockRenderer);
    }

    public static void addSpecialModels(final Set<ResourceLocation> list) {
        list.add(LONG_HAND);
        list.add(SHORT_HAND);
        list.add(GEAR);
    }

}
