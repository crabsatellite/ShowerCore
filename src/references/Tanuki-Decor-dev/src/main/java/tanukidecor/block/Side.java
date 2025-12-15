/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block;

import net.minecraft.util.StringRepresentable;

public enum Side implements StringRepresentable {
    LEFT("left"),
    RIGHT("right");

    private final String name;

    Side(String name) {
        this.name = name;
    }

    public Side getOpposite() {
        if (this == LEFT) {
            return RIGHT;
        }
        return LEFT;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }
}
