package com.mindcube.testmod.client;

import com.mindcube.testmod.common.TestMod;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public class TestModKeyInputs {

    public KeyMapping openGui = createAndRegisterSimpleKeyMapping("open_gui", GLFW.GLFW_KEY_H);

    private static KeyMapping createAndRegisterSimpleKeyMapping(String registryName, int key) {
        var keyMapping = new KeyMapping(
            "key." + TestMod.MOD_ID + "." + registryName,
            InputConstants.Type.KEYSYM,
            key,
            "key.categories." + TestMod.MOD_ID + ".main"
        );
        return KeyBindingHelper.registerKeyBinding(keyMapping);
    }
}
