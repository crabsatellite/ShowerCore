package mod.crabmod.showercore.item;

import mod.crabmod.showercore.block.entity.BathtubBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.component.CustomData;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BathtubBlockItem extends BlockItem {
    public BathtubBlockItem(Block block, Properties properties) {
        super(block, properties);
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
    private static ResourceLocation getMaterialBlockId(ItemStack stack) {
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
