package com.crabmod.hotbath.events.enter_fluid_events;

import static com.crabmod.hotbath.util.HealthRegenHandler.regenHealth;

import com.crabmod.hotbath.HotBath;
import com.crabmod.hotbath.util.CustomFluidHandler;
import com.crabmod.hotbath.util.EffectRemovalHandler;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = HotBath.MOD_ID)
public class PeonyBathEvents {
  static final String PEONY_BATH_ENTERED_NUMBER = "PeonyBathEnteredNumber";
  static final String PEONY_BATH_STAYED_TIME = "PeonyBathStayedTime";
  static final String HAS_ENTERED_PEONY_BATH = "HasEnteredPeonyBath";
  static final String PEONY_BATH_EXITED_TIME = "PeonyBathExitedTime";
  private static final int TICK_NUMBER = 20;
  private static final int PEONY_BATH_ENTERED_COUNT_TRIGGER_NUMBER = 100;
  private static final int PEONY_BATH_STAYED_EFFECT_TRIGGER_TIME_SECONDS = 5;
  private static final int KNOCKBACK_RESISTANCE_DURATION = 30 * TICK_NUMBER;
  private static final int ATTACK_SPEED_DURATION = 15 * TICK_NUMBER;
  private static final int LUCK_DURATION = 45 * TICK_NUMBER;
  private static final int LUCK_THRESHOLD = 50;

  private static final UUID ATTACK_SPEED_MODIFIER_UUID = UUID.randomUUID();
  private static final UUID KNOCKBACK_RESISTANCE_UUID = UUID.randomUUID();
  private static final int ATTRIBUTE_REMOVE_DELAY_TICKS = 15 * TICK_NUMBER;

  // enter hot water block event
  @SubscribeEvent
  public static void enterPeonyBathEvents(LivingEvent.LivingTickEvent event) {
    enterFluidEvents(
        event,
        PEONY_BATH_ENTERED_COUNT_TRIGGER_NUMBER,
        PEONY_BATH_STAYED_EFFECT_TRIGGER_TIME_SECONDS,
        PEONY_BATH_ENTERED_NUMBER,
        PEONY_BATH_STAYED_TIME,
        HAS_ENTERED_PEONY_BATH);
  }

  public static void enterFluidEvents(
      LivingEvent.LivingTickEvent event,
      int enteredCountTriggerNumber,
      int stayedEffectTriggerTime,
      String enteredNumberInPeonyBath,
      String peonyBathStayedTime,
      String hasEnteredPeonyBath) {
    if (event.getEntity() instanceof ServerPlayer player) {
      CompoundTag playerData = player.getPersistentData();
      boolean isInPeonyBath = CustomFluidHandler.isPlayerInPeonyBathBlock(player);

      if (isInPeonyBath && player.isAlive()) {
        if (!playerData.getBoolean(hasEnteredPeonyBath)) {
          int enteredCount = playerData.getInt(enteredNumberInPeonyBath) + 1;
          playerData.putInt(enteredNumberInPeonyBath, enteredCount);
          playerData.putBoolean(hasEnteredPeonyBath, true);
        }

        int hotBathTime = playerData.getInt(peonyBathStayedTime) + 1;
        playerData.putInt(peonyBathStayedTime, hotBathTime);

        regenHealth(0.25F, 2, player);

        if (playerData.getInt(peonyBathStayedTime) >= 15 * TICK_NUMBER) {
          applyAttributeModifier(
              player,
              Attributes.KNOCKBACK_RESISTANCE,
              KNOCKBACK_RESISTANCE_UUID,
              0.05,
              AttributeModifier.Operation.ADDITION);
          applyAttributeModifier(
              player,
              Attributes.ATTACK_SPEED,
              ATTACK_SPEED_MODIFIER_UUID,
              0.10,
              AttributeModifier.Operation.MULTIPLY_TOTAL);
          EffectRemovalHandler.removeNegativeEffects(player);
          EffectRemovalHandler.removeBadOmen(player);
        }

        if (playerData.getInt(enteredNumberInPeonyBath) >= LUCK_THRESHOLD) {
          player.addEffect(
              new MobEffectInstance(MobEffects.LUCK, LUCK_DURATION, 45, false, false, true));
        }

        playerData.putInt(PEONY_BATH_EXITED_TIME, 0);
      } else {
        if (playerData.getBoolean(hasEnteredPeonyBath)) {
          playerData.putBoolean(hasEnteredPeonyBath, false);
        }

        playerData.putInt(PEONY_BATH_EXITED_TIME, playerData.getInt(PEONY_BATH_EXITED_TIME) + 1);

        if (playerData.getInt(PEONY_BATH_EXITED_TIME) >= 15 * TICK_NUMBER) {
          // Remove attribute modifier
          removeAttributeModifier(player, Attributes.ATTACK_SPEED, ATTACK_SPEED_MODIFIER_UUID);
        }

        if (playerData.getInt(PEONY_BATH_EXITED_TIME) >= 30 * TICK_NUMBER) {
          removeAttributeModifier(
              player, Attributes.KNOCKBACK_RESISTANCE, KNOCKBACK_RESISTANCE_UUID);
        }

        playerData.putInt(peonyBathStayedTime, 0);
      }
    }
  }

  private static void applyAttributeModifier(
      ServerPlayer player,
      Attribute attribute,
      UUID uuid,
      double value,
      AttributeModifier.Operation operation) {
    AttributeInstance attributeInstance = player.getAttribute(attribute);

    if (attributeInstance != null) {
      AttributeModifier modifier =
          new AttributeModifier(uuid, attribute.getDescriptionId(), value, operation);

      if (attributeInstance.getModifier(modifier.getId()) != null) {
        attributeInstance.removeModifier(modifier.getId());
      }

      attributeInstance.addPermanentModifier(modifier);
    }
  }

  private static void removeAttributeModifier(ServerPlayer player, Attribute attribute, UUID uuid) {
    AttributeInstance attributeInstance = player.getAttribute(attribute);

    if (attributeInstance != null && attributeInstance.getModifier(uuid) != null) {
      attributeInstance.removeModifier(uuid);
    }
  }
}
