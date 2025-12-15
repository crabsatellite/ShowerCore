/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import tanukidecor.TDRegistry;
import tanukidecor.TanukiDecor;
import tanukidecor.block.RotatingTallBlock;
import tanukidecor.block.entity.DIYWorkbenchBlockEntity;

public class DIYWorkbenchBlock extends RotatingTallBlock implements EntityBlock {

    public static final VoxelShape SHAPE_UPPER = Shapes.or(
            box(2, 0, 15, 14, 7, 16),
            box(0, 0, 14, 2, 7, 16),
            box(14, 0, 14, 16, 7, 16),
            box(0, 7, 11, 16, 8, 16));
    public static final VoxelShape SHAPE_LOWER = Shapes.or(
            box(0, 11, 0, 16, 14, 16),
            box(2, 4, 4, 14, 5, 12),
            box(8, 5, 5, 11, 9, 8),
            box(4, 5, 8, 7, 9, 11),
            box(1, 0, 1, 4, 11, 4),
            box(12, 0, 1, 15, 11, 4),
            box(1, 0, 12, 4, 11, 15),
            box(12, 0, 12, 15, 11, 15),
            box(0, 14, 14, 2, 16, 16),
            box(2, 14, 15, 14, 16, 16),
            box(14, 14, 14, 16, 16, 16));

    public DIYWorkbenchBlock(Properties pProperties) {
        super(pProperties, RotatingTallBlock.createShapeBuilder(SHAPE_UPPER, SHAPE_LOWER));
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, BlockHitResult pHitResult) {
        if (pLevel.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        // check config settings
        if (!TanukiDecor.CONFIG.isDIYWorkbenchEnabled.get()) {
            // display message to user
            pPlayer.displayClientMessage(Component.translatable("message." + getDescriptionId() + ".disabled"), true);
            return InteractionResult.SUCCESS;
        }
        // determine block entity position
        BlockPos pos = getDelegatePos(pState, pPos);
        // open menu
        if (!pPlayer.isShiftKeyDown() && pPlayer instanceof ServerPlayer serverPlayer
                && pLevel.getBlockEntity(pos) instanceof MenuProvider menuProvider) {
            serverPlayer.openMenu(menuProvider, pos);
            return InteractionResult.SUCCESS;
        }
        return super.useWithoutItem(pState, pLevel, pPos, pPlayer, pHitResult);
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (!pState.is(pNewState.getBlock())) {
            DIYWorkbenchBlockEntity.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
            super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
        }
    }

    /// / BLOCK ENTITY ////

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        if (pPos.equals(getDelegatePos(pState, pPos))) {
            return TDRegistry.BlockEntityReg.DIY_WORKBENCH.get().create(pPos, pState);
        }
        return TDRegistry.BlockEntityReg.STORAGE_DELEGATE.get().create(pPos, pState);
    }
}
