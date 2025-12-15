/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.light;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import tanukidecor.block.TallBlock;

public class EgyptianLampBlock extends TallBlock {

    public static final VoxelShape SHAPE_UPPER = Shapes.join(
            box(1, -1, 1, 15, 2, 15),
            box(3, -1, 3, 13, 2, 13),
            BooleanOp.ONLY_FIRST
    );
    public static final VoxelShape SHAPE_LOWER = Shapes.or(
            box(3, 6, 3, 13, 8, 13),
            box(4, 8, 4, 12, 10, 12),
            box(3, 10, 3, 13, 12, 13),
            box(1, 12, 1, 15, 15, 15),
            box(3, 0, 3, 5, 6, 5),
            box(3, 0, 11, 5, 6, 13),
            box(11, 0, 3, 13, 6, 5),
            box(11, 0, 11, 13, 6, 13));

    private final float fireDamage;

    public EgyptianLampBlock(int fireDamage, Properties pProperties) {
        super(SHAPE_UPPER, SHAPE_LOWER, pProperties);
        this.fireDamage = fireDamage;
    }

    @Override
    public boolean isBurning(BlockState state, BlockGetter level, BlockPos pos) {
        return state.getValue(TallBlock.HALF) == DoubleBlockHalf.UPPER && !state.getValue(WATERLOGGED);
    }

    @Override
    public void entityInside(BlockState pState, Level pLevel, BlockPos pPos, Entity pEntity) {
        if (pState.getValue(HALF) == DoubleBlockHalf.UPPER && !pState.getValue(WATERLOGGED)
                && !pEntity.fireImmune() && !((pEntity.position().y() + 3.0D / 16.0D) < pPos.getY())
                && pEntity instanceof LivingEntity livingEntity && !hasFrostWalker(livingEntity)) {
            pEntity.hurt(pLevel.damageSources().inFire(), this.fireDamage);
        }

        super.entityInside(pState, pLevel, pPos, pEntity);
    }

    /**
     * Checks if the entity has Frost Walker enchantment
     */
    private static boolean hasFrostWalker(LivingEntity entity) {
        if (entity.level().isClientSide()) {
            return false;
        }
        ResourceKey<Enchantment> frostWalkerKey = ResourceKey.create(Registries.ENCHANTMENT, 
                ResourceLocation.withDefaultNamespace("frost_walker"));
        return entity.level().registryAccess().registryOrThrow(Registries.ENCHANTMENT)
                .getHolder(frostWalkerKey)
                .map(holder -> EnchantmentHelper.getEnchantmentLevel(holder, entity) > 0)
                .orElse(false);
    }

    @Override
    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRandom) {
        if (!pState.isBurning(pLevel, pPos)) {
            return;
        }
        // play sound
        if (pRandom.nextInt(10) == 0) {
            pLevel.playLocalSound(pPos.getX() + 0.5D, pPos.getY() + 0.5D, pPos.getZ() + 0.5D, SoundEvents.CAMPFIRE_CRACKLE, SoundSource.BLOCKS, 0.5F + pRandom.nextFloat(), pRandom.nextFloat() * 0.7F + 0.6F, false);
        }
        // particles
        double dx = (pRandom.nextInt(5) - 4.0D) / 16.0D;
        double dy = 3.0D / 16.0D;
        double dz = (pRandom.nextInt(5) - 4.0D) / 16.0D;
        final Vec3 pos = Vec3.atBottomCenterOf(pPos).add(dx, dy, dz);
        // smoke particle
        pLevel.addParticle(ParticleTypes.SMOKE, pos.x(), pos.y(), pos.z(), 0.0D, 0.0D, 0.0D);
        // fire particle
        if (pRandom.nextInt(5) == 0) {
            pLevel.addParticle(ParticleTypes.LARGE_SMOKE, pos.x(), pos.y(), pos.z(), 0.0D, 0.0D, 0.0D);
        }
    }
}
