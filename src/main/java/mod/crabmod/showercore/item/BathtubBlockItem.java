package mod.crabmod.showercore.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BathtubBlockItem extends BlockItem {
    public BathtubBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.showercore.bathtub.usage.sit"));
        tooltip.add(Component.translatable("tooltip.showercore.bathtub.usage.toggle"));
        tooltip.add(Component.translatable("tooltip.showercore.bathtub.usage.fill"));
        tooltip.add(Component.translatable("tooltip.showercore.bathtub.usage.duck"));
        super.appendHoverText(stack, level, tooltip, flag);
    }
}
