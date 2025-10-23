package com.mindcube.testmod.client.screen;

import com.mindcube.proto.Message;
import com.mindcube.testmod.common.network.ProtoMessagePacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class TestScreen extends Screen {

    private EditBox textInput;

    public TestScreen() {
        super(Component.literal("Test"));
    }

    @Override
    protected void init() {
        super.init();

        var screenWidth = 200;
        var screenHeight = 40;
        var leftPos = (this.width - screenWidth) / 2;
        var topPos = (this.height - screenHeight) / 2;

        textInput = addRenderableWidget(new EditBox(minecraft.fontFilterFishy, leftPos, topPos, screenWidth, screenHeight / 2, Component.literal("TextInput")));
        this.textInput.setMaxLength(256);
        this.textInput.setCanLoseFocus(false);

        addRenderableWidget(Button
            .builder(Component.literal("Отправить"), button -> {
                var text = textInput.getValue();
                ClientPlayNetworking.send(new ProtoMessagePacket(Message.newBuilder().setText(text).build()));
            })
            .bounds(leftPos, topPos + screenHeight / 2, screenWidth, screenHeight / 2)
            .build()
        );
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int i, int j, float f) {
        this.renderTransparentBackground(guiGraphics);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
