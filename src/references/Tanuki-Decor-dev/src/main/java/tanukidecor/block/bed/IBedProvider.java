/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.bed;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public interface IBedProvider {

    /**
     * @param level      the level
     * @param blockPos   the block position of the head of the bed
     * @param blockState the block state of the head of the bed
     */
    void removeBed(final Level level, final BlockPos blockPos, final BlockState blockState);

    /**
     * @param blockState the block state
     * @param level      the level
     * @param pos        the block position
     * @return the block position of the head of the bed, can be the same as the given pos
     */
    BlockPos getHeadPos(BlockState blockState, Level level, BlockPos pos);

    /**
     * @param blockState the block state
     * @return true if the block state is the head part of the bed
     */
    boolean isHeadOfBed(BlockState blockState);

    /**
     * Attempts to make the player start sleeping in the bed
     *
     * @param blockState the block state
     * @param level      the level
     * @param pos        the block position
     * @param player     the player
     * @return the interaction result
     */
    default InteractionResult useBed(BlockState blockState, Level level, BlockPos pos, Player player) {
        if (level.isClientSide) {
            return InteractionResult.CONSUME;
        }

        final BlockPos headPos = getHeadPos(blockState, level, pos);
        final BlockState headState = level.getBlockState(headPos);

        // validate block exists
        if (!headState.is(blockState.getBlock())) {
            return InteractionResult.CONSUME;
        }

        // create explosion when bed cannot be used
        if (!BedBlock.canSetSpawn(level)) {
            removeBed(level, headPos, headState);
            Vec3 explosionPos = Vec3.atCenterOf(headPos);
            level.explode(null, level.damageSources().badRespawnPointExplosion(explosionPos), null, explosionPos.x(), explosionPos.y(), explosionPos.z(), 5.0F, true, Level.ExplosionInteraction.BLOCK);
            return InteractionResult.SUCCESS;
        }

        // attempt to eject villager sleeping in the bed
        if (headState.hasProperty(BlockStateProperties.OCCUPIED) && headState.getValue(BlockStateProperties.OCCUPIED)) {
            if (!kickVillagerOutOfBed(level, headPos)) {
                player.displayClientMessage(Component.translatable("block.minecraft.bed.occupied"), true);
            }

            return InteractionResult.SUCCESS;
        }

        // start sleeping in the bed or display a problem, if any
        player.startSleepInBed(headPos).ifLeft(problem -> {
            if (problem != null) {
                player.displayClientMessage(problem.getMessage(), true);
            }

        });

        return InteractionResult.SUCCESS;

    }

    // copied from BedBlock#kickVillagerOutOfBed
    static boolean kickVillagerOutOfBed(Level level, BlockPos pos) {
        List<Villager> list = level.getEntitiesOfClass(Villager.class, new AABB(pos), LivingEntity::isSleeping);
        if (list.isEmpty()) {
            return false;
        } else {
            list.get(0).stopSleeping();
            return true;
        }
    }

    // copied from BedBlock#bounceUp
    static void bounceUp(Entity pEntity) {
        Vec3 vec3 = pEntity.getDeltaMovement();
        if (vec3.y < 0.0D) {
            double d0 = pEntity instanceof LivingEntity ? 1.0D : 0.8D;
            pEntity.setDeltaMovement(vec3.x, -vec3.y * (double) 0.66F * d0, vec3.z);
        }

    }
}
