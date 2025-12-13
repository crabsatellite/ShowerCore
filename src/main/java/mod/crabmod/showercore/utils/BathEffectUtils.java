package mod.crabmod.showercore.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import com.crabmod.hotbath.registers.ParticleRegister;
import mod.crabmod.showercore.particle.ShowerParticle;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundEvent;

public class BathEffectUtils {
    private ScheduledExecutorService scheduler;
    private final AtomicBoolean isEffectActive;
    private final List<FadeableSoundInstance> activeSounds = new ArrayList<>();

    public BathEffectUtils() {
        this.isEffectActive = new AtomicBoolean(false);
    }

    private void ensureScheduler() {
        if (this.scheduler == null || this.scheduler.isShutdown()) {
            this.scheduler = Executors.newScheduledThreadPool(2, r -> {
                Thread t = new Thread(r);
                t.setDaemon(true); // Ensure threads are daemon threads
                return t;
            });
        }
    }

    public void renderBathWater(Level level, BlockPos pos, java.util.function.Supplier<ParticleOptions> particleTypeSupplier, double width, double depth, double centerX, double centerZ, double height) {
        if (isEffectActive.get()) return;
        // Adjusted invisible height offsets to account for the model being shifted down by 1 block
        // Original: 1.4 and 5
        // New: 0.4 and 4 (shifted down by 1.0)
        renderBathWater(level, pos, 2, 0.4, 4, particleTypeSupplier, width, depth, centerX, centerZ, height);
        playBathSound(level, 200, pos);
    }

