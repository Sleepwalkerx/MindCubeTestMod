package com.mindcube.testmod.common.network;

import com.mindcube.proto.Message;
import com.mindcube.testmod.common.TestMod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record ProtoMessagePacket(Message message) implements CustomPacketPayload {

    public static final ResourceLocation SUMMON_LIGHTNING_PAYLOAD_ID = ResourceLocation.fromNamespaceAndPath(TestMod.MOD_ID, "proto");
    public static final Type<ProtoMessagePacket> PACKET_TYPE = new Type<>(SUMMON_LIGHTNING_PAYLOAD_ID);
    public static final StreamCodec<FriendlyByteBuf, ProtoMessagePacket> CODEC = StreamCodec.composite(StreamCodecs.MESSAGE, ProtoMessagePacket::message, ProtoMessagePacket::new);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return PACKET_TYPE;
    }
}
