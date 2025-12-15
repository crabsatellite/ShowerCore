/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.ShoulderRidingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import tanukidecor.TanukiDecor;
import tanukidecor.block.RotatingTallBlock;


import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class BirdcageBlock extends RotatingTallBlock {

    public static final VoxelShape COLLISION_SHAPE_UPPER = Shapes.or(
            box(2, 0, 2, 14, 15, 2.01D),
            box(2, 0, 13.99D, 14, 15, 14),
            box(13.99D, 0, 2, 14, 15, 14),
            box(2, 0, 2, 2.01D, 15, 14),
            box(2, 0, 2, 14, 0.01D, 14),
            box(2, 14.99D, 2, 14, 15, 14));

    public static final VoxelShape SHAPE_UPPER = Shapes.or(
            box(2, 0, 2, 14, 15, 14),
            box(7, 15, 7, 9, 16, 9));
    public static final VoxelShape SHAPE_LOWER = Shapes.or(
            box(2, 0, 2, 14, 2, 14),
            box(6.5D, 2, 6.5D, 9.5D, 16, 9.5D));

    private static final TagKey<EntityType<?>> BIRDCAGE_BLACKLIST = TagKey.create(BuiltInRegistries.ENTITY_TYPE.key(), ResourceLocation.fromNamespaceAndPath(TanukiDecor.MODID, "birdcage_blacklist"));

    private static final double CAGE_WIDTH = 12.0D / 16.0D;
    private static final double CAGE_HEIGHT = 15.0D / 16.0D;
    // entities can be caged if they are not on the blacklist, not a player, not a monster, and are the correct size
    private static final Predicate<LivingEntity> CAN_BE_CAGED = e ->
            !e.getType().is(BIRDCAGE_BLACKLIST) && !(e instanceof Player || e instanceof Monster)
                    && e.isAlive() && !(e.getType().getDimensions().width() > CAGE_WIDTH) && !(e.getType().getDimensions().height() > CAGE_HEIGHT);

    public BirdcageBlock(Properties pProperties) {
        super(pProperties, RotatingTallBlock.createShapeBuilder(SHAPE_UPPER, SHAPE_LOWER));
    }

    @Override
    public VoxelShape getCollisionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        if (pState.getValue(HALF) == DoubleBlockHalf.UPPER) {
            return COLLISION_SHAPE_UPPER;
        }
        return super.getCollisionShape(pState, pLevel, pPos, pContext);
    }

    @Override
    public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        if (pState.getValue(HALF) == DoubleBlockHalf.UPPER) {
            this.getCagedEntity(pLevel, pState, pPos).ifPresent(e -> updateEntityInCage(e));
        }
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (pState.getValue(HALF) == DoubleBlockHalf.UPPER) {
            this.getCagedEntity(pLevel, pState, pPos).ifPresent(e -> extractCagedEntity(e, pLevel, pState, pPos, null));
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
    }

    @Override
    public BlockState playerWillDestroy(Level pLevel, BlockPos pPos, BlockState pState, Player pPlayer) {
        if (pState.getValue(HALF) == DoubleBlockHalf.UPPER) {
            this.getCagedEntity(pLevel, pState, pPos).ifPresent(e -> extractCagedEntity(e, pLevel, pState, pPos, pPlayer));
        }
        return super.playerWillDestroy(pLevel, pPos, pState, pPlayer);
    }

    @Override
    public void entityInside(BlockState pState, Level pLevel, BlockPos pPos, Entity pEntity) {
        final BlockPos pos = getDelegatePos(pState, pPos);
        if (pEntity instanceof Mob entity
                && CAN_BE_CAGED.test(entity) && getCagedEntity(pLevel, pState, pos).isEmpty()
                && !entity.isOnPortalCooldown()) {
            final Vec3 cagedPos = getCagedEntityPos(pLevel, pState, pos);
            entity.setPos(cagedPos);
            updateEntityInCage(entity);
        }
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, BlockHitResult pHitResult) {
        if (pPlayer.isShiftKeyDown()) {
            return InteractionResult.PASS;
        }
        // locate caged entity, if any
        final BlockPos pos = getDelegatePos(pState, pPos);
        final Vec3 cagePos = getCagedEntityPos(pLevel, pState, pos);
        final Optional<LivingEntity> oEntity = getCagedEntity(pLevel, pState, pos);
        // validate server side
        if (pLevel.isClientSide()) {
            return oEntity.isPresent() ? InteractionResult.SUCCESS : InteractionResult.PASS;
        }
        // attempt to remove entity
        if (oEntity.isPresent()) {
            extractCagedEntity(oEntity.get(), pLevel, pState, pos, pPlayer);
            return InteractionResult.SUCCESS;
        }
        // attempt to insert entity from shoulders, if any
        final boolean hasShoulderEntity = !pPlayer.getShoulderEntityLeft().isEmpty() || !pPlayer.getShoulderEntityRight().isEmpty();
        if (hasShoulderEntity) {
            CompoundTag leftShoulder = pPlayer.getShoulderEntityLeft();
            if (!leftShoulder.isEmpty() && spawnFromShoulderEntity(pPlayer, leftShoulder, cagePos).isPresent()) {
                // Remove left shoulder entity using NeoForge's setShoulderEntityLeft API
                pPlayer.setShoulderEntityLeft(new CompoundTag());
                return InteractionResult.SUCCESS;
            }
            CompoundTag rightShoulder = pPlayer.getShoulderEntityRight();
            if (!rightShoulder.isEmpty() && spawnFromShoulderEntity(pPlayer, rightShoulder, cagePos).isPresent()) {
                // Remove right shoulder entity using NeoForge's setShoulderEntityRight API
                pPlayer.setShoulderEntityRight(new CompoundTag());
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    private Optional<Entity> spawnFromShoulderEntity(Player player, CompoundTag compoundTag, final Vec3 pos) {
        if (compoundTag.isEmpty()) {
            return Optional.empty();
        }
        final Optional<Entity> oEntity = EntityType.create(compoundTag, player.level());
        oEntity.ifPresent((e) -> {
            // update owner UUID
            if (e instanceof TamableAnimal tamable) {
                tamable.setOwnerUUID(player.getUUID());
            }
            // move the entity
            e.setPos(pos.x(), pos.y(), pos.z());
            e.setDeltaMovement(Vec3.ZERO);
            // spawn the entity
            ((ServerLevel) player.level()).addWithUUID(e);
            // post process the entity
            updateEntityInCage(e);
        });
        return oEntity;
    }

    /**
     * Attempts to extract the entity from the cage, either to the player shoulder or the player position
     *
     * @param entity     the entity to extract
     * @param level      the level
     * @param blockState the blocks tate
     * @param pos        the block position
     * @param player     the player that extracted the entity, if any
     */
    private void extractCagedEntity(LivingEntity entity, Level level, BlockState blockState, BlockPos pos, @Nullable Player player) {
        // update portal cooldown
        entity.setPortalCooldown();
        // move directly to shoulder
        if (entity instanceof ShoulderRidingEntity ridingEntity
                && player instanceof ServerPlayer serverPlayer
                && ridingEntity.isOwnedBy(player)
                && ridingEntity.setEntityOnShoulder(serverPlayer)) {
            return;
        }
        // stop sitting
        if (entity instanceof TamableAnimal tamable) {
            tamable.setOrderedToSit(false);
        }
        // move to player position
        if (player != null) {
            entity.setPos(player.position());
        }
    }

    private void updateEntityInCage(final Entity entity) {
        if (entity instanceof TamableAnimal tamable) {
            tamable.setOrderedToSit(true);
        }
        if (entity instanceof Mob mob) {
            mob.getNavigation().stop();
            mob.setJumping(false);
            mob.setTarget(null);
        }
    }

    private Vec3 getCagedEntityPos(Level level, BlockState blockState, BlockPos blockPos) {
        return Vec3.atBottomCenterOf(getDelegatePos(blockState, blockPos)).add(0, 0.05D, 0);
    }

    private Optional<LivingEntity> getCagedEntity(Level level, BlockState blockState, BlockPos pos) {
        if (blockState.getValue(HALF) != DoubleBlockHalf.UPPER) {
            return Optional.empty();
        }
        final Vec3 start = Vec3.atLowerCornerOf(pos).add(3.0D / 16.0D, 0, 3.0D / 16.0D);
        final Vec3 end = start.add(10.0D / 16.0D, 14.0D / 16.0D, 10.0D / 16.0D);
        final AABB aabb = new AABB(start, end);
        final List<LivingEntity> list = level.getEntitiesOfClass(LivingEntity.class, aabb, CAN_BE_CAGED);
        if (list.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(list.get(0));
    }
}
