package mod.crabmod.showercore.item;

import mod.crabmod.showercore.block.entity.BathtubBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BathtubBlockItem extends BlockItem {
    public BathtubBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
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
        super.appendHoverText(stack, level, tooltip, flag);
    }

    @Nullable
    private static ResourceLocation getMaterialBlockId(ItemStack stack) {
        CompoundTag tag = BlockItem.getBlockEntityData(stack);
        if (tag == null || !tag.contains(BathtubBlockEntity.TAG_MATERIAL_BLOCK_ID)) {
            return null;
        }
        return ResourceLocation.tryParse(tag.getString(BathtubBlockEntity.TAG_MATERIAL_BLOCK_ID));
    }

    private static Component getMaterialName(ResourceLocation materialBlockId) {
        Block block = ForgeRegistries.BLOCKS.getValue(materialBlockId);
        if (block == null) {
            return Component.literal(materialBlockId.toString());
        }
        ItemStack stack = new ItemStack(block);
        return stack.is(Items.AIR) ? Component.literal(materialBlockId.toString()) : stack.getHoverName();
    }
}
