package mod.crabmod.showercore.entity;

import mod.crabmod.showercore.registers.EntityRegister;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

public class RubberDuckEntity extends Entity {

    public RubberDuckEntity(EntityType<?> type, Level level) {
        super(type, level);
    }

    public RubberDuckEntity(Level level, double x, double y, double z) {
        this(EntityRegister.RUBBER_DUCK.get(), level);
        this.setPos(x, y, z);
    }

    @Override
    protected void defineSynchedData() {}

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {}

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {}

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public void tick() {
        super.tick();
        
        double fluidHeight = this.getFluidHeight(FluidTags.WATER);
        
        // Gravity
        this.setDeltaMovement(this.getDeltaMovement().add(0, -0.04D, 0));

        if (fluidHeight > 0) {
            // Buoyancy: proportional to submerged depth
            // Target: float higher
            double buoyancy = fluidHeight * 1.5D;
            this.setDeltaMovement(this.getDeltaMovement().add(0, buoyancy, 0));
            
            // Water drag, higher Y drag to prevent oscillation
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.9, 0.75, 0.9));
            
            // Custom particles only when moving horizontally
            if (this.level().isClientSide) {
                Vec3 velocity = this.getDeltaMovement();
                if (velocity.x * velocity.x + velocity.z * velocity.z > 0.001) {
                     this.level().addParticle(ParticleTypes.SPLASH, this.getX(), this.getY(), this.getZ(), 0, 0, 0);
                }
            }
        } else {
            // Air drag
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.98, 0.98, 0.98));
        }
        
        this.move(MoverType.SELF, this.getDeltaMovement());
    }

    @Override
    public void updateSwimming() {
        // Only allow vanilla swimming logic (particles) when moving horizontally
        Vec3 velocity = this.getDeltaMovement();
        if (velocity.x * velocity.x + velocity.z * velocity.z > 0.001) {
            super.updateSwimming();
        }
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        if (!this.level().isClientSide) {
            Vec3 look = player.getLookAngle();
            this.setDeltaMovement(look.x * 0.3, 0.1, look.z * 0.3);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.CONSUME;
    }
}
