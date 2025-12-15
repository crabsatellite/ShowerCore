/**
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 **/

package tanukidecor.network;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import tanukidecor.TanukiDecor;
import tanukidecor.menu.DIYWorkbenchMenu;
import tanukidecor.recipe.DIYRecipe;

import java.util.Optional;

public record ServerBoundSelectDIYRecipePacket(ResourceLocation recipeId) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ServerBoundSelectDIYRecipePacket> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(TanukiDecor.MODID, "select_diy_recipe"));

    private static final TagKey<Item> DIY_BLACKLIST_TAG_KEY = TagKey.create(Registries.ITEM,
            ResourceLocation.fromNamespaceAndPath(TanukiDecor.MODID, "diy_blacklist"));

    public static final StreamCodec<FriendlyByteBuf, ServerBoundSelectDIYRecipePacket> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC,
            ServerBoundSelectDIYRecipePacket::recipeId,
            ServerBoundSelectDIYRecipePacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    /**
     * Handles the packet when it is received on the server.
     *
     * @param payload the packet payload
     * @param context the payload context
     */
    public static void handle(final ServerBoundSelectDIYRecipePacket payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer) context.player();

            // validate menu
            if (!(player.containerMenu instanceof DIYWorkbenchMenu menu)) {
                return;
            }
            // validate crafting
            if (!TanukiDecor.CONFIG.isDIYWorkbenchEnabled.get()) {
                return;
            }
            // validate recipe
            final Optional<RecipeHolder<?>> oRecipe = player.level().getRecipeManager().byKey(payload.recipeId());
            if (oRecipe.isEmpty() || !(oRecipe.get().value() instanceof DIYRecipe recipe)) {
                return;
            }
            // validate result
            if (recipe.getResultItem(player.level().registryAccess()).is(DIY_BLACKLIST_TAG_KEY)) {
                return;
            }
            // update menu
            menu.setRecipe(recipe);
        });
    }
}
