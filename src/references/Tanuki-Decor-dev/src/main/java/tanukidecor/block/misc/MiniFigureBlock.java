/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;
import tanukidecor.TDRegistry;
import tanukidecor.block.RotatingBlock;

import java.util.function.Supplier;

public class MiniFigureBlock extends RotatingBlock {

    public static final VoxelShape SHAPE = box(4, 0, 4, 12, 12, 12);

    private final Supplier<SoundEvent> useSound;

    public MiniFigureBlock(Properties pProperties) {
        this(null, pProperties);
    }

    public MiniFigureBlock(Supplier<SoundEvent> useSound, Properties pProperties) {
        super(pProperties, b -> SHAPE);
        this.useSound = useSound;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, BlockHitResult pHitResult) {
        if (pPlayer.isShiftKeyDown()) {
            return super.useWithoutItem(pState, pLevel, pPos, pPlayer, pHitResult);
        }
        // determine sounds to play
        SoundEvent sqeak = getSqueakSound(pLevel, pState, pPos);
        SoundEvent extra = getExtraSqueakSound(pLevel, pState, pPos);
        // play sounds
        if (sqeak != null) {
            pLevel.playSound(pPlayer, pPos, sqeak, SoundSource.BLOCKS, 1.0F, 0.8F + pPlayer.getRandom().nextFloat() * 0.4F);
        }
        if (extra != null) {
            pLevel.playSound(pPlayer, pPos, extra, SoundSource.BLOCKS, 0.8F, 1.1F + pPlayer.getRandom().nextFloat() * 0.4F);
        }
        return InteractionResult.SUCCESS;
    }

    protected SoundEvent getSqueakSound(Level level, BlockState state, BlockPos pos) {
        return TDRegistry.SoundReg.MINI_FIGURE_SQUEAK.get();
    }

    protected SoundEvent getExtraSqueakSound(Level level, BlockState state, BlockPos pos) {
        return this.useSound != null ? this.useSound.get() : null;
    }
}
