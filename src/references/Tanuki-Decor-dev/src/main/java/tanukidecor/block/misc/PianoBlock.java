/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import tanukidecor.block.RotatingMultiblock;
import tanukidecor.util.MultiblockHandler;

public class PianoBlock extends RotatingMultiblock {

    public PianoBlock(Properties pProperties) {
        super(MultiblockHandler.MULTIBLOCK_2X2X1, RotatingMultiblock.createMultiblockShapeBuilder(MultiblockHandler.MULTIBLOCK_2X2X1, SHAPE), pProperties);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, BlockHitResult pHitResult) {
        if (!pPlayer.isShiftKeyDown() && pLevel.getBlockState(pPos.above()).isAir()) {
            playNote(pLevel, pPos, pState, pPlayer);
            return InteractionResult.SUCCESS;
        }
        return super.useWithoutItem(pState, pLevel, pPos, pPlayer, pHitResult);
    }

    protected void playNote(Level level, BlockPos blockPos, BlockState blockState, Player player) {
        if (level.isClientSide() || !(level instanceof ServerLevel serverLevel) || !(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        final int note;
        // detect note block below
        BlockState blockBelow = level.getBlockState(blockPos.below(2));
        if (blockBelow.is(Blocks.NOTE_BLOCK)) {
            // use the same note as the note block
            note = blockBelow.getValue(BlockStateProperties.NOTE);
        } else {
            // no note block found, use random note
            note = level.getRandom().nextInt(25);
        }
        // play the note
        playNoteAt(serverLevel, serverPlayer, blockPos, note);
    }

    protected void playNoteAt(ServerLevel serverLevel, ServerPlayer player, BlockPos blockPos, int note) {
        // play sound
        float noteData = ((float) Math.pow(2.0D, (note - 12.0D) / 12.0D));
        serverLevel.playSound(null, blockPos, NoteBlockInstrument.PLING.getSoundEvent().value(), SoundSource.RECORDS, 3.0F, noteData);
        // send particles
        final Vec3 vec = Vec3.atCenterOf(blockPos)
                .add(0, 0.7D, 0);
        serverLevel.sendParticles(player, ParticleTypes.NOTE, true,
                vec.x(), vec.y(), vec.z(),
                1, 0.25F, 0, 0, note / 24.0D);
    }

    //// SHAPE ////

    /**
     * Shape data for each block in the default horizontal direction, ordered by index {@code [height][width][depth]}
     **/
    public static final VoxelShape[][][] SHAPE = new VoxelShape[][][]{
            // height = 0
            {},
            // height = 1
            {
                    // width = 0
                    {},
                    // width = 1
                    {
                            Shapes.or(box(0, 0, 7, 16, 16, 15),
                                    box(11, 1, 1, 13, 4, 6),
                                    box(11.5D, 0, 2.5D, 12.5D, 1, 4.5D),
                                    box(11, 4, 2.5D, 13, 16, 4.5D))
                    },
                    // width = 2
                    {
                            Shapes.or(box(0, 0, 7, 16, 16, 15),
                                    box(3.5D, 0, 2.5D, 4.5D, 1, 4.5D),
                                    box(3, 1, 1, 5, 4, 6),
                                    box(3, 4, 2.5D, 5, 16, 4.5D))
                    }
            },
            // height = 2
            {
                    // width = 0
                    {},
                    // width = 1
                    {
                            Shapes.or(box(0, 0, 7, 16, 16, 15),
                                    box(0, 0, 0, 14, 3, 7),
                                    box(0, 3, 4, 14, 8, 7))
                    },
                    // width = 2
                    {
                            Shapes.or(box(0, 0, 7, 16, 16, 15),
                                    box(2, 0, 0, 16, 3, 7),
                                    box(2, 3, 4, 16, 8, 7))
                    }
            }
    };
}
