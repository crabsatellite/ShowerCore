package mod.crabmod.showercore.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import mod.crabmod.showercore.block.BathtubBlock;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.core.Holder;

public class SeatEntity extends Entity {
    public SeatEntity(EntityType<?> type, Level level) {
        super(type, level);
    }

    public SeatEntity(Level level, double x, double y, double z) {
        this(mod.crabmod.showercore.registers.EntityRegister.SEAT_ENTITY.get(), level);
        this.setPos(x, y, z);
    }

    @Override
    protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {}

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {}

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {}

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity serverEntity) {
        return new ClientboundAddEntityPacket(this, serverEntity);
    }
    
    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide) {
            if (this.tickCount > 5 && this.getPassengers().isEmpty()) {
                this.discard();
            }
            
            if (this.tickCount % 20 == 0) {
                 BlockPos pos = this.blockPosition();
                 BlockState state = this.level().getBlockState(pos);
                 if (state.getBlock() instanceof BathtubBlock) {
                     BathtubBlock.LiquidType liquid = state.getValue(BathtubBlock.LIQUID);
                     for (Entity passenger : this.getPassengers()) {
                         if (passenger instanceof LivingEntity living) {
                             applyBathEffects(living, liquid);
                         }
                     }
                 }
            }
        }
    }

    private void applyBathEffects(LivingEntity entity, BathtubBlock.LiquidType liquid) {
        Holder<MobEffect> effect = null;
        switch (liquid) {
            case HOT_WATER -> effect = MobEffects.MOVEMENT_SPEED;
            case HERBAL_BATH -> effect = MobEffects.REGENERATION;
            case HONEY_BATH -> effect = MobEffects.ABSORPTION;
            case MILK_BATH -> effect = MobEffects.SATURATION;
            case PEONY_BATH -> effect = MobEffects.LUCK;
            case ROSE_BATH -> effect = MobEffects.DAMAGE_BOOST;
            default -> {}
        }
        
        if (effect != null) {
            entity.addEffect(new MobEffectInstance(effect, 260, 0, false, false));
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
