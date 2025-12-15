/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor;

import com.mojang.logging.LogUtils;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLEnvironment;
import org.slf4j.Logger;
import tanukidecor.network.TDNetwork;

@Mod(TanukiDecor.MODID)
public class TanukiDecor {

    public static final String MODID = "tanukidecor";

    public static final Logger LOGGER = LogUtils.getLogger();

    private static final net.neoforged.neoforge.common.ModConfigSpec.Builder CONFIG_BUILDER = new net.neoforged.neoforge.common.ModConfigSpec.Builder();
    public static final TDConfig CONFIG = new TDConfig(CONFIG_BUILDER);

    public TanukiDecor(ModContainer container) {
        IEventBus modEventBus = container.getEventBus();
        // register common config
        container.registerConfig(ModConfig.Type.COMMON, CONFIG_BUILDER.build());
        // register network
        TDNetwork.register();
        // register registry objects
        TDRegistry.register(modEventBus);
        // register mod lifecycle events
        modEventBus.addListener(this::commonSetup);
        // register client events
        if (FMLEnvironment.dist == Dist.CLIENT) {
            tanukidecor.client.TDClientEvents.register();
            modEventBus.register(tanukidecor.client.TDClientEvents.ModHandler.class);
        }
    }

    private void commonSetup(final net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent event) {
        // Common setup if needed
    }
}
