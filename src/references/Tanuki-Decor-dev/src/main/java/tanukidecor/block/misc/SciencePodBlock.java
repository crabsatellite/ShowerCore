/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import tanukidecor.TDRegistry;
import tanukidecor.block.RotatingMultiblock;
import tanukidecor.block.entity.SingleSlotBlockEntity;
import tanukidecor.util.MultiblockHandler;

public class SciencePodBlock extends RotatingMultiblock implements EntityBlock, IDisplayProvider {

    private static final Vector3f DISPLAY_SCALE = new Vector3f(0.725F, 0.725F, 0.725F);

    public SciencePodBlock(Properties pProperties) {
        super(MultiblockHandler.MULTIBLOCK_1X3X1, SciencePodBlock::buildShape, pProperties);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack pStack, BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHitResult) {
        BlockPos pos = getDelegatePos(pState, pPos);
        return SingleSlotBlockEntity.useItemOn(pStack, pState, pLevel, pos, pPlayer, pHand, pHitResult);
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (!pState.is(pNewState.getBlock())) {
            SingleSlotBlockEntity.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
            super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
        }
    }

    /// / DISPLAY PROVIDER ////

    @Override
    public Vector3f getDisplayRotation(Level level, BlockState blockState, BlockPos blockPos, ItemStack itemStack, int renderPass, float partialTick) {
        return new Vector3f(0, blockState.getValue(FACING).getOpposite().toYRot(), 0);
    }

    @Override
    public Vector3f getDisplayTranslation(Level level, BlockState blockState, BlockPos blockPos, ItemStack itemStack, int renderPass, float partialTick) {
        final float time = (int) (level.getGameTime() + Math.abs(blockState.getSeed(blockPos)) % 96000L) + partialTick;
        final float dy = Mth.sin(time * 0.032F) * 0.09F;
        return new Vector3f(0, dy, 0);
    }

    @Override
    public Vector3f getDisplayScale(Level level, BlockState blockState, BlockPos blockPos, ItemStack itemStack, int renderPass, float partialTick) {
        return DISPLAY_SCALE;
    }

    /// / BLOCK ENTITY ////

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        if (this.getMultiblockHandler().isCenterState(pState)) {
            return TDRegistry.BlockEntityReg.DISPLAY_CASE.get().create(pPos, pState);
        }
        return TDRegistry.BlockEntityReg.STORAGE_DELEGATE.get().create(pPos, pState);
    }

    /// / SHAPE ////

    public static final VoxelShape SHAPE_LOWER = Shapes.or(
            box(1, 0, 1, 15, 3, 15),
            box(0, 0, 5, 16, 4, 11),
            box(5, 0, 0, 11, 4, 16),
            box(2, 6, 2, 14, 16, 14));
    public static final VoxelShape SHAPE_MIDDLE = box(2, 0, 2, 14, 16, 14);
    public static final VoxelShape SHAPE_UPPER = Shapes.or(
            box(1, 13, 1, 15, 16, 15),
            box(0, 12, 5, 16, 16, 11),
            box(5, 12, 0, 11, 16, 16),
            box(2, 0, 2, 14, 10, 14));

    public static VoxelShape buildShape(final BlockState blockState) {
        final Vec3i index = MultiblockHandler.MULTIBLOCK_1X3X1.getIndex(blockState);
        switch (index.getY()) {
            case -1:
                return SHAPE_LOWER;
            case 0:
                return SHAPE_MIDDLE;
            case 1:
                return SHAPE_UPPER;
            default:
                return Shapes.block();
        }
    }
}
