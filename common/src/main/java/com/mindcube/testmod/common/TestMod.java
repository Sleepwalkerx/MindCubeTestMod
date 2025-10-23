package com.mindcube.testmod.common;

import com.mindcube.testmod.common.network.ProtoMessagePacket;
import com.mojang.logging.LogUtils;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import org.slf4j.Logger;

public class TestMod implements ModInitializer {

    public static String MOD_ID = "testmod";
    public static String MOD_NAME = "TestMod";
    public static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public void onInitialize() {
        registerNetwork();
    }

    private void registerNetwork(){
        PayloadTypeRegistry.playC2S().register(ProtoMessagePacket.PACKET_TYPE, ProtoMessagePacket.CODEC);
    }
}
