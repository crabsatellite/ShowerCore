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

public class BathEffectUtils {
    private final ScheduledExecutorService scheduler;
    private final AtomicBoolean isEffectActive;

    public BathEffectUtils() {
        this.scheduler = Executors.newScheduledThreadPool(2);
        this.isEffectActive = new AtomicBoolean(false);
    }

    public void renderBathWater(Level level, BlockPos pos, java.util.function.Supplier<ParticleOptions> particleTypeSupplier) {
        renderBathWater(level, pos, 2, 1.4, 5, particleTypeSupplier);
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

    public void renderBathWater(Level level, BlockPos pos, int particleInterval, double invisibleHeightMinOffset, double invisibleHeightMaxOffset, java.util.function.Supplier<ParticleOptions> particleTypeSupplier) {
        // 如果效果已激活，则返回，避免重复开启
        if (isEffectActive.get()) {
            return;
        }
        isEffectActive.set(true);

        // 定期生成粒子效果
        scheduler.scheduleAtFixedRate(
                () -> {
                    if (!isEffectActive.get()) {
                        return; // 如果效果已停用，跳过任务
                    }

                    Minecraft.getInstance().execute(() -> {
                        // 设置相对高度范围
                        double invisibleHeightMin = pos.getY() + invisibleHeightMinOffset;
                        double invisibleHeightMax = pos.getY() + invisibleHeightMaxOffset;

                        // 将高度范围设置到 ThreadLocal
                        ShowerParticle.INVISIBLE_HEIGHT_MIN.set(invisibleHeightMin);
                        ShowerParticle.INVISIBLE_HEIGHT_MAX.set(invisibleHeightMax);

                        for (int i = 0; i < 1; i++) {
                            double offsetX = 0.5 - (level.random.nextDouble() - 0.5) * 0.3;
                            double offsetY = 2;
                            double offsetZ = 0.5 - (level.random.nextDouble() - 0.5) * 0.3;
                            double velocityY = -3;

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
        // 定期播放声音效果
        scheduler.scheduleAtFixedRate(
                () -> {
                    if (!isEffectActive.get()) {
                        return; // 如果效果已停用，跳过任务
                    }

                    Minecraft.getInstance().execute(() -> {
                        level.playLocalSound(
                                pos.getX(),
                                pos.getY(),
                                pos.getZ(),
                                SoundEvents.WEATHER_RAIN,
                                SoundSource.BLOCKS,
                                1.0F,
                                1.0F,
                                false
                        );
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
    }
}
