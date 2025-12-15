/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.client.blockentity.misc;

import com.mojang.blaze3d.vertex.PoseStack;
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
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import tanukidecor.TanukiDecor;
import tanukidecor.block.clock.IChimeProvider;
import tanukidecor.block.entity.MetronomeBlockEntity;
import tanukidecor.block.misc.MetronomeBlock;
import tanukidecor.client.blockentity.clock.ClockBER;
import tanukidecor.client.blockentity.clock.ClockRenderHelper;

import java.util.Set;

public class MetronomeBER implements BlockEntityRenderer<MetronomeBlockEntity> {

    public static final ResourceLocation PENDULUM = ResourceLocation.fromNamespaceAndPath(TanukiDecor.MODID, "block/metronome/pendulum");

    private static final Vec3 PENDULUM_POSITION = new Vec3(-8.0D / 16.0D, -8.0D / 16.0D, 0);
    private static final Vec3 PENDULUM_PIVOT_POINT = new Vec3(8.0D / 16.0D, 3.0D / 16.0D, 0);

    protected final BlockRenderDispatcher blockRenderer;
    protected final ClockRenderHelper clockRenderHelper;

    public MetronomeBER(BlockEntityRendererProvider.Context pContext) {
        this.blockRenderer = pContext.getBlockRenderDispatcher();
        this.clockRenderHelper = new ClockRenderHelper();
    }

    @Override
    public void render(MetronomeBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        // prepare to render
        final Minecraft mc = Minecraft.getInstance();
        final BlockState blockState = pBlockEntity.getBlockState();
        final Direction direction = blockState.getValue(BlockStateProperties.HORIZONTAL_FACING);
        final BakedModel pendulum = mc.getModelManager().getModel(net.minecraft.client.resources.model.ModelResourceLocation.standalone(PENDULUM));

        final int blockSpeed = blockState.getValue(MetronomeBlock.SPEED);
        final float pendulumRotation;
        if (blockSpeed == 0) {
            pendulumRotation = 0;
        } else {
            final int tickInterval = ((IChimeProvider) blockState.getBlock()).getTickSoundInterval(blockState);
            final float time = ((pBlockEntity.getLevel().getGameTime()) % 12000L) + pPartialTick;
            final float speed = Mth.PI / (float) tickInterval;
            final float angle = 30.0F * Mth.DEG_TO_RAD;
            pendulumRotation = pBlockEntity.getBias() * Mth.sin(time * speed) * angle;
        }

        // start rendering
        pPoseStack.pushPose();

        // prepare render helper
        this.clockRenderHelper
                .withPoseStack(pPoseStack)
                .withBlockState(blockState)
                .withRenderType(pBufferSource, RenderType.cutout())
                .withPackedLight(pPackedLight)
                .withPackedOverlay(pPackedOverlay)
                .withPartialTick(pPartialTick)
                .withRandom(pBlockEntity.getLevel().getRandom())
                .withRotationZ(0);

        // rotate pose stack
        this.clockRenderHelper
                .withPosition(ClockBER.ROOT_POSITION)
                .withPivotPoint(ClockBER.ROOT_PIVOT_POINT)
                .rotateForDirection(direction);


        // render pendulum
        this.clockRenderHelper
                .withModel(pendulum)
                .withPosition(PENDULUM_POSITION)
                .withPivotPoint(PENDULUM_PIVOT_POINT)
                .withRotationZ(pendulumRotation)
                .render(blockRenderer);

        // finish rendering
        pPoseStack.popPose();
    }

    @Override
    public AABB getRenderBoundingBox(MetronomeBlockEntity blockEntity) {
        return new AABB(blockEntity.getBlockPos()).inflate(1);
    }

    public static void addSpecialModels(final Set<ResourceLocation> list) {
        list.add(PENDULUM);
    }

}
