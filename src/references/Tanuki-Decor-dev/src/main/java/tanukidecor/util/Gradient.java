/*
 * Copyright (c) 2024 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.util;

import com.google.common.collect.ImmutableList;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Contains color and threshold information
 *
 * @see Gradient.Builder
 */
public class Gradient {

    private final Vector4f ONE = new Vector4f(1.0F, 1.0F, 1.0F, 1.0F);
    private final List<Entry> colors;

    private Gradient(final List<Entry> colors) {
        if (colors.isEmpty()) {
            throw new IllegalArgumentException("Failed to build Gradient with empty list");
        }
        // sort list
        this.colors = ImmutableList.sortedCopyOf(Comparator.reverseOrder(), colors);
    }

    /**
     * @param percent a number from 0.0 to 1.0
     * @return the color for the given threshold
     */
    public Vector4f getColor(final float percent) {
        for (int i = 0, n = colors.size(); i < n; i++) {
            Entry entry = colors.get(i);
            if (percent > entry.threshold) {
                return entry.color();
            }
        }
        return ONE;
    }

    //// HELPER METHODS ////

    /**
     * Separates a hex color into RGBA components.
     * Alpha is set to 255 if not present.
     *
     * @param color a packed int RGBA color
     * @return the red, green, blue, and alpha components as a Vector4f
     **/
    public static Vector4f unpackColor(final int color) {
        long tmpColor = color;
        if ((tmpColor & -16777216) == 0) {
            tmpColor |= 0xFF000000; // Set alpha to 255 if it's not present
        }

        float colorRed = (float) ((tmpColor >> 16) & 255) / 255.0F;
        float colorGreen = (float) ((tmpColor >> 8) & 255) / 255.0F;
        float colorBlue = (float) (tmpColor & 255) / 255.0F;
        float colorAlpha = (float) ((tmpColor >> 24) & 255) / 255.0F;

        return new Vector4f(colorRed, colorGreen, colorBlue, colorAlpha);
    }

    public static class Builder {

        private final List<Entry> list;

        /**
         * @param color the initial color
         */
        public Builder(int color) {
            this(unpackColor(color));
        }

        /**
         * @param color the initial color
         */
        public Builder(Vector4f color) {
            this.list = new ArrayList<>();
            this.list.add(new Entry(0.0F, color));
        }

        /**
         * @param threshold the minimum threshold for the given color
         * @param color     the packed RGBA color
         * @return the builder instance
         */
        public Builder with(final float threshold, final int color) {
            this.list.add(new Entry(threshold, unpackColor(color)));
            return this;
        }

        /**
         * @param threshold the minimum threshold for the given color
         * @param color     the RGBA color values from 0 to 1
         * @return the builder instance
         */
        public Builder with(final float threshold, final Vector4f color) {
            this.list.add(new Entry(threshold, color));
            return this;
        }

        /**
         * @return a {@link Gradient} instance
         */
        public Gradient build() {
            return new Gradient(this.list);
        }
    }

    private record Entry(float threshold, Vector4f color) implements Comparable<Entry> {

        private Entry(float threshold, Vector4f color) {
            this.threshold = Mth.clamp(threshold, 0.0F, 1.0F);
            this.color = new Vector4f(
                    Mth.clamp(color.x(), 0.0F, 1.0F),
                    Mth.clamp(color.y(), 0.0F, 1.0F),
                    Mth.clamp(color.z(), 0.0F, 1.0F),
                    Mth.clamp(color.w(), 0.0F, 1.0F)
            );
        }

        @Override
        public int compareTo(@NotNull Gradient.Entry o) {
            return Float.compare(this.threshold, o.threshold);
        }
    }
}
