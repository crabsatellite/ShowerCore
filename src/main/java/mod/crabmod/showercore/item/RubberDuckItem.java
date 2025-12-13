package mod.crabmod.showercore.item;

import mod.crabmod.showercore.entity.RubberDuckEntity;
import net.minecraft.core.Direction;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.function.Predicate;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.Nullable;

public class RubberDuckItem extends Item {
    private static final Predicate<Entity> ENTITY_PREDICATE = EntitySelector.NO_SPECTATORS.and(Entity::isPickable);

    public RubberDuckItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.showercore.rubber_duck.usage.place"));
        tooltip.add(Component.translatable("tooltip.showercore.rubber_duck.usage.push"));
        tooltip.add(Component.translatable("tooltip.showercore.rubber_duck.usage.pickup"));
        super.appendHoverText(stack, context, tooltip, flag);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        Vec3 pos = context.getClickLocation();
        Direction direction = context.getClickedFace();
        if (direction.getAxis().isHorizontal()) {
            pos = pos.add(direction.getStepX() * 0.25, 0, direction.getStepZ() * 0.25);
        }

        RubberDuckEntity duck = new RubberDuckEntity(level, pos.x, pos.y, pos.z);
        duck.setYRot(context.getRotation());

        if (!level.noCollision(duck, duck.getBoundingBox())) {
            return InteractionResult.FAIL;
        }

        if (!level.isClientSide) {
            level.addFreshEntity(duck);
            context.getItemInHand().shrink(1);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        HitResult hitresult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.ANY);
        if (hitresult.getType() == HitResult.Type.MISS) {
            return InteractionResultHolder.pass(itemstack);
        } else {
            Vec3 vec3 = player.getViewVector(1.0F);
            List<Entity> list = level.getEntities(player, player.getBoundingBox().expandTowards(vec3.scale(5.0D)).inflate(1.0D), ENTITY_PREDICATE);
            if (!list.isEmpty()) {
                Vec3 vec31 = player.getEyePosition();

                for(Entity entity : list) {
                    AABB aabb = entity.getBoundingBox().inflate((double)entity.getPickRadius());
                    if (aabb.contains(vec31)) {
                        return InteractionResultHolder.pass(itemstack);
                    }
                }
            }

            if (hitresult.getType() == HitResult.Type.BLOCK) {
                net.minecraft.world.phys.BlockHitResult blockHitResult = (net.minecraft.world.phys.BlockHitResult) hitresult;
                Direction direction = blockHitResult.getDirection();
                Vec3 pos = hitresult.getLocation();
                if (direction.getAxis().isHorizontal()) {
                    pos = pos.add(direction.getStepX() * 0.25, 0, direction.getStepZ() * 0.25);
                }

                RubberDuckEntity duck = new RubberDuckEntity(level, pos.x, pos.y, pos.z);
                duck.setYRot(player.getYRot());
                if (!level.noCollision(duck, duck.getBoundingBox())) {
                    return InteractionResultHolder.fail(itemstack);
                } else {
                    if (!level.isClientSide) {
                        level.addFreshEntity(duck);
                        level.gameEvent(player, GameEvent.ENTITY_PLACE, hitresult.getLocation());
                        if (!player.getAbilities().instabuild) {
                            itemstack.shrink(1);
                        }
                    }

                    player.awardStat(Stats.ITEM_USED.get(this));
                    return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
                }
            } else {
                return InteractionResultHolder.pass(itemstack);
            }
        }
    }
}
