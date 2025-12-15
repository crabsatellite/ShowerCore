/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor;

import net.neoforged.neoforge.common.ModConfigSpec;

public class TDConfig {

    public final ModConfigSpec.BooleanValue isDIYWorkbenchEnabled;
    public final ModConfigSpec.DoubleValue slotMachineJackboxChance;

    public TDConfig(final ModConfigSpec.Builder builder) {
        isDIYWorkbenchEnabled = builder
                .comment("True to allow the DIY Workbench to craft items, false to disable")
                .define("enable_diy_workbench", true);
        slotMachineJackboxChance = builder
                .comment("The percent chance of an instant jackpot, in addition to the natural 6.25% chance")
                .defineInRange("slot_machine_jackbox_chance", 15.0D, 0.0D, 100.0D);
    }
}