    private void generateOuterSteamParticles(Level worldIn, BlockPos pos, RandomSource rand, int radius) {
        List<BlockPos> airPositions = new ArrayList<>();

        // 遍历立方体范围，找到最外层的空气块，并存储到列表中
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    // 确定是否在最外层
                    if (dx == -radius || dx == radius || dy == -radius || dy == radius || dz == -radius || dz == radius) {
                        BlockPos checkPos = pos.offset(dx, dy, dz);

                        if (worldIn.getBlockState(checkPos).isAir()) {
                            airPositions.add(checkPos); // 添加符合条件的位置
                        }
                    }
                }
            }
        }

        // 如果找到符合条件的位置，则随机选择一个生成粒子
        if (!airPositions.isEmpty()) {
            BlockPos selectedPos = airPositions.get(rand.nextInt(airPositions.size()));
            worldIn.addParticle(
                    (ParticleOptions) ParticleRegister.STEAM_PARTICLE.get(),
                    selectedPos.getX() + rand.nextDouble(),
                    selectedPos.getY() + rand.nextDouble(),
                    selectedPos.getZ() + rand.nextDouble(),
                    0.0,
                    0.02,
                    0.0
            );
        }
    }

    public void renderBathWater(Level level, BlockPos pos, int particleInterval, double invisibleHeightMinOffset, double invisibleHeightMaxOffset, java.util.function.Supplier<ParticleOptions> particleTypeSupplier, double width, double depth, double centerX, double centerZ, double height) {
        // 如果效果已激活，则返回，避免重复开启
        if (isEffectActive.get()) {
            return;
        }
        isEffectActive.set(true);
        ensureScheduler();

        // 定期生成粒子效果
        scheduler.scheduleAtFixedRate(
                () -> {
                    if (!isEffectActive.get()) {
                        return; // 如果效果已停用，跳过任务
                    }

                    if (Minecraft.getInstance().level != level) {
                        this.shutdown();
                        return; // 如果世界已更改，停止任务
                    }

                    if (Minecraft.getInstance().isPaused()) {
                        return;
                    }

                    Minecraft.getInstance().execute(() -> {
                        // 设置相对高度范围
                        double invisibleHeightMin = pos.getY() + invisibleHeightMinOffset;
                        double invisibleHeightMax = pos.getY() + invisibleHeightMaxOffset;

                        // 将高度范围设置到 ThreadLocal
                        ShowerParticle.INVISIBLE_HEIGHT_MIN.set(invisibleHeightMin);
                        ShowerParticle.INVISIBLE_HEIGHT_MAX.set(invisibleHeightMax);

                        for (int i = 0; i < 1; i++) {
                            double offsetX = centerX + (level.random.nextDouble() - 0.5) * width;
                            double offsetY = height;
                            double offsetZ = centerZ + (level.random.nextDouble() - 0.5) * depth;
                            double velocityY = 3;
                            // 添加粒子
                            level.addParticle(
                                    particleTypeSupplier.get(),
                                    pos.getX() + offsetX,
                                    pos.getY() + offsetY,
                                    pos.getZ() + offsetZ,
                                    0,
                                    velocityY,
                                    0
                            );

                            if (level.random.nextInt(5000) == 0) {
                                this.generateOuterSteamParticles(level, pos, level.random, 1);
                            }
                        }

                        // 清理 ThreadLocal 防止数据泄漏
                        ShowerParticle.INVISIBLE_HEIGHT_MIN.remove();
                        ShowerParticle.INVISIBLE_HEIGHT_MAX.remove();
                    });
                },
                0,
                particleInterval,
                TimeUnit.MILLISECONDS
        );
    }

    public void playBathSound(Level level, int soundInterval, BlockPos pos) {
        ensureScheduler();
        // 定期播放声音效果
        scheduler.scheduleAtFixedRate(
                () -> {
                    if (!isEffectActive.get()) {
                        return; // 如果效果已停用，跳过任务
                    }

                    if (Minecraft.getInstance().level != level) {
                        this.shutdown();
                        return; // 如果世界已更改，停止任务
                    }

                    if (Minecraft.getInstance().isPaused()) {
                        return;
                    }

                    Minecraft.getInstance().execute(() -> {
                        if (Minecraft.getInstance().level == null || !isEffectActive.get()) return;
                        
                        FadeableSoundInstance soundInstance = new FadeableSoundInstance(
                                SoundEvents.WEATHER_RAIN,
                                SoundSource.BLOCKS,
                                0.2F,
                                1.0F,
                                RandomSource.create(),
                                pos
                        );
                        Minecraft.getInstance().getSoundManager().play(soundInstance);
                        activeSounds.add(soundInstance);
                        
                        // Cleanup inactive sounds
                        activeSounds.removeIf(instance -> !Minecraft.getInstance().getSoundManager().isActive(instance));
                    });
                },
                0,
                soundInterval,
                TimeUnit.MILLISECONDS
        );
    }

    // 停止粒子效果
    public void stopBathEffect() {
        isEffectActive.set(false);
        if (scheduler != null) {
            scheduler.shutdownNow();
        }
        
        Minecraft.getInstance().execute(() -> {
            for (FadeableSoundInstance sound : activeSounds) {
                sound.triggerFadeOut();
            }
            activeSounds.clear();
        });
    }

    private static class FadeableSoundInstance extends AbstractTickableSoundInstance {
        private boolean fadingOut = false;
        private int fadeTicks = 0;
        private final int fadeDuration = 20; // 1 second
        private final float originalVolume;

        public FadeableSoundInstance(SoundEvent sound, SoundSource source, float volume, float pitch, RandomSource random, BlockPos pos) {
            super(sound, source, random);
            this.volume = volume;
            this.pitch = pitch;
            this.x = pos.getX() + 0.5;
            this.y = pos.getY() + 0.5;
            this.z = pos.getZ() + 0.5;
            this.originalVolume = volume;
        }

        @Override
        public void tick() {
            if (fadingOut) {
                fadeTicks++;
                if (fadeTicks >= fadeDuration) {
                    this.stop();
                } else {
                    this.volume = originalVolume * (1.0f - (float)fadeTicks / fadeDuration);
                }
            }
        }

        public void triggerFadeOut() {
            this.fadingOut = true;
        }
    }

    public void shutdown() {
        stopBathEffect();
        if (scheduler != null) {
            scheduler.shutdownNow();
        }
    }
}
