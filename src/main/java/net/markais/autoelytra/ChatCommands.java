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
                ).then(literal("cancel")
                    .executes(context -> {
                        FlyManager.getInstance().cancel();
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
                                AutoFlyConfig.getInstance().setLandingMode(InteractionMode.NEVER);
                                return 1;
                            })
                        ).then(literal("always")
                            .executes(context -> {
                                sendPrivateMessage(new LiteralText("Landing mode: ALWAYS"));
                                AutoFlyConfig.getInstance().setLandingMode(InteractionMode.ALWAYS);
                                return 1;
                            })
                        ).then(literal("last")
                            .executes(context -> {
                                sendPrivateMessage(new LiteralText("Landing mode: LAST"));
                                AutoFlyConfig.getInstance().setLandingMode(InteractionMode.LAST);
                                return 1;
                            })
                        )
                    ).then(literal("disconnect")
                        .then(literal("never")
                            .executes(context -> {
                                sendPrivateMessage(new LiteralText("Disconnect mode: NEVER"));
                                AutoFlyConfig.getInstance().setDisconnectMode(InteractionMode.NEVER);
                                return 1;
                            })
                        ).then(literal("always")
                            .executes(context -> {
                                sendPrivateMessage(new LiteralText("Disconnect mode: ALWAYS"));
                                AutoFlyConfig.getInstance().setDisconnectMode(InteractionMode.ALWAYS);
                                return 1;
                            })
                        ).then(literal("last")
                            .executes(context -> {
                                sendPrivateMessage(new LiteralText("Disconnect mode: LAST"));
                                AutoFlyConfig.getInstance().setDisconnectMode(InteractionMode.LAST);
                                return 1;
                            })
                        )
                    ).then(literal("order")
                        .then(literal("nearest")
                            .executes(context -> {
                                sendPrivateMessage(new LiteralText("Fly order: NEAREST"));
                                AutoFlyConfig.getInstance().setFollowMode(FollowMode.NEAREST);
                                return 1;
                            })
                        ).then(literal("iterative")
                            .executes(context -> {
                                sendPrivateMessage(new LiteralText("Fly order: ITERATIVE"));
                                AutoFlyConfig.getInstance().setFollowMode(FollowMode.ITERATIVE);
                                return 1;
                            })
                        )
                    ).then(literal("liftOffAltitude")
                        .then(argument("y", IntegerArgumentType.integer())
                            .executes(context -> {
                                int y = context.getArgument("y", Integer.class);
                                AutoFlyConfig.getInstance().setLiftOffAltitude(y);
                                return 1;
                            })
                        )
                    ).then(literal("unsafeAltitude")
                        .then(argument("y", IntegerArgumentType.integer())
                            .executes(context -> {
                                int y = context.getArgument("y", Integer.class);
                                AutoFlyConfig.getInstance().setUnsafeAltitude(y);
                                return 1;
                            })
                        )
                    ).then(literal("disconnectAltitude")
                        .then(argument("y", IntegerArgumentType.integer())
                            .executes(context -> {
                                int y = context.getArgument("y", Integer.class);
                                AutoFlyConfig.getInstance().setDisconnectAltitude(y);
                                return 1;
                            })
                        )
                    ).then(literal("rocketSlot")
                        .then(argument("slot", IntegerArgumentType.integer())
                            .executes(context -> {
                                int slot = context.getArgument("slot", Integer.class);
                                AutoFlyConfig.getInstance().setRocketSlot(slot);
                                return 1;
                            })
                        )
                    ).then(literal("minRocketCount")
                        .then(argument("count", IntegerArgumentType.integer())
                            .executes(context -> {
                                int count = context.getArgument("count", Integer.class);
                                AutoFlyConfig.getInstance().setMinRocketCount(count);
                                return 1;
                            })
                        )
                    ).then(literal("elytraChangeDurability")
                        .then(argument("durability", IntegerArgumentType.integer())
                            .executes(context -> {
                                int durability = context.getArgument("durability", Integer.class);
                                AutoFlyConfig.getInstance().setElytraChangeDurability(durability);
                                return 1;
                            })
                        )
                    ).then(literal("minElytraDurability")
                        .then(argument("durability", IntegerArgumentType.integer())
                            .executes(context -> {
                                int durability = context.getArgument("durability", Integer.class);
                                AutoFlyConfig.getInstance().setMinElytraDurability(durability);
                                return 1;
                            })
                        )
                    ).then(literal("minElytraDurability")
                        .then(argument("speed", IntegerArgumentType.integer())
                            .executes(context -> {
                                int speed = context.getArgument("speed", Integer.class);
                                AutoFlyConfig.getInstance().setYawSpeed(speed);
                                return 1;
                            })
                        )
                    )
                ).then(literal("current")
                    .executes(context -> {
                        FlyManager.getInstance().printCurrentWaypoint();
                        return 1;
                    })
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
