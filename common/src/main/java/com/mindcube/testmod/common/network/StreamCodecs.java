package com.mindcube.testmod.common.network;

import com.mindcube.proto.Message;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.MessageLite;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

public class StreamCodecs {

    //В целом, можно не заниматься этим и воспользоваться JavaReflection, но всё зависит от требований, так как рефлексия очень много кушает.
    public static final StreamCodec<FriendlyByteBuf, Message> MESSAGE = createProtoMessageCodec(Message::parseFrom);

    private static <M extends MessageLite> StreamCodec<FriendlyByteBuf, M> createProtoMessageCodec(ProtoMessageParser<M> parser) {
        return new StreamCodec<>() {
            @Override
            public @NotNull M decode(FriendlyByteBuf buf) {
                try {
                    return parser.parse(buf.readByteArray());
                } catch (InvalidProtocolBufferException e){
                    throw new IllegalStateException("Can't decode message", e);
                }
            }
            @Override
            public void encode(FriendlyByteBuf buf, M object) {
                buf.writeByteArray(object.toByteArray());
            }
        };
    }

    @FunctionalInterface
    private interface ProtoMessageParser<M> {
        @NotNull M parse(byte[] data) throws InvalidProtocolBufferException;
    }
}
