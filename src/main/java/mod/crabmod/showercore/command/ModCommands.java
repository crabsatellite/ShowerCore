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
                            BlockPos seatPos = seat.blockPosition();
                            
                            // Let's try to find the BathtubBlock at the seat's position
                            if (level.getBlockState(seatPos).getBlock() instanceof mod.crabmod.showercore.block.BathtubBlock) {
                                net.minecraft.world.level.block.state.BlockState state = level.getBlockState(seatPos);
                                net.minecraft.core.Direction facing = state.getValue(mod.crabmod.showercore.block.BathtubBlock.FACING);
                                net.minecraft.world.level.block.state.properties.BedPart part = state.getValue(mod.crabmod.showercore.block.BathtubBlock.PART);
                                
                                BlockPos targetPos;
                                if (part == net.minecraft.world.level.block.state.properties.BedPart.HEAD) {
                                    // Acceptor is at HEAD, put requester at FOOT
                                    targetPos = seatPos.relative(facing.getOpposite());
                                } else {
                                    // Acceptor is at FOOT, put requester at HEAD
                                    targetPos = seatPos.relative(facing);
                                }

                                // Check if target position is already occupied?
                                List<SeatEntity> existingSeats = level.getEntitiesOfClass(SeatEntity.class, new AABB(targetPos));
                                if (!existingSeats.isEmpty() && !existingSeats.get(0).getPassengers().isEmpty()) {
                                     context.getSource().sendFailure(Component.literal("The other side of the tub is already occupied!"));
                                     return 0;
                                }

                                // Spawn seat at target position
                                SeatEntity targetSeat = new SeatEntity(level, targetPos.getX() + 0.5, targetPos.getY() + 0.1, targetPos.getZ() + 0.5);
                                level.addFreshEntity(targetSeat);
                                requester.startRiding(targetSeat);
                                
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
