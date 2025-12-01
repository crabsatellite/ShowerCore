package mod.crabmod.showercore.item;

import mod.crabmod.showercore.entity.RubberDuckEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class RubberDuckItem extends Item {
    public RubberDuckItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        if (!(level instanceof ServerLevel)) {
            return InteractionResult.SUCCESS;
        } else {
            BlockPos blockpos = context.getClickedPos();
            Direction direction = context.getClickedFace();
            BlockPos blockpos1 = blockpos.relative(direction);
            
            RubberDuckEntity duck = new RubberDuckEntity(level, blockpos1.getX() + 0.5, blockpos1.getY(), blockpos1.getZ() + 0.5);
            level.addFreshEntity(duck);
            
            context.getItemInHand().shrink(1);
            return InteractionResult.CONSUME;
        }
    }
}
