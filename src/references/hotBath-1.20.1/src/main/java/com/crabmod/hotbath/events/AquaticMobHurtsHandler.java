package com.crabmod.hotbath.events;

import static com.crabmod.hotbath.HotBath.MOD_ID;

import com.crabmod.hotbath.fluid_blocks.AbstractHotbathBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.animal.Squid;
import net.minecraft.world.entity.animal.TropicalFish;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MOD_ID)
public class AquaticMobHurtsHandler {

  @SubscribeEvent
  public static void onEntityUpdate(LivingEvent.LivingTickEvent event) {
    LivingEntity entity = event.getEntity();
    Level world = entity.level();
    BlockPos pos = entity.blockPosition();

    // Check if the entity is in a hot bath block
    boolean inHotBath = world.getBlockState(pos).getBlock() instanceof AbstractHotbathBlock;

    if (inHotBath && isNonTropicalAquatic(entity)) {
      entity.hurt(world.damageSources().magic(), 1.0F);
    }
  }

  private static boolean isNonTropicalAquatic(LivingEntity livingEntity) {
    return (livingEntity instanceof AbstractFish && !(livingEntity instanceof TropicalFish))
        || livingEntity instanceof Squid;
  }
}
