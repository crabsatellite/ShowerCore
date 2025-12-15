/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import tanukidecor.TDRegistry;
import tanukidecor.block.RotatingTallBlock;
import tanukidecor.block.entity.SlotMachineBlockEntity;

public class SlotMachineBlock extends RotatingTallBlock implements EntityBlock {

    public static final VoxelShape SHAPE_UPPER = box(1, 0, 4, 15, 16, 12);
    public static final VoxelShape SHAPE_LOWER = Shapes.or(
            box(1, 4, 4, 15, 16, 12),
            box(7, 1, 7, 9, 4, 9),
            box(3, 0, 3, 13, 1, 13));


    public SlotMachineBlock(Properties pProperties) {
        super(pProperties, RotatingTallBlock.createShapeBuilder(SHAPE_UPPER, SHAPE_LOWER));
    }

    /// / METHODS ////

    @Override
    protected InteractionResult useWithoutItem(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, BlockHitResult pHitResult) {
        BlockPos pos = getDelegatePos(pState, pPos);
        return SlotMachineBlockEntity.use(pState, pLevel, pos, pPlayer, net.minecraft.world.InteractionHand.MAIN_HAND, pHitResult);
    }

    /// / BLOCK ENTITY ////

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        if (pPos.equals(getDelegatePos(pState, pPos))) {
            return TDRegistry.BlockEntityReg.SLOT_MACHINE.get().create(pPos, pState);
        }
        return null;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return !pLevel.isClientSide() ? (BlockEntityTicker<T>) (BlockEntityTicker<SlotMachineBlockEntity>) (SlotMachineBlockEntity::tick) : null;
    }

    /// / REDSTONE ////

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        if (level.getBlockEntity(getDelegatePos(state, pos)) instanceof SlotMachineBlockEntity blockEntity) {
            if (blockEntity.isActive()) {
                return 1;
            }
            if (blockEntity.isJackpot()) {
                return 15;
            }
        }
        return 0;
    }
}
