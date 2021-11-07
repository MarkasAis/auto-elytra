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

public class AutoElytraCommands {

    /*
     *
     * /fly to <x> <y>
     * /fly to <name>
     *
     * /fly stop
     * /fly pause
     * /fly resume
     *
     * /fly dictionary
     * /fly dictionary add <x> <y> <name>
     * /fly dictionary remove <name>
     *
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
        AutoElytra.LOGGER.info("Initializing commands!");

        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {

            dispatcher.register(literal("fly")
                .then(literal("to")
                    .then(argument("x", IntegerArgumentType.integer())
                        .then(argument("z", IntegerArgumentType.integer())
                            .executes(context -> {
                                return 1;
                            })
                        )
                    ).then(argument("name", StringArgumentType.string())
                        .executes(context -> {
                            return 1;
                        })
                    )
                ).then(literal("stop")
                    .executes(context -> {
                        return 1;
                    })
                ).then(literal("pause")
                    .executes(context -> {
                        return 1;
                    })
                ).then(literal("resume")
                    .executes(context -> {
                        return 1;
                    })
                ).then(literal("dictionary")
                    .executes(context -> {
                        return 1;
                    }).then(literal("add")
                        .then(argument("x", IntegerArgumentType.integer())
                            .then(argument("z", IntegerArgumentType.integer())
                                .then(argument("name", StringArgumentType.string())
                                    .executes(context -> {
                                        return 1;
                                    })
                                )
                            )
                        )
                    ).then(literal("remove")
                        .then(argument("name", StringArgumentType.string())
                            .executes(context -> {
                                return 1;
                            })
                        )
                    )
                ).then(literal("sequence")
                    .then(literal("add")
                        .then(argument("x", IntegerArgumentType.integer())
                            .then(argument("z", IntegerArgumentType.integer())
                                .executes(context -> {
                                    return 1;
                                })
                            )
                        ).then(argument("name", StringArgumentType.string())
                            .executes(context -> {
                                return 1;
                            })
                        )
                    ).then(literal("load")
                        .then(argument("filename", StringArgumentType.string())
                            .executes(context -> {
                                return 1;
                            })
                        )
                    ).then(literal("clear")
                        .executes(context -> {
                            return 1;
                        })
                    ).then(literal("start")
                        .executes(context -> {
                            return 1;
                        })
                    )
                ).then(literal("set")
                    .then(literal("landing")
                        .then(literal("never")
                            .executes(context -> {
                                sendPrivateMessage(new LiteralText("Landing mode: NEVER"));
                                return 1;
                            })
                        ).then(literal("always")
                            .executes(context -> {
                                sendPrivateMessage(new LiteralText("Landing mode: ALWAYS"));
                                return 1;
                            })
                        ).then(literal("last")
                            .executes(context -> {
                                sendPrivateMessage(new LiteralText("Landing mode: LAST"));
                                return 1;
                            })
                        )
                    ).then(literal("disconnect")
                        .then(literal("never")
                            .executes(context -> {
                                sendPrivateMessage(new LiteralText("Disconnect mode: NEVER"));
                                return 1;
                            })
                        ).then(literal("always")
                            .executes(context -> {
                                sendPrivateMessage(new LiteralText("Disconnect mode: ALWAYS"));
                                return 1;
                            })
                        ).then(literal("last")
                            .executes(context -> {
                                sendPrivateMessage(new LiteralText("Disconnect mode: LAST"));
                                return 1;
                            })
                        )
                    ).then(literal("order")
                        .then(literal("nearest")
                            .executes(context -> {
                                sendPrivateMessage(new LiteralText("Fly order: NEAREST"));
                                return 1;
                            })
                        ).then(literal("iterative")
                            .executes(context -> {
                                sendPrivateMessage(new LiteralText("Fly order: ITERATIVE"));
                                return 1;
                            })
                        )
                    )
                )
            );

        });
    }

    private static void sendPrivateMessage(Text message) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player != null)
            mc.inGameHud.addChatMessage(MessageType.SYSTEM, message, mc.player.getUuid());
    }
}
