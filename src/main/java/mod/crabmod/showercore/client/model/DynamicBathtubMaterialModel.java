package mod.crabmod.showercore.client.model;

import mod.crabmod.showercore.ShowerCore;
import mod.crabmod.showercore.block.BathtubBlock;
import mod.crabmod.showercore.block.entity.BathtubBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.BakedModelWrapper;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelProperty;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class DynamicBathtubMaterialModel extends BakedModelWrapper<BakedModel> {
    private static final ModelProperty<ResourceLocation> MATERIAL_BLOCK_ID = new ModelProperty<>();
    private static final Map<ResourceLocation, Optional<TextureAtlasSprite>> MATERIAL_SPRITE_CACHE = new ConcurrentHashMap<>();
    private static final int INTS_PER_VERTEX = 8;
    private static final int U_OFFSET = 4;
    private static final int V_OFFSET = 5;

    public DynamicBathtubMaterialModel(BakedModel originalModel) {
        super(originalModel);
    }

    public static boolean shouldWrap(ResourceLocation location) {
        return location.getNamespace().equals(ShowerCore.MODID) && location.getPath().contains("bathtub_");
    }

    public static void clearCache() {
        MATERIAL_SPRITE_CACHE.clear();
    }

    @Override
    public ModelData getModelData(BlockAndTintGetter level, BlockPos pos, BlockState state, ModelData modelData) {
        ModelData data = super.getModelData(level, pos, state, modelData);
        if (state.getBlock() instanceof BathtubBlock
                && level.getBlockEntity(pos) instanceof BathtubBlockEntity bathtubEntity
                && bathtubEntity.getMaterialBlockId() != null) {
            return data.derive().with(MATERIAL_BLOCK_ID, bathtubEntity.getMaterialBlockId()).build();
        }
        return data;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand,
                                    ModelData extraData, @Nullable RenderType renderType) {
        List<BakedQuad> quads = super.getQuads(state, side, rand, extraData, renderType);
        ResourceLocation materialBlockId = extraData.get(MATERIAL_BLOCK_ID);
        if (materialBlockId == null || state == null || !(state.getBlock() instanceof BathtubBlock)) {
            return quads;
        }

        TextureAtlasSprite materialSprite = resolveMaterialSprite(materialBlockId);
        if (materialSprite == null) {
            return quads;
        }

        TextureAtlasSprite bodySprite = originalModel.getParticleIcon(ModelData.EMPTY);
        if (bodySprite == materialSprite) {
            return quads;
        }

        List<BakedQuad> replaced = new ArrayList<>(quads.size());
        boolean changed = false;
        for (BakedQuad quad : quads) {
            if (quad.getSprite() == bodySprite) {
                replaced.add(copyWithSprite(quad, bodySprite, materialSprite));
                changed = true;
            } else {
                replaced.add(quad);
            }
        }
        return changed ? replaced : quads;
    }

    @Nullable
    private static TextureAtlasSprite resolveMaterialSprite(ResourceLocation materialBlockId) {
        Optional<TextureAtlasSprite> cached = MATERIAL_SPRITE_CACHE.get(materialBlockId);
        if (cached != null) {
            return cached.orElse(null);
        }

        Block block = ForgeRegistries.BLOCKS.getValue(materialBlockId);
        Optional<TextureAtlasSprite> resolved = Optional.ofNullable(block)
                .filter(value -> !(value instanceof BathtubBlock))
                .map(DynamicBathtubMaterialModel::getParticleSprite);
        MATERIAL_SPRITE_CACHE.put(materialBlockId, resolved);
        return resolved.orElse(null);
    }

    private static TextureAtlasSprite getParticleSprite(Block block) {
        BakedModel model = Minecraft.getInstance().getBlockRenderer().getBlockModel(block.defaultBlockState());
        return model.getParticleIcon(ModelData.EMPTY);
    }

    private static BakedQuad copyWithSprite(BakedQuad quad, TextureAtlasSprite fromSprite, TextureAtlasSprite toSprite) {
        int[] vertices = quad.getVertices().clone();
        remapSpriteUvs(vertices, fromSprite, toSprite);
        return new BakedQuad(vertices, quad.getTintIndex(), quad.getDirection(), toSprite, quad.isShade());
    }

    private static void remapSpriteUvs(int[] vertices, TextureAtlasSprite fromSprite, TextureAtlasSprite toSprite) {
        float fromUSize = fromSprite.getU1() - fromSprite.getU0();
        float fromVSize = fromSprite.getV1() - fromSprite.getV0();
        if (fromUSize == 0.0F || fromVSize == 0.0F) {
            return;
        }
        for (int vertex = 0; vertex < 4; vertex++) {
            int base = vertex * INTS_PER_VERTEX;
            float oldU = Float.intBitsToFloat(vertices[base + U_OFFSET]);
            float oldV = Float.intBitsToFloat(vertices[base + V_OFFSET]);
            float relativeU = (oldU - fromSprite.getU0()) / fromUSize;
            float relativeV = (oldV - fromSprite.getV0()) / fromVSize;
            float newU = toSprite.getU0() + relativeU * (toSprite.getU1() - toSprite.getU0());
            float newV = toSprite.getV0() + relativeV * (toSprite.getV1() - toSprite.getV0());
            vertices[base + U_OFFSET] = Float.floatToRawIntBits(newU);
            vertices[base + V_OFFSET] = Float.floatToRawIntBits(newV);
        }
    }
}
