package mod.crabmod.showercore.block.entity;

import com.crabmod.hotbath.custom_fluid.CustomFluidAPI;
import com.crabmod.hotbath.custom_fluid.CustomFluidDefinition;
import mod.crabmod.showercore.registers.BlockEntitiesRegister;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class BathtubBlockEntity extends BlockEntity {
    private static final String TAG_CUSTOM_FLUID_ID = "CustomFluidId";

    private final FluidTank fluidTank = new FluidTank(1000) {
        @Override
        protected void onContentsChanged() {
            setChanged();
            if (level != null && !level.isClientSide) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    };

    @Nullable
    private ResourceLocation customFluidId;

    public BathtubBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntitiesRegister.BATHTUB_BLOCK_ENTITY.get(), pos, state);
    }

    @Nullable
    public ResourceLocation getCustomFluidId() {
        return customFluidId;
    }

    public void setCustomFluidId(@Nullable ResourceLocation customFluidId) {
        this.customFluidId = customFluidId;
        setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
    }

    /**
     * Gets the custom fluid definition for the stored custom fluid ID.
     * @return Optional containing the definition if the ID is set and the fluid is registered
     */
    public Optional<CustomFluidDefinition> getCustomFluidDefinition() {
        if (customFluidId == null) {
            return Optional.empty();
        }
        return CustomFluidAPI.getFluidDefinition(customFluidId);
    }

    @Override
    public void loadAdditional(CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        fluidTank.readFromNBT(registries, tag);
        if (tag.contains(TAG_CUSTOM_FLUID_ID)) {
            customFluidId = ResourceLocation.tryParse(tag.getString(TAG_CUSTOM_FLUID_ID));
        } else {
            customFluidId = null;
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        fluidTank.writeToNBT(registries, tag);
        if (customFluidId != null) {
            tag.putString(TAG_CUSTOM_FLUID_ID, customFluidId.toString());
        }
    }

    @Override
    public CompoundTag getUpdateTag(net.minecraft.core.HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public FluidTank getFluidTank() {
        return fluidTank;
    }
}
