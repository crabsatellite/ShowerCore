/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.common.NeoForge;
import tanukidecor.TDRegistry;
import tanukidecor.TDRegistry.BlockEntityReg;
import tanukidecor.block.seat.ISeatProvider;
import tanukidecor.client.blockentity.clock.*;
import tanukidecor.client.blockentity.misc.*;
import tanukidecor.client.menu.DIYWorkbenchScreen;

import java.util.HashSet;
import java.util.Set;

public final class TDClientEvents {

    public static void register() {
        NeoForge.EVENT_BUS.register(ForgeHandler.class);
        ClientRecipeCollections.register();
    }

    public static final class ForgeHandler {

        @SubscribeEvent
        public static void onRenderOverlay(final RenderGuiLayerEvent.Pre event) {
            final Player player = Minecraft.getInstance().player;
            if (VanillaGuiLayers.VEHICLE_HEALTH.equals(event.getName())
                    && player != null && player.isPassenger()
                    && ISeatProvider.IS_SEAT_ENTITY.test(player.getVehicle())) {
                event.setCanceled(true);
            }
        }
    }

    public static final class ModHandler {

        @SubscribeEvent
        public static void onRegisterMenuScreens(final net.neoforged.neoforge.client.event.RegisterMenuScreensEvent event) {
            event.register(TDRegistry.MenuReg.DIY_WORKBENCH.get(), DIYWorkbenchScreen::new);
            ClientRecipeCollections.registerSearchTrees();
        }

        @SubscribeEvent
        public static void onRegisterEntityRenderers(final EntityRenderersEvent.RegisterRenderers event) {
            // CLOCK //
            event.registerBlockEntityRenderer(BlockEntityReg.ALARM_CLOCK.get(), AlarmClockBER::new);
            event.registerBlockEntityRenderer(BlockEntityReg.ANNIVERSARY_CLOCK.get(), AnniversaryClockBER::new);
            event.registerBlockEntityRenderer(BlockEntityReg.ANTIQUE_CLOCK.get(), AntiqueClockBER::new);
            event.registerBlockEntityRenderer(BlockEntityReg.BANJO_CLOCK.get(), BanjoClockBER::new);
            event.registerBlockEntityRenderer(BlockEntityReg.BLUE_CLOCK.get(), BlueClockBER::new);
            event.registerBlockEntityRenderer(BlockEntityReg.CARRIAGE_CLOCK.get(), CarriageClockBER::new);
            event.registerBlockEntityRenderer(BlockEntityReg.CRYSTAL_CLOCK.get(), CrystalClockBER::new);
            event.registerBlockEntityRenderer(BlockEntityReg.CUCKOO_CLOCK.get(), CuckooClockBER::new);
            event.registerBlockEntityRenderer(BlockEntityReg.DISPLAY_WATCH.get(), DisplayWatchBER::new);
            event.registerBlockEntityRenderer(BlockEntityReg.EMBLEM_CLOCK.get(), EmblemClockBER::new);
            event.registerBlockEntityRenderer(BlockEntityReg.FOLIOT_CLOCK.get(), FoliotClockBER::new);
            event.registerBlockEntityRenderer(BlockEntityReg.GINGERBREAD_CLOCK.get(), GingerbreadClockBER::new);
            event.registerBlockEntityRenderer(BlockEntityReg.GRANDFATHER_CLOCK.get(), GrandfatherClockBER::new);
            event.registerBlockEntityRenderer(BlockEntityReg.GORGEOUS_CLOCK.get(), GorgeousClockBER::new);
            event.registerBlockEntityRenderer(BlockEntityReg.GREEN_CLOCK.get(), GreenClockBER::new);
            event.registerBlockEntityRenderer(BlockEntityReg.LANTERN_CLOCK.get(), LanternClockBER::new);
            event.registerBlockEntityRenderer(BlockEntityReg.LARGE_CLOCK_TOWER_DIAL.get(), LargeClockTowerDialBER::new);
            event.registerBlockEntityRenderer(BlockEntityReg.LIBRARY_CLOCK.get(), LibraryClockBER::new);
            event.registerBlockEntityRenderer(BlockEntityReg.MANTLE_CLOCK.get(), MantleClockBER::new);
            event.registerBlockEntityRenderer(BlockEntityReg.MINIMALIST_CLOCK.get(), MinimalistClockBER::new);
            event.registerBlockEntityRenderer(BlockEntityReg.OWL_CLOCK.get(), OwlClockBER::new);
            event.registerBlockEntityRenderer(BlockEntityReg.RECOGNIZABLE_CLOCK.get(), RecognizableClockBER::new);
            event.registerBlockEntityRenderer(BlockEntityReg.RED_CLOCK.get(), RedClockBER::new);
            event.registerBlockEntityRenderer(BlockEntityReg.REED_CLOCK.get(), ReedClockBER::new);
            event.registerBlockEntityRenderer(BlockEntityReg.REGAL_CLOCK.get(), RegalClockBER::new);
            event.registerBlockEntityRenderer(BlockEntityReg.ROCOCO_CLOCK.get(), RococoClockBER::new);
            event.registerBlockEntityRenderer(BlockEntityReg.SMALL_CLOCK_TOWER_DIAL.get(), SmallClockTowerDialBER::new);
            event.registerBlockEntityRenderer(BlockEntityReg.SLATE_CLOCK.get(), SlateClockBER::new);
            event.registerBlockEntityRenderer(BlockEntityReg.STATION_CLOCK.get(), StationClockBER::new);
            event.registerBlockEntityRenderer(BlockEntityReg.WOODEN_BLOCK_CLOCK.get(), WoodenBlockClockBER::new);
            // MISC //
            event.registerBlockEntityRenderer(BlockEntityReg.DISPLAY_CASE.get(), DisplayCaseBER::new);
            event.registerBlockEntityRenderer(BlockEntityReg.GLOBE.get(), GlobeBER::new);
            event.registerBlockEntityRenderer(BlockEntityReg.HANDCART.get(), HandcartBER::new);
            event.registerBlockEntityRenderer(BlockEntityReg.HOURGLASS.get(), HourglassBER::new);
            event.registerBlockEntityRenderer(BlockEntityReg.METRONOME.get(), MetronomeBER::new);
            event.registerBlockEntityRenderer(BlockEntityReg.NEWTONS_CRADLE.get(), NewtonsCradleBER::new);
            event.registerBlockEntityRenderer(BlockEntityReg.PHONOGRAPH.get(), PhonographBER::new);
            event.registerBlockEntityRenderer(BlockEntityReg.ROCKET_LAMP.get(), RocketLampBER::new);
            event.registerBlockEntityRenderer(BlockEntityReg.SLOT_MACHINE.get(), SlotMachineBER::new);
            event.registerBlockEntityRenderer(BlockEntityReg.TRAIN_SET.get(), TrainSetBER::new);
            event.registerBlockEntityRenderer(BlockEntityReg.PLASMA_BALL.get(), PlasmaBallBER::new);
        }

