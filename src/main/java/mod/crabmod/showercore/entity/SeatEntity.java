package mod.crabmod.showercore.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class SeatEntity extends Entity {
    public SeatEntity(EntityType<?> type, Level level) {
        super(type, level);
    }

    public SeatEntity(Level level, double x, double y, double z) {
        this(mod.crabmod.showercore.registers.EntityRegister.SEAT_ENTITY.get(), level);
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
        return new ClientboundAddEntityPacket(this);
    }
    
    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide) {
            if (this.tickCount > 5 && this.getPassengers().isEmpty()) {
                this.discard();
            }
        }
    }

    @Override
    public net.minecraft.world.phys.Vec3 getDismountLocationForPassenger(net.minecraft.world.entity.LivingEntity passenger) {
        net.minecraft.core.Direction direction = this.getDirection();
        net.minecraft.core.Direction[] directions = {direction, direction.getClockWise(), direction.getCounterClockWise(), direction.getOpposite()};
        
        for (net.minecraft.core.Direction dir : directions) {
            net.minecraft.world.phys.Vec3 vec3 = net.minecraft.world.entity.vehicle.DismountHelper.findSafeDismountLocation(passenger.getType(), this.level(), this.blockPosition().relative(dir), false);
            if (vec3 != null) {
                return vec3;
            }
        }
        
        return super.getDismountLocationForPassenger(passenger);
    }
}
