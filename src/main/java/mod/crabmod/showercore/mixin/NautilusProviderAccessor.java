package mod.crabmod.showercore.mixin;

import net.minecraft.client.particle.FlyTowardsPositionParticle;
import net.minecraft.client.particle.SpriteSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(FlyTowardsPositionParticle.NautilusProvider.class)
public interface NautilusProviderAccessor {
    @Invoker("<init>")
    static FlyTowardsPositionParticle.NautilusProvider create(SpriteSet spriteSet) {
        throw new UnsupportedOperationException();
    }
}
