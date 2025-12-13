package mod.crabmod.showercore.entity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import mod.crabmod.showercore.base.BaseShowerHeadBlockEntity;
import mod.crabmod.showercore.registers.BlockEntitiesRegister;
import mod.crabmod.showercore.client.ShowerHeadClientHelper;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.api.distmarker.Dist;
import mod.crabmod.showercore.utils.CoreUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraft.world.effect.MobEffect;

public class ShowerHeadContainerEntity extends BaseShowerHeadBlockEntity {
  private boolean effectActive = false;
  private Object bathEffectUtils;
  private final Map<UUID, Integer> timeUnderShower = new HashMap<>();

  public ShowerHeadContainerEntity(BlockPos pos, BlockState state) {
    super(BlockEntitiesRegister.RAIN_SHOWER_HEAD_CONTAINER.get(), pos, state);
  }

  @Override
  protected Component getDefaultName() {
    return Component.translatable("container.shower_head");
  }

  // 切换效果状态的方法
  public void toggleEffect() {
    effectActive = !effectActive;
    this.setChanged();
  }

  // 获取当前效果状态的方法
  public boolean isEffectActive() {
    return effectActive;
  }

  public net.minecraft.core.particles.SimpleParticleType getParticleType() {
    return CoreUtils.getParticleForCore(this.getItem(0));
  }

  @Override
  public net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket getUpdatePacket() {
    return net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket.create(this);
  }

  @Override
  public net.minecraft.nbt.CompoundTag getUpdateTag() {
    return this.saveWithoutMetadata();
  }

