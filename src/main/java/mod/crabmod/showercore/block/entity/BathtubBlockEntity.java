package mod.crabmod.showercore.block.entity;

import com.crabmod.hotbath.custom_fluid.CustomFluidAPI;
import com.crabmod.hotbath.custom_fluid.CustomFluidDefinition;
import mod.crabmod.showercore.registers.BlockEntitiesRegister;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class BathtubBlockEntity extends BlockEntity {
    private static final String TAG_CUSTOM_FLUID_ID = "CustomFluidId";
    public static final String TAG_MATERIAL_BLOCK_ID = "MaterialBlockId";

    private final FluidTank fluidTank = new FluidTank(1000) {
        @Override
        protected void onContentsChanged() {
            setChanged();
            if (level != null && !level.isClientSide) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    };
    private final LazyOptional<IFluidHandler> fluidHandler = LazyOptional.of(() -> fluidTank);

    @Nullable
    private ResourceLocation customFluidId;
    @Nullable
    private ResourceLocation materialBlockId;

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
        if (level != null) {
            refreshCustomFluidLighting(true);
        }
    }

    @Nullable
    public ResourceLocation getMaterialBlockId() {
        return materialBlockId;
    }

    public void setMaterialBlockId(@Nullable ResourceLocation materialBlockId) {
        if (java.util.Objects.equals(this.materialBlockId, materialBlockId)) {
            return;
        }
        this.materialBlockId = materialBlockId;
        setChanged();
        requestModelDataUpdate();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    public void onLoad() {
        super.onLoad();
        // Saved chunks load BE data AFTER the chunk's initial light pass, so the recheck
        // we do in setCustomFluidId never fired during the load that produced the cached
        // lightmap. Re-trigger here so a luminous custom fluid lights its bathtub on world
        // re-entry without requiring a neighboring block update.
        if (level != null && customFluidId != null) {
            refreshCustomFluidLighting(true);
        }
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        super.handleUpdateTag(tag);
        // Bulk chunk-data path: client-side recheck after the chunk's lightmap has been built.
        if (level != null && level.isClientSide && customFluidId != null) {
            refreshCustomFluidLighting(false);
        }
    }

    @Override
    public void onDataPacket(net.minecraft.network.Connection net,
                             ClientboundBlockEntityDataPacket pkt) {
        super.onDataPacket(net, pkt);
        // Single-BE update path (live pour into an already-loaded chunk): handleUpdateTag
        // does NOT fire here, so we need an explicit recheck for emissive custom fluids.
        if (level != null && level.isClientSide && customFluidId != null) {
            refreshCustomFluidLighting(false);
        }
    }

    private void refreshCustomFluidLighting(boolean notifyServerClients) {
        if (level == null) {
            return;
        }
        if (level.isClientSide) {
            level.setBlocksDirty(getBlockPos(), Blocks.AIR.defaultBlockState(), getBlockState());
        }
        level.getLightEngine().checkBlock(getBlockPos());
        if (notifyServerClients && !level.isClientSide) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
    }

    public Optional<CustomFluidDefinition> getCustomFluidDefinition() {
        if (customFluidId == null) {
            return Optional.empty();
        }
        return CustomFluidAPI.getFluidDefinition(customFluidId);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        fluidTank.readFromNBT(tag);
        if (tag.contains(TAG_CUSTOM_FLUID_ID)) {
            customFluidId = ResourceLocation.tryParse(tag.getString(TAG_CUSTOM_FLUID_ID));
        } else {
            customFluidId = null;
        }
        if (tag.contains(TAG_MATERIAL_BLOCK_ID)) {
            materialBlockId = ResourceLocation.tryParse(tag.getString(TAG_MATERIAL_BLOCK_ID));
        } else {
            materialBlockId = null;
        }
        requestModelDataUpdate();
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        fluidTank.writeToNBT(tag);
        if (customFluidId != null) {
            tag.putString(TAG_CUSTOM_FLUID_ID, customFluidId.toString());
        }
        if (materialBlockId != null) {
            tag.putString(TAG_MATERIAL_BLOCK_ID, materialBlockId.toString());
        }
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        saveAdditional(tag);
        return tag;
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable net.minecraft.core.Direction side) {
        if (cap == ForgeCapabilities.FLUID_HANDLER) {
            return fluidHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        fluidHandler.invalidate();
    }

    public FluidTank getFluidTank() {
        return fluidTank;
    }
}
