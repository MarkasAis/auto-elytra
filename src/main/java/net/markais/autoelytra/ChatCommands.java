package net.markais.autoelytra;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.MessageType;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class ChatCommands {

    /*
     *
     * /fly to <x> <y>
     * /fly to <name>
     *
     * /fly stop
     * /fly pause
     * /fly resume
     *
     * /fly waypoints
     * /fly waypoints add <x> <y> <name>
     * /fly waypoints remove <name>
     *
     * /fly sequence
     * /fly sequence add <x> <y>
     * /fly sequence add <name>
     * /fly sequence load <filename>
     * /fly sequence clear
     * /fly sequence start
     *
     * /fly set landing <never/always/last>
     * /fly set disconnect <never/always/last>
     * /fly set order <nearest/iterative>
     *
     */

    public static void initCommands() {
        Utils.LOGGER.info("Initializing commands!");

        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {

            dispatcher.register(literal("fly")
                .then(literal("to")
                    .then(argument("x", IntegerArgumentType.integer())
                        .then(argument("z", IntegerArgumentType.integer())
                            .executes(context -> {
                                int x = context.getArgument("x", Integer.class);
                                int z = context.getArgument("z", Integer.class);
                                FlyManager.getInstance().fly(new Waypoint(x, z));
                                return 1;
                            })
                        )
                    ).then(argument("name", StringArgumentType.string())
                        .executes(context -> {
                            String name = context.getArgument("name", String.class);
                            FlyManager.getInstance().fly(name);
                            return 1;
                        })
                    )
                ).then(literal("stop")
                    .executes(context -> {
                        FlyManager.getInstance().stop();
                        return 1;
                    })
                ).then(literal("pause")
                    .executes(context -> {
                        FlyManager.getInstance().pause();
                        return 1;
                    })
                ).then(literal("resume")
                    .executes(context -> {
                        FlyManager.getInstance().resume();
                        return 1;
                    })
                ).then(literal("waypoints")
                    .executes(context -> {
                        WaypointLibrary.getInstance().listWaypoints();
                        return 1;
                    }).then(literal("add")
                        .then(argument("x", IntegerArgumentType.integer())
                            .then(argument("z", IntegerArgumentType.integer())
                                .then(argument("name", StringArgumentType.string())
                                    .executes(context -> {
                                        int x = context.getArgument("x", Integer.class);
                                        int z = context.getArgument("z", Integer.class);
                                        String name = context.getArgument("name", String.class);
                                        WaypointLibrary.getInstance().addWaypoint(new Waypoint(x, z, name));
                                        return 1;
                                    })
                                )
                            )
                        )
                    ).then(literal("remove")
                        .then(argument("name", StringArgumentType.string())
                            .executes(context -> {
                                String name = context.getArgument("name", String.class);
                                WaypointLibrary.getInstance().removeWaypoint(name);
                                return 1;
                            })
                        )
                    )
                ).then(literal("sequence")
                    .executes(context -> {
                        FlyManager.getInstance().listWaypoints();
                        return 1;
                    }).then(literal("add")
                        .then(argument("x", IntegerArgumentType.integer())
                            .then(argument("z", IntegerArgumentType.integer())
                                .executes(context -> {
                                    int x = context.getArgument("x", Integer.class);
                                    int z = context.getArgument("z", Integer.class);
                                    FlyManager.getInstance().addWaypoint(new Waypoint(x, z));
                                    return 1;
                                })
                            )
                        ).then(argument("name", StringArgumentType.string())
                            .executes(context -> {
                                String name = context.getArgument("name", String.class);
                                FlyManager.getInstance().addWaypoint(name);
                                return 1;
                            })
                        )
                    ).then(literal("load")
                        .then(argument("filename", StringArgumentType.string())
                            .executes(context -> {
                                String filename = context.getArgument("filename", String.class);
                                FlyManager.getInstance().loadSequence(filename);
                                return 1;
                            })
                        )
                    ).then(literal("clear")
                        .executes(context -> {
                            FlyManager.getInstance().clearSequence();
                            return 1;
                        })
                    ).then(literal("start")
                        .executes(context -> {
                            FlyManager.getInstance().flySequence();
                            return 1;
                        })
                    )
                ).then(literal("set")
                    .then(literal("landing")
                        .then(literal("never")
                            .executes(context -> {
                                sendPrivateMessage(new LiteralText("Landing mode: NEVER"));
                                // TODO
                                return 1;
                            })
                        ).then(literal("always")
                            .executes(context -> {
                                sendPrivateMessage(new LiteralText("Landing mode: ALWAYS"));
                                // TODO
                                return 1;
                            })
                        ).then(literal("last")
                            .executes(context -> {
                                sendPrivateMessage(new LiteralText("Landing mode: LAST"));
                                // TODO
                                return 1;
                            })
                        )
                    ).then(literal("disconnect")
                        .then(literal("never")
                            .executes(context -> {
                                sendPrivateMessage(new LiteralText("Disconnect mode: NEVER"));
                                // TODO
                                return 1;
                            })
                        ).then(literal("always")
                            .executes(context -> {
                                sendPrivateMessage(new LiteralText("Disconnect mode: ALWAYS"));
                                // TODO
                                return 1;
                            })
                        ).then(literal("last")
                            .executes(context -> {
                                sendPrivateMessage(new LiteralText("Disconnect mode: LAST"));
                                // TODO
                                return 1;
                            })
                        )
                    ).then(literal("order")
                        .then(literal("nearest")
                            .executes(context -> {
                                sendPrivateMessage(new LiteralText("Fly order: NEAREST"));
                                // TODO
                                return 1;
                            })
                        ).then(literal("iterative")
                            .executes(context -> {
                                sendPrivateMessage(new LiteralText("Fly order: ITERATIVE"));
                                // TODO
                                return 1;
                            })
                        )
                    )
                )
            );

        });
    }

    public static void sendPrivateMessage(Text message) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player != null)
            mc.inGameHud.addChatMessage(MessageType.SYSTEM, message, mc.player.getUuid());
    }
}
