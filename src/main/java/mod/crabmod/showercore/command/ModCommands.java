package mod.crabmod.showercore.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import mod.crabmod.showercore.ShowerCore;
import mod.crabmod.showercore.entity.SeatEntity;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = ShowerCore.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModCommands {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        register(event.getDispatcher());
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("showercore")
            .then(Commands.literal("accept_bath")
                .then(Commands.argument("requester", StringArgumentType.string())
                    .executes(context -> {
                        String requesterName = StringArgumentType.getString(context, "requester");
                        ServerPlayer acceptor = context.getSource().getPlayerOrException();
                        ServerPlayer requester = context.getSource().getServer().getPlayerList().getPlayerByName(requesterName);

                        if (requester == null) {
                            context.getSource().sendFailure(Component.literal("Player not found or offline."));
                            return 0;
                        }

                        if (acceptor.distanceToSqr(requester) > 25) {
                            context.getSource().sendFailure(Component.literal("You are too far apart!"));
                            return 0;
                        }

                        // Find the bathtub the acceptor is sitting in
                        if (acceptor.getVehicle() instanceof SeatEntity seat) {
                            Level level = acceptor.level();
                            BlockPos headPos = seat.blockPosition();
                            // Assuming the seat is at the HEAD, the FOOT is adjacent.
                            // We need to find the bathtub block to know the direction.
                            // Actually, we can just search for a valid spot near the seat.
                            // But better to check the block state.
                            
                            // The seat is at the center of the block, so blockPosition should be correct.
                            // However, SeatEntity might be slightly offset.
                            
                            // Let's try to find the BathtubBlock at the seat's position
                            if (level.getBlockState(headPos).getBlock() instanceof mod.crabmod.showercore.block.BathtubBlock) {
                                // Found it. Now find the FOOT position.
                                // The seat is at HEAD.
                                // We need to check the blockstate to find the FOOT.
                                net.minecraft.world.level.block.state.BlockState state = level.getBlockState(headPos);
                                net.minecraft.core.Direction facing = state.getValue(mod.crabmod.showercore.block.BathtubBlock.FACING);
                                net.minecraft.world.level.block.state.properties.BedPart part = state.getValue(mod.crabmod.showercore.block.BathtubBlock.PART);
                                
                                BlockPos footPos;
                                if (part == net.minecraft.world.level.block.state.properties.BedPart.HEAD) {
                                    footPos = headPos.relative(facing.getOpposite());
                                } else {
                                    // Should not happen if seat is at HEAD, but just in case
                                    footPos = headPos; 
                                }

                                // Check if FOOT is already occupied?
                                List<SeatEntity> existingSeats = level.getEntitiesOfClass(SeatEntity.class, new AABB(footPos));
                                if (!existingSeats.isEmpty() && !existingSeats.get(0).getPassengers().isEmpty()) {
                                     context.getSource().sendFailure(Component.literal("The foot of the tub is already occupied!"));
                                     return 0;
                                }

                                // Spawn seat at FOOT
                                SeatEntity footSeat = new SeatEntity(level, footPos.getX() + 0.5, footPos.getY() + 0.1, footPos.getZ() + 0.5);
                                level.addFreshEntity(footSeat);
                                requester.startRiding(footSeat);
                                
                                acceptor.sendSystemMessage(Component.literal("Splish splash! " + requesterName + " joined the bath!"));
                                requester.sendSystemMessage(Component.literal("You squeezed in!"));
                                return 1;
                            }
                        }

                        context.getSource().sendFailure(Component.literal("You are not in a bathtub!"));
                        return 0;
                    }))
            )
            .then(Commands.literal("deny_bath")
                .then(Commands.argument("requester", StringArgumentType.string())
                    .executes(context -> {
                        String requesterName = StringArgumentType.getString(context, "requester");
                        ServerPlayer requester = context.getSource().getServer().getPlayerList().getPlayerByName(requesterName);
                        if (requester != null) {
                            requester.sendSystemMessage(Component.literal("Your request was denied. It's a private session."));
                        }
                        context.getSource().sendSuccess(() -> Component.literal("You denied the request."), false);
                        return 1;
                    }))
            )
        );
    }
}