        @SubscribeEvent
        public static void onRegisterModels(final ModelEvent.RegisterAdditional event) {
            // gather special models
            final Set<ResourceLocation> set = new HashSet<>();
            // CLOCK //
            AlarmClockBER.addSpecialModels(set);
            AnniversaryClockBER.addSpecialModels(set);
            AntiqueClockBER.addSpecialModels(set);
            BanjoClockBER.addSpecialModels(set);
            BlueClockBER.addSpecialModels(set);
            CarriageClockBER.addSpecialModels(set);
            CrystalClockBER.addSpecialModels(set);
            CuckooClockBER.addSpecialModels(set);
            DisplayWatchBER.addSpecialModels(set);
            EmblemClockBER.addSpecialModels(set);
            FoliotClockBER.addSpecialModels(set);
            GingerbreadClockBER.addSpecialModels(set);
            GorgeousClockBER.addSpecialModels(set);
            GrandfatherClockBER.addSpecialModels(set);
            GreenClockBER.addSpecialModels(set);
            LanternClockBER.addSpecialModels(set);
            LargeClockTowerDialBER.addSpecialModels(set);
            LibraryClockBER.addSpecialModels(set);
            MantleClockBER.addSpecialModels(set);
            MinimalistClockBER.addSpecialModels(set);
            OwlClockBER.addSpecialModels(set);
            RecognizableClockBER.addSpecialModels(set);
            RedClockBER.addSpecialModels(set);
            ReedClockBER.addSpecialModels(set);
            RegalClockBER.addSpecialModels(set);
            RococoClockBER.addSpecialModels(set);
            SmallClockTowerDialBER.addSpecialModels(set);
            SlateClockBER.addSpecialModels(set);
            StationClockBER.addSpecialModels(set);
            WoodenBlockClockBER.addSpecialModels(set);
            // MISC //
            GlobeBER.addSpecialModels(set);
            HourglassBER.addSpecialModels(set);
            MetronomeBER.addSpecialModels(set);
            NewtonsCradleBER.addSpecialModels(set);
            PhonographBER.addSpecialModels(set);
            RocketLampBER.addSpecialModels(set);
            SlotMachineBER.addSpecialModels(set);
            TrainSetBER.addSpecialModels(set);
            // register special models
            set.forEach(rl -> event.register(ModelResourceLocation.standalone(rl)));
        }
    }
}
