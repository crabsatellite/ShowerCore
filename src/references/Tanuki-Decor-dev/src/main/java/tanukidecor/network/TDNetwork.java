/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.network;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import tanukidecor.TanukiDecor;

@EventBusSubscriber(modid = TanukiDecor.MODID)
public final class TDNetwork {

    private TDNetwork() {
    }

    public static void register() {
        // Network registration is now done via RegisterPayloadHandlersEvent
    }

    @SubscribeEvent
    public static void registerPayloads(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(TanukiDecor.MODID);

        // Register server-bound packets
        registrar.playToServer(
                ServerBoundSelectDIYRecipePacket.TYPE,
                ServerBoundSelectDIYRecipePacket.STREAM_CODEC,
                ServerBoundSelectDIYRecipePacket::handle
        );
    }
}
