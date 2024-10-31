package mod.crabmod.showercore.block.bath_core.herbal_bath_core;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mod.crabmod.showercore.ShowerCore;
import net.minecraft.client.Camera;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@OnlyIn(Dist.CLIENT)
public class HerbalBathCoreBlockEntityRenderer implements BlockEntityRenderer<HerbalBathCoreBlockEntity>{

    public static final Material SHELL_TEXTURE = new Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation(ShowerCore.MODID, "entity/conduit/herbal_bath_core_base"));
    public static final Material ACTIVE_SHELL_TEXTURE = new Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation(ShowerCore.MODID, "entity/conduit/herbal_bath_core_cage"));
    public static final Material WIND_TEXTURE = new Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation(ShowerCore.MODID, "entity/conduit/herbal_bath_core_wind"));
    public static final Material VERTICAL_WIND_TEXTURE = new Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation(ShowerCore.MODID, "entity/conduit/herbal_bath_core_wind_vertical"));
    public static final Material OPEN_EYE_TEXTURE = new Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation(ShowerCore.MODID, "entity/conduit/herbal_bath_core_open"));
    public static final Material CLOSED_EYE_TEXTURE = new Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation(ShowerCore.MODID, "entity/conduit/herbal_bath_core_closed"));
    private final ModelPart eye;
    private final ModelPart wind;
    private final ModelPart shell;
    private final ModelPart cage;
    private final BlockEntityRenderDispatcher renderer;

    public HerbalBathCoreBlockEntityRenderer(BlockEntityRendererProvider.Context pContext) {
        this.renderer = pContext.getBlockEntityRenderDispatcher();
        this.eye = pContext.bakeLayer(HerbalBathCoreModelLayers.HERBAL_BATH_CORE_EYE);
        this.wind = pContext.bakeLayer(HerbalBathCoreModelLayers.HERBAL_BATH_CORE_WIND);
        this.shell = pContext.bakeLayer(HerbalBathCoreModelLayers.HERBAL_BATH_CORE_SHELL);
        this.cage = pContext.bakeLayer(HerbalBathCoreModelLayers.HERBAL_BATH_CORE_CAGE);
    }

    public static LayerDefinition createEyeLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        partdefinition.addOrReplaceChild("eye", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -4.0F, 0.0F, 8.0F, 8.0F, 0.0F, new CubeDeformation(0.01F)), PartPose.ZERO);
        return LayerDefinition.create(meshdefinition, 16, 16);
    }

    public static LayerDefinition createWindLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        partdefinition.addOrReplaceChild("wind", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -8.0F, -8.0F, 16.0F, 16.0F, 16.0F), PartPose.ZERO);
        return LayerDefinition.create(meshdefinition, 64, 32);
    }

    public static LayerDefinition createShellLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        partdefinition.addOrReplaceChild("shell", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -3.0F, -3.0F, 6.0F, 6.0F, 6.0F), PartPose.ZERO);
        return LayerDefinition.create(meshdefinition, 32, 16);
    }

    public static LayerDefinition createCageLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        partdefinition.addOrReplaceChild("shell", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F), PartPose.ZERO);
        return LayerDefinition.create(meshdefinition, 32, 16);
    }

    @Override
    public void render(HerbalBathCoreBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        float f = (float)pBlockEntity.tickCount + pPartialTick;
        if (!pBlockEntity.isActive()) {
            float f5 = pBlockEntity.getActiveRotation(0.0F);
            VertexConsumer vertexconsumer1 = SHELL_TEXTURE.buffer(pBuffer, RenderType::entitySolid);
            pPoseStack.pushPose();
            pPoseStack.translate(0.5F, 0.5F, 0.5F);
            pPoseStack.mulPose((new Quaternionf()).rotationY(f5 * ((float)Math.PI / 180F)));
            this.shell.render(pPoseStack, vertexconsumer1, pPackedLight, pPackedOverlay);
            pPoseStack.popPose();
        } else {
            float f1 = pBlockEntity.getActiveRotation(pPartialTick) * (180F / (float)Math.PI);
            float f2 = Mth.sin(f * 0.1F) / 2.0F + 0.5F;
            f2 = f2 * f2 + f2;
            pPoseStack.pushPose();
            pPoseStack.translate(0.5F, 0.3F + f2 * 0.2F, 0.5F);
            Vector3f vector3f = (new Vector3f(0.5F, 1.0F, 0.5F)).normalize();
            pPoseStack.mulPose((new Quaternionf()).rotationAxis(f1 * ((float)Math.PI / 180F), vector3f));
            this.cage.render(pPoseStack, ACTIVE_SHELL_TEXTURE.buffer(pBuffer, RenderType::entityCutoutNoCull), pPackedLight, pPackedOverlay);
            pPoseStack.popPose();
            int i = pBlockEntity.tickCount / 66 % 3;
            pPoseStack.pushPose();
            pPoseStack.translate(0.5F, 0.5F, 0.5F);
            if (i == 1) {
                pPoseStack.mulPose((new Quaternionf()).rotationX(((float)Math.PI / 2F)));
            } else if (i == 2) {
                pPoseStack.mulPose((new Quaternionf()).rotationZ(((float)Math.PI / 2F)));
            }

            VertexConsumer vertexconsumer = (i == 1 ? VERTICAL_WIND_TEXTURE : WIND_TEXTURE).buffer(pBuffer, RenderType::entityCutoutNoCull);
            this.wind.render(pPoseStack, vertexconsumer, pPackedLight, pPackedOverlay);
            pPoseStack.popPose();
            pPoseStack.pushPose();
            pPoseStack.translate(0.5F, 0.5F, 0.5F);
            pPoseStack.scale(0.875F, 0.875F, 0.875F);
            pPoseStack.mulPose((new Quaternionf()).rotationXYZ((float)Math.PI, 0.0F, (float)Math.PI));
            this.wind.render(pPoseStack, vertexconsumer, pPackedLight, pPackedOverlay);
            pPoseStack.popPose();
            Camera camera = this.renderer.camera;
            pPoseStack.pushPose();
            pPoseStack.translate(0.5F, 0.3F + f2 * 0.2F, 0.5F);
            pPoseStack.scale(0.5F, 0.5F, 0.5F);
            float f3 = -camera.getYRot();
            pPoseStack.mulPose((new Quaternionf()).rotationYXZ(f3 * ((float)Math.PI / 180F), camera.getXRot() * ((float)Math.PI / 180F), (float)Math.PI));
            float f4 = 1.3333334F;
            pPoseStack.scale(1.3333334F, 1.3333334F, 1.3333334F);
            this.eye.render(pPoseStack, (pBlockEntity.isHunting() ? OPEN_EYE_TEXTURE : CLOSED_EYE_TEXTURE).buffer(pBuffer, RenderType::entityCutoutNoCull), pPackedLight, pPackedOverlay);
            pPoseStack.popPose();
        }
    }
}
