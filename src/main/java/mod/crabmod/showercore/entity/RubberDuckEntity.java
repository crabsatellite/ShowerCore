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
        
        // Calculate fluid height for ANY fluid
        net.minecraft.world.phys.AABB aabb = this.getBoundingBox().deflate(0.001D);
        int minX = net.minecraft.util.Mth.floor(aabb.minX);
        int maxX = net.minecraft.util.Mth.ceil(aabb.maxX);
        int minY = net.minecraft.util.Mth.floor(aabb.minY);
        int maxY = net.minecraft.util.Mth.ceil(aabb.maxY);
        int minZ = net.minecraft.util.Mth.floor(aabb.minZ);
        int maxZ = net.minecraft.util.Mth.ceil(aabb.maxZ);
        
        double fluidHeight = 0.0D;
        boolean isDestroyFluid = false;
        
        net.minecraft.core.BlockPos.MutableBlockPos mutablePos = new net.minecraft.core.BlockPos.MutableBlockPos();
        
        for(int k = minX; k < maxX; ++k) {
            for(int l = minY; l < maxY; ++l) {
                for(int i1 = minZ; i1 < maxZ; ++i1) {
                    mutablePos.set(k, l, i1);
                    net.minecraft.world.level.material.FluidState fluidstate = this.level().getFluidState(mutablePos);
                    if (!fluidstate.isEmpty()) {
                        if (mod.crabmod.showercore.Config.rubberDuckDestroyFluids.contains(fluidstate.getType())) {
                            isDestroyFluid = true;
                        }
                        
                        double height = (double)((float)l + fluidstate.getHeight(this.level(), mutablePos));
                        if (height >= aabb.minY) {
                            fluidHeight = Math.max(fluidHeight, height - aabb.minY);
                        }
                    }
                }
            }
        }

        if (isDestroyFluid) {
             if (!this.level().isClientSide) {
                this.discard();
                this.level().playSound(null, this.getX(), this.getY(), this.getZ(), net.minecraft.sounds.SoundEvents.GENERIC_BURN, this.getSoundSource(), 1.0F, 1.0F);
             }
             this.level().addParticle(ParticleTypes.LAVA, this.getX(), this.getY(), this.getZ(), 0, 0, 0);
             return;
        }
        
        // Gravity
        this.setDeltaMovement(this.getDeltaMovement().add(0, -0.04D, 0));

        if (fluidHeight > 0) {
            // Buoyancy: proportional to submerged depth
            // Target: float higher
            double buoyancy = fluidHeight * 2.5D;
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
            this.playSound(mod.crabmod.showercore.registers.SoundRegister.RUBBER_DUCK.get(), 1.0F, 1.0F);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.CONSUME;
    }
}
