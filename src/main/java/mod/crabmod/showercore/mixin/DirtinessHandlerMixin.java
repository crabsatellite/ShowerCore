package mod.crabmod.showercore.mixin;

import com.crabmod.hotbath.dirtiness.DirtinessHandler;
import mod.crabmod.showercore.utils.CoreUtils;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DirtinessHandler.class)
public class DirtinessHandlerMixin {

    // Only trigger hotBath's fast dirtiness drop for built-in cores / shower heads. CUSTOM-fluid
    // bathtubs use ShowerCore's own (slower) dirtiness path so pack authors can tune cleaning speed.
    @Inject(method = "isInHotBathFluid", at = @At("HEAD"), cancellable = true, remap = false)
    private static void isInHotBathFluid(ServerPlayer player, CallbackInfoReturnable<Boolean> cir) {
        if (CoreUtils.isPlayerInShowerCoreHotWaterNonCustom(player)) {
            cir.setReturnValue(true);
        }
    }
}
