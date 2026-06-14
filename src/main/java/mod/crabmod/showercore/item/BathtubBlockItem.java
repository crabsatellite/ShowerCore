package mod.crabmod.showercore.item;

import mod.crabmod.showercore.block.entity.BathtubBlockEntity;
import mod.crabmod.showercore.block.BathtubBlock;
import mod.crabmod.showercore.registers.BlockEntitiesRegister;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.item.component.CustomData;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class BathtubBlockItem extends BlockItem {
    private static final int MATERIAL_CHANGE_COST = 6;

    public BathtubBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        InteractionResult materialResult = tryApplyMaterialToHeldBathtub(level, player, hand);
        if (materialResult.consumesAction()) {
            return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide());
        }
        return InteractionResultHolder.pass(player.getItemInHand(hand));
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        ResourceLocation materialBlockId = getMaterialBlockId(stack);
        if (materialBlockId != null) {
            tooltip.add(Component.translatable("tooltip.showercore.bathtub.material", getMaterialName(materialBlockId))
                    .withStyle(ChatFormatting.GRAY));
        }
        tooltip.add(Component.translatable("tooltip.showercore.bathtub.usage.sit"));
        tooltip.add(Component.translatable("tooltip.showercore.bathtub.usage.toggle"));
        tooltip.add(Component.translatable("tooltip.showercore.bathtub.usage.fill"));
        tooltip.add(Component.translatable("tooltip.showercore.bathtub.usage.material"));
        tooltip.add(Component.translatable("tooltip.showercore.bathtub.usage.duck"));
        super.appendHoverText(stack, context, tooltip, flag);
    }

    @Nullable
    public static ResourceLocation getMaterialBlockId(ItemStack stack) {
        CustomData customData = stack.get(DataComponents.BLOCK_ENTITY_DATA);
        if (customData == null) {
            return null;
        }
        CompoundTag tag = customData.copyTag();
        if (!tag.contains(BathtubBlockEntity.TAG_MATERIAL_BLOCK_ID)) {
            return null;
        }
        return ResourceLocation.tryParse(tag.getString(BathtubBlockEntity.TAG_MATERIAL_BLOCK_ID));
    }

    public static InteractionResult tryApplyMaterialToHeldBathtub(Level level, Player player, InteractionHand activeHand) {
        InteractionHand otherHand = activeHand == InteractionHand.MAIN_HAND
                ? InteractionHand.OFF_HAND
                : InteractionHand.MAIN_HAND;
        ItemStack activeStack = player.getItemInHand(activeHand);
        ItemStack otherStack = player.getItemInHand(otherHand);

        if (activeStack.getItem() instanceof BathtubBlockItem && isMaterialStack(otherStack)) {
            return applyMaterial(level, player, activeHand, otherHand);
        }
        if (otherStack.getItem() instanceof BathtubBlockItem && isMaterialStack(activeStack)) {
            return applyMaterial(level, player, otherHand, activeHand);
        }
        return InteractionResult.PASS;
    }

    private static boolean isMaterialStack(ItemStack stack) {
        if (!(stack.getItem() instanceof BlockItem blockItem)) {
            return false;
        }
        Block materialBlock = blockItem.getBlock();
        return !(materialBlock instanceof BathtubBlock) && materialBlock != Blocks.AIR;
    }

    private static InteractionResult applyMaterial(Level level, Player player, InteractionHand bathtubHand, InteractionHand materialHand) {
        ItemStack bathtubStack = player.getItemInHand(bathtubHand);
        ItemStack materialStack = player.getItemInHand(materialHand);
        if (!(bathtubStack.getItem() instanceof BathtubBlockItem)
                || !(materialStack.getItem() instanceof BlockItem blockItem)) {
            return InteractionResult.PASS;
        }

        Block materialBlock = blockItem.getBlock();
        if (materialBlock instanceof BathtubBlock || materialBlock == Blocks.AIR) {
            return InteractionResult.PASS;
        }

        ResourceLocation materialBlockId = BuiltInRegistries.BLOCK.getKey(materialBlock);
        if (materialBlockId == null) {
            return InteractionResult.PASS;
        }
        if (Objects.equals(getMaterialBlockId(bathtubStack), materialBlockId)) {
            return InteractionResult.sidedSuccess(level.isClientSide());
        }
        if (!player.isCreative() && materialStack.getCount() < MATERIAL_CHANGE_COST) {
            if (!level.isClientSide) {
                player.displayClientMessage(Component.translatable("message.showercore.bathtub.material.not_enough",
                        MATERIAL_CHANGE_COST), true);
            }
            return InteractionResult.sidedSuccess(level.isClientSide());
        }
        if (!level.isClientSide) {
            applyMaterialToOneBathtub(player, bathtubHand, bathtubStack, materialBlockId);
            if (!player.isCreative()) {
                materialStack.shrink(MATERIAL_CHANGE_COST);
            }
            SoundEvent sound = materialBlock.defaultBlockState().getSoundType().getPlaceSound();
            level.playSound(null, player.blockPosition(), sound, SoundSource.BLOCKS, 0.7F, 1.0F);
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    private static void applyMaterialToOneBathtub(Player player, InteractionHand bathtubHand,
                                                  ItemStack bathtubStack, ResourceLocation materialBlockId) {
        if (bathtubStack.getCount() <= 1) {
            setMaterialBlockId(bathtubStack, materialBlockId);
            return;
        }

        ItemStack remainder = bathtubStack.copy();
        remainder.setCount(bathtubStack.getCount() - 1);
        ItemStack skinnedBathtub = bathtubStack.copy();
        skinnedBathtub.setCount(1);
        setMaterialBlockId(skinnedBathtub, materialBlockId);
        player.setItemInHand(bathtubHand, skinnedBathtub);
        if (!player.getInventory().add(remainder)) {
            player.drop(remainder, false);
        }
    }

    private static void setMaterialBlockId(ItemStack stack, ResourceLocation materialBlockId) {
        CustomData customData = stack.get(DataComponents.BLOCK_ENTITY_DATA);
        CompoundTag tag = customData == null ? new CompoundTag() : customData.copyTag();
        tag.putString(BathtubBlockEntity.TAG_MATERIAL_BLOCK_ID, materialBlockId.toString());
        BlockItem.setBlockEntityData(stack, BlockEntitiesRegister.BATHTUB_BLOCK_ENTITY.get(), tag);
    }

    private static Component getMaterialName(ResourceLocation materialBlockId) {
        return BuiltInRegistries.BLOCK.getOptional(materialBlockId)
                .map(block -> {
                    ItemStack stack = new ItemStack(block);
                    return stack.is(Items.AIR)
                            ? Component.literal(materialBlockId.toString())
                            : stack.getHoverName();
                })
                .orElse(Component.literal(materialBlockId.toString()));
    }
}
