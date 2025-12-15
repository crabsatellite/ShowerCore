/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import org.joml.Vector4f;
import tanukidecor.block.TallBlock;
import tanukidecor.block.misc.PlasmaBallBlock;
import tanukidecor.util.Gradient;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class PlasmaBallBlockEntity extends BlockEntity {

    private final List<Arc> arcs = new ArrayList<>();
    private static final Gradient ARC_COLORS = new Gradient.Builder(0xDDFFFFFF)
            .with(0.36F, 0xCF70AEFF)
            .with(0.92F, 0xFFFF6DD8)
            .build();

    public PlasmaBallBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
    }

    public static void tick(Level level, BlockPos blockPos, BlockState blockState, PlasmaBallBlockEntity blockEntity) {
        // validate arcs can update
        if (!(blockState.getValue(TallBlock.HALF) == DoubleBlockHalf.UPPER && blockState.getValue(PlasmaBallBlock.ENABLED))) {
            return;
        }
        // update arcs
        final Collection<Arc> arcsView = Collections.unmodifiableCollection(blockEntity.getArcs());
        for (Arc arc : arcsView) {
            arc.update(blockEntity, arcsView, level.getRandom());
        }
        // move arcs
        for (Arc arc : arcsView) {
            arc.move(blockEntity, level.getRandom());
        }
    }

    public List<Arc> getArcs() {
        if (arcs.isEmpty() && this.getLevel() != null) {
            // populate arcs
            this.arcs.addAll(createArcs(12, ARC_COLORS, this.getLevel().getRandom()));
        }
        return arcs;
    }

    protected List<Arc> createArcs(final int count, final Gradient colors, final RandomSource random) {
        final List<Arc> list = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            list.add(new Arc(colors, random));
        }
        return list;
    }

    public static class Arc {
        private static final float MAX_POSITION = 1.0F;
        private static final float MAX_QUADRATIC_TERM = 1.0F;
        private static final float MAX_DELTA_POSITION = 0.028F;
        private static final float MAX_DELTA_QUADRATIC_TERM = 0.04F;

        private final Vector3f position;
        private final Vector3f deltaPosition;
        private final Vector3f quadraticTerm;
        private final Vector3f deltaQuadraticTerm;
        private final Gradient color;

        public Arc(final Gradient color, final RandomSource random) {
            // initialize values
            this.color = color;
            this.position = new Vector3f();
            this.deltaPosition = new Vector3f();
            this.quadraticTerm = new Vector3f();
            this.deltaQuadraticTerm = new Vector3f();
            // randomize values
            setRandomFloats(this.position, random, 0.0F, MAX_POSITION);
            setRandomFloats(this.deltaPosition, random, -MAX_DELTA_POSITION, MAX_DELTA_POSITION);
            setRandomFloats(this.quadraticTerm, random, -MAX_QUADRATIC_TERM, MAX_QUADRATIC_TERM);
            setRandomFloats(this.deltaQuadraticTerm, random, -MAX_DELTA_QUADRATIC_TERM, MAX_DELTA_QUADRATIC_TERM);
        }

        /// / METHODS ////

        public void update(final BlockEntity blockEntity, final Collection<Arc> arcs, final RandomSource random) {
            // count edge touches
            int edges = 0;
            if ((this.position.x() * this.position.x() > 0.95F)) edges++;
            if ((this.position.y() * this.position.y() > 0.95F)) edges++;
            if ((this.position.z() * this.position.z() > 0.95F)) edges++;
            // update delta rotation
            if (random.nextFloat() < 0.01F + edges * 0.005F) {
                setRandomFloats(this.deltaPosition, random, -MAX_DELTA_POSITION, MAX_DELTA_POSITION);
            }
            // update quadratic terms
            if (random.nextFloat() < 0.004F + edges * 0.003F) {
                setRandomFloats(this.deltaQuadraticTerm, random, -MAX_DELTA_QUADRATIC_TERM, MAX_DELTA_QUADRATIC_TERM);
            }
            // update delta quadratic terms
            if (random.nextFloat() < 0.01F + edges * 0.009F) {
                setRandomFloats(this.quadraticTerm, random, -MAX_QUADRATIC_TERM, MAX_QUADRATIC_TERM);
            }
            // update position
            if (random.nextFloat() < 0.003F + edges * 0.04F) {
                setRandomFloats(this.position, random, 0.0F, MAX_POSITION);
            }
            // iterate other arcs
            final Vec3 pos = new Vec3(this.getEndPosition());
            for (Arc other : arcs) {
                if (other == this) continue;
                // check distance to other arc
                Vec3 otherVec = new Vec3(other.getEndPosition());
                if (pos.closerThan(otherVec, 0.125F)) {
                    // when other arc is too close, move this one
                    setRandomFloats(this.position, random, 0.0F, MAX_POSITION);
                    break;
                }
            }
        }

        public void move(final BlockEntity blockEntity, final RandomSource random) {
            // TODO smarter checks so it wraps around corners
            this.position.set(
                    Mth.clamp(this.position.x() + this.deltaPosition.x(), -0.5F, 0.5F),
                    Mth.clamp(this.position.y() + this.deltaPosition.y(), -0.5F, 0.5F),
                    Mth.clamp(this.position.z() + this.deltaPosition.z(), -0.5F, 0.5F));
            // update curve
            //this.curve = this.curve + this.deltaCurve;
            this.quadraticTerm.set(
                    Mth.clamp(this.quadraticTerm.x() + this.deltaQuadraticTerm.x(), -0.6F, 0.6F),
                    Mth.clamp(this.quadraticTerm.y() + this.deltaQuadraticTerm.y(), -0.6F, 0.6F),
                    Mth.clamp(this.quadraticTerm.z() + this.deltaQuadraticTerm.z(), -0.6F, 0.6F));
        }

        /// / GETTERS AND SETTERS ////

        public Vector3f getEndPosition() {
            return position;
        }

        /**
         * @param percent     the percent along the arc
         * @param time        the game time
         * @param partialTick the partial tick
         * @return XYZ coordinates in the range [-1,1]
         */
        public Vector3f getPosition(final float percent, final long time, final float partialTick) {
            final Vector3f endPos = getEndPosition();
            // lerp position
            float x = percent * Mth.lerp(deltaPosition.x() * partialTick, endPos.x() - deltaPosition.x(), endPos.x());
            float y = percent * Mth.lerp(deltaPosition.y() * partialTick, endPos.y() - deltaPosition.y(), endPos.y());
            float z = percent * Mth.lerp(deltaPosition.z() * partialTick, endPos.z() - deltaPosition.z(), endPos.z());

            // pass position through a non-linear formula
            float timeFactor = Mth.PI * ((time + partialTick) / 40.0F);
            float noise = noiseFactor(time / 15.0F, percent);
            x += this.quadraticTerm.x() * noise;
            y += this.quadraticTerm.y() * noise;
            z += this.quadraticTerm.z() * noise;

            return new Vector3f(x, y, z);
        }

        private float noiseFactor(final float time, final float percent) {
            return 0.019F * Mth.sin((percent * time) * 8.4F);
        }

        public Vector4f getColor(final float percent) {
            return color.getColor(percent);
        }

        /// / HELPER METHODS ////

        protected static void setRandomFloats(final Vector3f vec, final RandomSource random, final float min, final float max) {
            final float range = max - min;
            float x = min + random.nextFloat() * range;
            float y = min + random.nextFloat() * range;
            float z = min + random.nextFloat() * range;
            vec.set(x, y, z);
        }

    }
}
