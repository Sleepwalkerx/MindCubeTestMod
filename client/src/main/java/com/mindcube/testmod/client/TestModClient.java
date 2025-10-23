package com.mindcube.testmod.client;

import com.mindcube.testmod.client.screen.TestScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;

public class TestModClient implements ClientModInitializer {

    private TestModKeyInputs keyInputs;

    @Override
    public void onInitializeClient() {
        registerKeyMappings();
    }

    private void registerKeyMappings(){
        keyInputs = new TestModKeyInputs();
        ClientTickEvents.END_CLIENT_TICK.register(this::handleKeyBinding);
    }

    private void handleKeyBinding(Minecraft minecraft) {
        while (keyInputs.openGui.consumeClick()){
            minecraft.setScreen(new TestScreen());
        }
    }
}
