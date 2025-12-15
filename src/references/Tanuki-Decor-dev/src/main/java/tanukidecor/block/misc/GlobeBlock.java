/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import tanukidecor.TDRegistry;
import tanukidecor.block.RotatingBlock;
import tanukidecor.block.entity.GlobeBlockEntity;

public class GlobeBlock extends RotatingBlock implements EntityBlock {

    public static final VoxelShape SHAPE = Shapes.or(
            box(4, 0, 4, 12, 1, 12),
            box(7, 1, 7, 9, 3, 9),
            Shapes.join(
                    box(1, 3, 7, 10, 15, 9),
                    box(2, 4, 7, 10, 14, 9),
                    BooleanOp.ONLY_FIRST
            ),
            box(7.5D, 4, 7.5D, 8.5D, 14, 8.5D),
            box(4, 5, 4, 12, 13, 12));

    public GlobeBlock(Properties pProperties) {
        super(pProperties, RotatingBlock.createShapeBuilder(SHAPE));
    }

    /// / METHODS ////

    @Override
    protected ItemInteractionResult useItemOn(ItemStack pStack, BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHitResult) {
        return GlobeBlockEntity.use(pState, pLevel, pPos, pPlayer, pHand, pHitResult);
    }

    /// / BLOCK ENTITY ////

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return TDRegistry.BlockEntityReg.GLOBE.get().create(pPos, pState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return !pLevel.isClientSide() ? (BlockEntityTicker<T>) (BlockEntityTicker<GlobeBlockEntity>) (GlobeBlockEntity::tick) : null;
    }

    /// / REDSTONE ////

    @Override
    public void neighborChanged(BlockState pState, Level pLevel, BlockPos pPos, Block pBlock, BlockPos pFromPos, boolean pIsMoving) {
        this.checkPoweredState(pLevel, pPos, pState);
    }

    @Override
    public void onPlace(BlockState pState, Level pLevel, BlockPos pPos, BlockState pOldState, boolean pIsMoving) {
        if (!pOldState.is(pState.getBlock())) {
            this.checkPoweredState(pLevel, pPos, pState);
        }
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof GlobeBlockEntity blockEntity && !blockEntity.isActive()) {
            return 1 + blockEntity.getTargetDirection().get2DDataValue();
        }
        return 0;
    }

    private void checkPoweredState(Level pLevel, BlockPos pPos, BlockState pState) {
        if (!pLevel.isClientSide() && pLevel.hasNeighborSignal(pPos)
                && pLevel.getBlockEntity(pPos) instanceof GlobeBlockEntity blockEntity
                && !blockEntity.isActive()) {
            blockEntity.start(pLevel);
        }
    }
}
