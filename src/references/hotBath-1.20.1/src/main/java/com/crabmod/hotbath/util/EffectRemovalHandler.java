package com.crabmod.hotbath.util;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public class EffectRemovalHandler {
  public static void removeNegativeEffectsExceptUnluck(ServerPlayer player) {
    List<MobEffectInstance> activeEffects = new ArrayList<>(player.getActiveEffects());

    for (MobEffectInstance effectInstance : activeEffects) {
      MobEffect effect = effectInstance.getEffect();

      if (effect.getCategory() == MobEffectCategory.HARMFUL && effect != MobEffects.UNLUCK) {
        player.removeEffect(effect);
      }
    }
  }

  public static void removeNegativeEffectsExceptSlowAndUnluck(ServerPlayer player) {
    List<MobEffectInstance> activeEffects = new ArrayList<>(player.getActiveEffects());

    for (MobEffectInstance effectInstance : activeEffects) {
      MobEffect effect = effectInstance.getEffect();

      if (effect.getCategory() == MobEffectCategory.HARMFUL
              && effect != MobEffects.UNLUCK
              && effect != MobEffects.MOVEMENT_SLOWDOWN) {
        player.removeEffect(effect);
      }
    }
  }

  public static void removeNegativeEffects(ServerPlayer player) {
    List<MobEffectInstance> activeEffects = new ArrayList<>(player.getActiveEffects());

    for (MobEffectInstance effectInstance : activeEffects) {
      MobEffect effect = effectInstance.getEffect();

      if (effect.getCategory() == MobEffectCategory.HARMFUL) {
        player.removeEffect(effect);
      }
    }
  }

  public static void removeBadOmen(ServerPlayer player) {
    player.removeEffect(MobEffects.BAD_OMEN);
  }
}