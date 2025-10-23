package com.mindcube.testmod.server;

import com.mindcube.testmod.common.TestMod;
import com.mindcube.testmod.common.network.ProtoMessagePacket;
import com.mindcube.testmod.server.service.TestSystemService;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class TestModServer implements DedicatedServerModInitializer {

    private TestSystemService testSystemService;

    @Override
    public void onInitializeServer() {
        registerNetwork();
        registerCommands();
        testSystemService = new TestSystemService();
        testSystemService.boostrap();

        ServerLifecycleEvents.SERVER_STARTING.register(minecraftServer -> {
            try {
                testSystemService.reload();
            } catch (Exception e){
                TestMod.LOGGER.error("Can't load TestSystemService", e);
            }
        });
    }

    private void registerCommands(){
        CommandRegistrationCallback.EVENT.register((dispatcher, commandBuildContext, commandSelection) -> {
            dispatcher.register(Commands.literal(TestMod.MOD_ID)
                .requires(s -> s.hasPermission(4))
                .then(Commands.literal("reload")
                    .executes(c -> {
                        try {
                            testSystemService.reload();
                            c.getSource().sendSuccess(() -> Component.translatable("response.testmod.config.reload.success"), true);
                            return 1;
                        } catch (Exception e) {
                            TestMod.LOGGER.error("Can't reload ServiceConfig", e);
                            c.getSource().sendFailure(Component.translatable("response.testmod.config.reload.error"));
                            return 0;
                        }
                    })
                )
            );
        });
    }

    private void registerNetwork(){
        ServerPlayNetworking.registerGlobalReceiver(ProtoMessagePacket.PACKET_TYPE, (protoMessagePacket, context) -> {
            testSystemService.handleProtoMessage(protoMessagePacket, context.player());
        });
    }
}
