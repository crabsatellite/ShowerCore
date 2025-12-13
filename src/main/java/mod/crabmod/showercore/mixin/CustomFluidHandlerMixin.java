package mod.crabmod.showercore.mixin;

// import com.crabmod.hotbath.util.CustomFluidHandler;
import mod.crabmod.showercore.block.BathtubBlock;
import mod.crabmod.showercore.utils.CoreUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// @Mixin(CustomFluidHandler.class)
public class CustomFluidHandlerMixin {

    /*
    @Inject(method = "isPlayerInHotBathBlock", at = @At("HEAD"), cancellable = true, remap = false)
    private static void isPlayerInHotBathBlock(Player player, CallbackInfoReturnable<Boolean> cir) {
        if (CoreUtils.isEntityInAnyHotBathtub(player)) {
            cir.setReturnValue(true);
        }
    }
    */
}
