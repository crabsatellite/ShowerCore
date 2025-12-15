/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import tanukidecor.TDRegistry;
import tanukidecor.block.RotatingWideBlock;
import tanukidecor.block.entity.DisplayBlockEntity;
import tanukidecor.block.entity.SingleSlotBlockEntity;

public class HandcartBlock extends RotatingWideBlock implements EntityBlock, IDisplayProvider {

    public static final VoxelShape SHAPE_EAST = Shapes.or(
            box(7, 12, 12, 15, 14, 14),
            box(7, 12, 2, 15, 14, 4),
            box(5, 0, 12, 7, 6, 14),
            box(5, 0, 2, 7, 6, 4),
            Shapes.join(
                    box(0, 6, 2, 7, 14, 14),
                    box(0, 8, 4, 5, 14, 12),
                    BooleanOp.ONLY_FIRST
            )
    );
    public static final VoxelShape SHAPE_WEST = Shapes.or(
            box(4.5D, 2, 5, 6.5D, 6, 7),
            box(4.5D, 2, 9, 6.5D, 6, 11),
            box(3, 0, 7, 8, 5, 9),
            Shapes.join(
                    box(3, 6, 2, 16, 14, 14),
                    box(5, 8, 4, 16, 14, 12),
                    BooleanOp.ONLY_FIRST
            )
    );

    private static final Vector3f DISPLAY_SCALE = new Vector3f(0.5F, 0.5F, 0.5F);
    /**
     * Used client-side to display items with psuedorandom positions and rotations
     **/
    private static final RandomSource RANDOM = new LegacyRandomSource(0);

    public HandcartBlock(Properties pProperties) {
        super(pProperties, RotatingWideBlock.createShapeBuilder(SHAPE_EAST, SHAPE_WEST));
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

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        if (this.getDelegatePos(pState, pPos).equals(pPos)) {
            return TDRegistry.BlockEntityReg.HANDCART.get().create(pPos, pState);
        }
        return TDRegistry.BlockEntityReg.STORAGE_DELEGATE.get().create(pPos, pState);
    }

    /// / DISPLAY PROVIDER ////

    @Override
    public Vector3f getDisplayRotation(Level level, BlockState blockState, BlockPos blockPos, ItemStack itemStack, int renderPass, float partialTick) {
        // prepare random instance
        RANDOM.setSeed(renderPass * 3311L + blockPos.asLong());
        RANDOM.nextFloat();
        // calculate x angle bias, this is set to 0 for blocks and 90 for most other items
        final float biasX;
        if (level.getBlockEntity(getDelegatePos(blockState, blockPos)) instanceof DisplayBlockEntity displayBlockEntity && !(displayBlockEntity.getItem(0).getItem() instanceof BlockItem)) {
            biasX = 90.0F;
        } else {
            biasX = 0.0F;
        }
        final float deltaXZ = 15.0F;
        final float deltaY = 360.0F;
        // calculate random vector
        return new Vector3f(biasX + RANDOM.nextFloat() * deltaXZ * 2 - deltaXZ, RANDOM.nextFloat() * deltaY, RANDOM.nextFloat() * deltaXZ * 2 - deltaXZ);
    }

    @Override
    public Vector3f getDisplayTranslation(Level level, BlockState blockState, BlockPos blockPos, ItemStack itemStack, int renderPass, float partialTick) {
        // prepare random instance
        RANDOM.setSeed(renderPass * 1919L + blockPos.asLong());
        RANDOM.nextFloat();
        // calculate height of each layer
        final int height = (int) Math.pow(renderPass, 0.425F);
        // calculate horizontal offset limit
        final float dxy = Math.max(1.0F / 16.0F, height * 0.06125F);
        // calculate maximum horizontal and vertical bounds
        final float deltaX = (8.0F / 16.0F) - dxy;
        final float deltaY = 0.125F;
        final float deltaZ = (4.0F / 16.0F) - dxy;
        // calculate random vector
        return new Vector3f(RANDOM.nextFloat() * deltaX * 2 - deltaX, height * deltaY + RANDOM.nextFloat() * deltaY, RANDOM.nextFloat() * deltaZ * 2 - deltaZ);
    }

    @Override
    public Vector3f getDisplayScale(Level level, BlockState blockState, BlockPos blockPos, ItemStack itemStack, int renderPass, float partialTick) {
        return DISPLAY_SCALE;
    }

    /// / REDSTONE ////

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(level.getBlockEntity(pos));
    }
}