  @Override
  public void load(net.minecraft.nbt.CompoundTag tag) {
    super.load(tag);
    this.effectActive = tag.getBoolean("EffectActive");
    if (this.level != null && this.level.isClientSide) {
      if (this.effectActive) {
        this.startEffect();
      } else {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ShowerHeadClientHelper.stopBathEffect(this.bathEffectUtils));
      }
    }
  }

  @Override
  public void onDataPacket(net.minecraft.network.Connection net, net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket pkt) {
    net.minecraft.nbt.CompoundTag tag = pkt.getTag();
    if (tag != null) {
        this.load(tag);
    }
  }

  @Override
  protected void saveAdditional(net.minecraft.nbt.CompoundTag tag) {
    super.saveAdditional(tag);
    tag.putBoolean("EffectActive", this.effectActive);
  }

  @Override
  public void onLoad() {
    super.onLoad();
    if (this.level != null && this.level.isClientSide) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            if (this.bathEffectUtils == null) {
                this.bathEffectUtils = ShowerHeadClientHelper.createBathEffectUtils();
            }
        });
        if (this.effectActive) {
            this.startEffect();
        }
    }
  }

  public void startEffect() {
      if (this.level != null && this.level.isClientSide && this.effectActive) {
            net.minecraft.world.level.block.Block block = this.getBlockState().getBlock();
            String name = ForgeRegistries.BLOCKS.getKey(block).getPath();
            
            final double width = name.contains("compact_shower_head") ? 0.375 : 0.5625;
            final double depth = name.contains("compact_shower_head") ? 0.375 : 0.3125;
            final double centerX = 0.5;
            final double centerZ = name.contains("compact_shower_head") ? 0.53125 : 0.5;
            final double height = 0.48;

            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                if (this.bathEffectUtils == null) {
                    this.bathEffectUtils = ShowerHeadClientHelper.createBathEffectUtils();
                }
                ShowerHeadClientHelper.renderBathWater(this.bathEffectUtils, this.level, this.worldPosition, this::getParticleType, width, depth, centerX, centerZ, height);
            });
      }
  }

  public void stopEffect() {
    if (this.level != null && this.level.isClientSide) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ShowerHeadClientHelper.stopBathEffect(this.bathEffectUtils));
    }
  }

  public void shutdownEffect() {
    if (this.level != null && this.level.isClientSide) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ShowerHeadClientHelper.shutdown(this.bathEffectUtils));
    }
  }

  @Override
  public void setRemoved() {
    super.setRemoved();
    this.shutdownEffect();
  }

  public static void tick(Level level, BlockPos pos, BlockState state, ShowerHeadContainerEntity entity) {
    if (level.isClientSide) return;

    if (!entity.isEffectActive() || entity.isEmpty()) {
      entity.timeUnderShower.clear();
      return;
    }

    ItemStack core = entity.getItem(0);
    if (core.isEmpty()) {
        entity.timeUnderShower.clear();
        return;
    }

    // Calculate the effective height of the shower
    int maxRange = 10;
    double bottomY = pos.getY() - 1 - maxRange;
    
    BlockPos.MutableBlockPos checkPos = pos.mutable();
    checkPos.move(net.minecraft.core.Direction.DOWN);
    
    for (int i = 1; i <= maxRange; i++) {
        checkPos.move(net.minecraft.core.Direction.DOWN);
        BlockState checkState = level.getBlockState(checkPos);
        net.minecraft.world.phys.shapes.VoxelShape collisionShape = checkState.getCollisionShape(level, checkPos);
        if (!collisionShape.isEmpty()) {
            bottomY = checkPos.getY() + collisionShape.max(net.minecraft.core.Direction.Axis.Y);
            break;
        }
    }
    
    // Define area: slightly smaller than 1x1 to avoid hitting entities through walls, but centered.
    AABB detectionBox = new AABB(
        pos.getX() + 0.2, bottomY, pos.getZ() + 0.2, 
        pos.getX() + 0.8, pos.getY() + 0.48, pos.getZ() + 0.8
    );

    List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, detectionBox);
    Set<UUID> currentEntities = new HashSet<>();

    for (LivingEntity livingEntity : entities) {
      UUID uuid = livingEntity.getUUID();
      currentEntities.add(uuid);
      int time = entity.timeUnderShower.getOrDefault(uuid, 0);
      time++;
      entity.timeUnderShower.put(uuid, time);

      if (time >= 100) { // 5 seconds
        applyEffects(livingEntity, core);
      }
    }

    entity.timeUnderShower.keySet().retainAll(currentEntities);
  }

  private static void applyEffects(LivingEntity entity, ItemStack core) {
    String path = ForgeRegistries.ITEMS.getKey(core.getItem()).getPath();
    long gameTime = entity.level().getGameTime();
    CompoundTag data = entity.getPersistentData();

    // Prevent double processing in the same tick for the same core type
    String tickKey = "showercore.tick." + path;
    if (data.getLong(tickKey) == gameTime) return;
    data.putLong(tickKey, gameTime);
    
    // Buff Refresh Logic (Every 10s)
    boolean shouldRefreshBuffs = false;
    String buffKey = "showercore.buff_refresh." + path;
    if (gameTime - data.getLong(buffKey) >= 200) {
        shouldRefreshBuffs = true;
        data.putLong(buffKey, gameTime);
    }

    switch (path) {
        case "hot_water_core":
            // Speed II
            if (shouldRefreshBuffs) addStackingEffect(entity, MobEffects.MOVEMENT_SPEED, 400, 1);
            break;
        case "milk_bath_core":
            // Regeneration II
            if (shouldRefreshBuffs) {
                addStackingEffect(entity, MobEffects.REGENERATION, 400, 1);
                cureNegativeEffects(entity);
            }
            // Hunger: 1 every 15s (300 ticks)
            if (gameTime - data.getLong("showercore.last.milk_hunger") >= 300) {
                entity.addEffect(new MobEffectInstance(MobEffects.SATURATION, 1, 0, true, false));
                data.putLong("showercore.last.milk_hunger", gameTime);
            }
            break;
        case "herbal_bath_core":
            // Regeneration II, Resistance II
            if (shouldRefreshBuffs) {
                addStackingEffect(entity, MobEffects.REGENERATION, 400, 1);
                addStackingEffect(entity, MobEffects.DAMAGE_RESISTANCE, 400, 1);
                cureNegativeEffects(entity);
            }
            
            // Undead Damage: 1 every 2s (40 ticks)
            if (entity.isInvertedHealAndHarm()) {
                if (gameTime - data.getLong("showercore.last.herbal_damage") >= 40) {
                    entity.hurt(entity.damageSources().magic(), 1.0f);
                    data.putLong("showercore.last.herbal_damage", gameTime);
                }
            }
            break;
        case "peony_bath_core":
            // Regeneration II, Luck II, Haste II
            if (shouldRefreshBuffs) {
                addStackingEffect(entity, MobEffects.REGENERATION, 400, 1);
                addStackingEffect(entity, MobEffects.LUCK, 400, 1);
                addStackingEffect(entity, MobEffects.DIG_SPEED, 400, 1);
                entity.removeEffect(MobEffects.BAD_OMEN);
                cureNegativeEffects(entity);
            }
            break;
        case "honey_bath_core":
            // Regeneration II, Absorption IV
            if (shouldRefreshBuffs) {
                addStackingEffect(entity, MobEffects.REGENERATION, 400, 1);
                addStackingEffect(entity, MobEffects.ABSORPTION, 400, 3);
            }
            // Hunger: 1 every 4s (80 ticks)
            if (gameTime - data.getLong("showercore.last.honey_hunger") >= 80) {
                entity.addEffect(new MobEffectInstance(MobEffects.SATURATION, 1, 0, true, false));
                data.putLong("showercore.last.honey_hunger", gameTime);
            }
            break;
        case "rose_bath_core":
            // Regeneration II, Strength II
            if (shouldRefreshBuffs) {
                addStackingEffect(entity, MobEffects.REGENERATION, 400, 1);
                addStackingEffect(entity, MobEffects.DAMAGE_BOOST, 400, 1);
                entity.removeEffect(MobEffects.BAD_OMEN);
                cureNegativeEffects(entity);
            }
            break;
    }
  }

  private static void addStackingEffect(LivingEntity entity, MobEffect effect, int durationIncrement, int amplifier) {
    CompoundTag data = entity.getPersistentData();
    String key = "showercore.last_stack." + effect.getDescriptionId();
    long gameTime = entity.level().getGameTime();
    
    // Prevent applying same effect multiple times in short window (e.g. 100 ticks)
    if (gameTime - data.getLong(key) < 100) return; 
    data.putLong(key, gameTime);

    MobEffectInstance current = entity.getEffect(effect);
    int currentDuration = (current != null) ? current.getDuration() : 0;
    int newDuration = Math.min(currentDuration + durationIncrement, 2400); // Cap at 2 mins (2400 ticks)
    if (newDuration < durationIncrement) newDuration = durationIncrement;
    
    entity.addEffect(new MobEffectInstance(effect, newDuration, amplifier, true, false));
  }

  private static void cureNegativeEffects(LivingEntity entity) {
    entity.removeEffect(MobEffects.POISON);
    entity.removeEffect(MobEffects.WITHER);
    entity.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);
    entity.removeEffect(MobEffects.WEAKNESS);
    entity.removeEffect(MobEffects.CONFUSION);
    entity.removeEffect(MobEffects.BLINDNESS);
    entity.removeEffect(MobEffects.HUNGER);
  }
}
