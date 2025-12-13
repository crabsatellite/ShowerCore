package mod.crabmod.showercore.registers;

import mod.crabmod.showercore.ShowerCore;
import mod.crabmod.showercore.entity.FaucetInteractionEntity;
import mod.crabmod.showercore.entity.SeatEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public class EntityRegister {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, ShowerCore.MODID);

    public static final DeferredHolder<EntityType<?>, EntityType<SeatEntity>> SEAT_ENTITY = ENTITY_TYPES.register("seat",
            () -> EntityType.Builder.<SeatEntity>of(SeatEntity::new, MobCategory.MISC)
                    .sized(0.0F, 0.0F)
                    .build("seat"));

    public static final DeferredHolder<EntityType<?>, EntityType<FaucetInteractionEntity>> FAUCET_ENTITY = ENTITY_TYPES.register("faucet",
            () -> EntityType.Builder.<FaucetInteractionEntity>of(FaucetInteractionEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .build("faucet"));

    public static final DeferredHolder<EntityType<?>, EntityType<mod.crabmod.showercore.entity.RubberDuckEntity>> RUBBER_DUCK = ENTITY_TYPES.register("rubber_duck",
            () -> EntityType.Builder.<mod.crabmod.showercore.entity.RubberDuckEntity>of(mod.crabmod.showercore.entity.RubberDuckEntity::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .build("rubber_duck"));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
