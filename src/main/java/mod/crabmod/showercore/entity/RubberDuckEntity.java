package mod.crabmod.showercore.entity;

import mod.crabmod.showercore.registers.EntityRegister;
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
        
        if (this.isInWater()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0, 0.01D, 0));
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.9, 0.9, 0.9));
            if (this.getDeltaMovement().y > 0.05) {
                 this.setDeltaMovement(this.getDeltaMovement().x, 0.05, this.getDeltaMovement().z);
            }
        } else {
            this.setDeltaMovement(this.getDeltaMovement().add(0, -0.04D, 0));
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.98, 0.98, 0.98));
        }
        
        this.move(MoverType.SELF, this.getDeltaMovement());
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
