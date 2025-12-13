package mod.crabmod.showercore.item;

import mod.crabmod.showercore.ShowerCore;
import mod.crabmod.showercore.registers.BlocksRegister;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ItemGroup {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ShowerCore.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> HOT_BATH_TAB = CREATIVE_MODE_TABS.register("hot_bath_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.showercore"))
            .icon(() -> new ItemStack(BlocksRegister.BATHTUB_WHITE.get()))
            .build());
}
