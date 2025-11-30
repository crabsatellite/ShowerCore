package mod.crabmod.showercore.entity;

import mod.crabmod.showercore.block.BathtubBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class FaucetInteractionEntity extends Entity {
    public FaucetInteractionEntity(EntityType<?> type, Level level) {
        super(type, level);
    }

    public FaucetInteractionEntity(Level level, double x, double y, double z) {
        this(mod.crabmod.showercore.registers.EntityRegister.FAUCET_ENTITY.get(), level);
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
    public boolean isPickable() {
        return true;
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        if (!player.isShiftKeyDown() || !player.getItemInHand(hand).isEmpty()) {
            return InteractionResult.PASS;
        }

        if (this.level().isClientSide) return InteractionResult.SUCCESS;
        
        BlockPos pos = this.blockPosition();
        BlockState state = this.level().getBlockState(pos);
        
        if (state.getBlock() instanceof BathtubBlock) {
            BathtubBlock.LiquidType currentLiquid = state.getValue(BathtubBlock.LIQUID);
            boolean isRunning = state.getValue(BathtubBlock.RUNNING);
            
            if (!isRunning && currentLiquid != BathtubBlock.LiquidType.EMPTY) {
                this.level().setBlock(pos, state.setValue(BathtubBlock.RUNNING, true), 3);
                this.level().playSound(null, pos, SoundEvents.WATER_AMBIENT, SoundSource.BLOCKS, 1.0F, 1.0F);
            } else {
                this.level().setBlock(pos, state.setValue(BathtubBlock.RUNNING, false), 3);
            }
            return InteractionResult.CONSUME;
        } else {
            this.discard(); // If block is gone, remove entity
            return InteractionResult.PASS;
        }
    }
}
