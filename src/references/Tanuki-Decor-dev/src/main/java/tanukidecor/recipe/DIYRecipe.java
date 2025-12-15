/*
 * Copyright (c) 2024 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import tanukidecor.TDRegistry;
import tanukidecor.block.entity.DIYWorkbenchBlockEntity;

public class DIYRecipe implements Recipe<RecipeInput> {

    private final ItemStack result;

    public DIYRecipe(ItemStack result) {
        this.result = result;
    }

    @Override
    public boolean matches(RecipeInput pInput, Level pLevel) {
        for (int i = 0; i < 4; i++) {
            Ingredient ingredient = DIYWorkbenchBlockEntity.INGREDIENTS[i];
            ItemStack itemStack = pInput.getItem(i);
            if (!ingredient.test(itemStack)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack assemble(RecipeInput pInput, HolderLookup.Provider pProvider) {
        return this.result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return (pWidth * pHeight) >= 4;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider pProvider) {
        return this.result;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return TDRegistry.RecipeReg.DIY_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return TDRegistry.RecipeReg.DIY.get();
    }

    /// / SERIALIZER ////

    public static class Serializer implements RecipeSerializer<DIYRecipe> {

        public static final String CATEGORY = "diy";

        public static final MapCodec<DIYRecipe> CODEC = RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        ItemStack.CODEC.fieldOf("result").forGetter(recipe -> recipe.result)
                ).apply(instance, DIYRecipe::new)
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, DIYRecipe> STREAM_CODEC = StreamCodec.composite(
                ItemStack.STREAM_CODEC,
                recipe -> recipe.result,
                DIYRecipe::new
        );

        @Override
        public MapCodec<DIYRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, DIYRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
