package mod.crabmod.showercore.mixin;

import com.crabmod.hotbath.util.CustomFluidHandler;
import mod.crabmod.showercore.block.BathtubBlock;
import mod.crabmod.showercore.utils.CoreUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CustomFluidHandler.class)
public class CustomFluidHandlerMixin {

    @Inject(method = "isPlayerInHotBathBlock", at = @At("HEAD"), cancellable = true, remap = false)
    private static void isPlayerInHotBathBlock(Player player, CallbackInfoReturnable<Boolean> cir) {
        if (CoreUtils.isEntityInAnyHotBathtub(player)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "isPlayerInHotWaterBlock", at = @At("HEAD"), cancellable = true, remap = false)
    private static void isPlayerInHotWaterBlock(Player player, CallbackInfoReturnable<Boolean> cir) {
        if (CoreUtils.isEntityInBathtub(player, BathtubBlock.LiquidType.HOT_WATER)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "isPlayerInHerbalBathBlock", at = @At("HEAD"), cancellable = true, remap = false)
    private static void isPlayerInHerbalBathBlock(Player player, CallbackInfoReturnable<Boolean> cir) {
        if (CoreUtils.isEntityInBathtub(player, BathtubBlock.LiquidType.HERBAL_BATH)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "isPlayerInHoneyBathBlock", at = @At("HEAD"), cancellable = true, remap = false)
    private static void isPlayerInHoneyBathBlock(Player player, CallbackInfoReturnable<Boolean> cir) {
        if (CoreUtils.isEntityInBathtub(player, BathtubBlock.LiquidType.HONEY_BATH)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "isPlayerInPeonyBathBlock", at = @At("HEAD"), cancellable = true, remap = false)
    private static void isPlayerInPeonyBathBlock(Player player, CallbackInfoReturnable<Boolean> cir) {
        if (CoreUtils.isEntityInBathtub(player, BathtubBlock.LiquidType.PEONY_BATH)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "isPlayerInMilkBathBlock", at = @At("HEAD"), cancellable = true, remap = false)
    private static void isPlayerInMilkBathBlock(Player player, CallbackInfoReturnable<Boolean> cir) {
        if (CoreUtils.isEntityInBathtub(player, BathtubBlock.LiquidType.MILK_BATH)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "isPlayerInRoseBathBlock", at = @At("HEAD"), cancellable = true, remap = false)
    private static void isPlayerInRoseBathBlock(Player player, CallbackInfoReturnable<Boolean> cir) {
        if (CoreUtils.isEntityInBathtub(player, BathtubBlock.LiquidType.ROSE_BATH)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "isEntityInHerbalBathBlock", at = @At("HEAD"), cancellable = true, remap = false)
    private static void isEntityInHerbalBathBlock(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (CoreUtils.isEntityInBathtub(entity, BathtubBlock.LiquidType.HERBAL_BATH)) {
            cir.setReturnValue(true);
        }
    }
}
