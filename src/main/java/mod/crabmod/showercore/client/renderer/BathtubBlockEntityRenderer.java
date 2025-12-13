package mod.crabmod.showercore.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mod.crabmod.showercore.block.BathtubBlock;
import mod.crabmod.showercore.block.entity.BathtubBlockEntity;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import org.joml.Matrix4f;

public class BathtubBlockEntityRenderer implements BlockEntityRenderer<BathtubBlockEntity> {

    public BathtubBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(BathtubBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        BlockState state = pBlockEntity.getBlockState();
        if (state.getValue(BathtubBlock.LIQUID) != BathtubBlock.LiquidType.CUSTOM) {
            return;
        }

        FluidStack fluidStack = pBlockEntity.getFluidTank().getFluid();
        if (fluidStack.isEmpty()) return;

        Fluid fluid = fluidStack.getFluid();
        IClientFluidTypeExtensions fluidTypeExtensions = IClientFluidTypeExtensions.of(fluid);
        ResourceLocation stillTexture = fluidTypeExtensions.getStillTexture(fluidStack);
        if (stillTexture == null) return;

        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(stillTexture);
        int color = fluidTypeExtensions.getTintColor(fluidStack);
        if (pBlockEntity.getLevel() != null) {
            color = fluidTypeExtensions.getTintColor(fluid.defaultFluidState(), pBlockEntity.getLevel(), pBlockEntity.getBlockPos());
        }
        
        float alpha = ((color >> 24) & 0xFF) / 255f;
        float red = ((color >> 16) & 0xFF) / 255f;
        float green = ((color >> 8) & 0xFF) / 255f;
        float blue = (color & 0xFF) / 255f;

        pPoseStack.pushPose();
        
        VertexConsumer builder = pBufferSource.getBuffer(RenderType.translucent());
        Matrix4f matrix = pPoseStack.last().pose();
        
        float y = 0.6f; // Approximate water level
        float minU = sprite.getU0();
        float maxU = sprite.getU1();
        float minV = sprite.getV0();
        float maxV = sprite.getV1();
        
        float x1 = 0.125f;
        float x2 = 0.875f;
        float z1 = 0.125f;
        float z2 = 0.875f;
        
        Direction facing = state.getValue(BathtubBlock.FACING);
        BedPart part = state.getValue(BathtubBlock.PART);
        
        // Determine connected side
        // HEAD connects to opposite of facing (e.g. North facing -> connects to South)
        // FOOT connects to facing (e.g. North facing -> connects to North)
        Direction connectedSide = (part == BedPart.HEAD) ? facing.getOpposite() : facing;
        
        if (connectedSide == Direction.NORTH) z1 = 0;
        if (connectedSide == Direction.SOUTH) z2 = 1;
        if (connectedSide == Direction.WEST) x1 = 0;
        if (connectedSide == Direction.EAST) x2 = 1;
        
        // Render Quad
        // Note: UVs should probably be adjusted if we stretch the quad, but for now simple mapping is fine.
        addVertex(builder, matrix, x1, y, z1, minU, minV, red, green, blue, alpha, pPackedLight);
        addVertex(builder, matrix, x1, y, z2, minU, maxV, red, green, blue, alpha, pPackedLight);
        addVertex(builder, matrix, x2, y, z2, maxU, maxV, red, green, blue, alpha, pPackedLight);
        addVertex(builder, matrix, x2, y, z1, maxU, minV, red, green, blue, alpha, pPackedLight);
        
        pPoseStack.popPose();
    }

    private void addVertex(VertexConsumer builder, Matrix4f matrix, float x, float y, float z, float u, float v, float r, float g, float b, float a, int packedLight) {
        org.joml.Vector4f vector = new org.joml.Vector4f(x, y, z, 1.0F);
        matrix.transform(vector);
        builder.addVertex(vector.x, vector.y, vector.z)
               .setColor(r, g, b, a)
               .setUv(u, v)
               .setOverlay(net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY)
               .setLight(packedLight)
               .setNormal(0, 1, 0);
    }
}
