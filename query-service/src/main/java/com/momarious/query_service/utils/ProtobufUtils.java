package com.momarious.query_service.utils;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;
import com.momarious.query_service.exception.ProtobufParseException;

public class ProtobufUtils {

    private ProtobufUtils() {
    }

    public static <T extends Message> T convertJsonStringToProtobuf(String json, Message.Builder builder) {
        try {
            JsonFormat.parser().ignoringUnknownFields().merge(json, builder);
            return (T) builder.build();
        } catch (InvalidProtocolBufferException e) {
            throw new ProtobufParseException("Failed to parse JSON into Protobuf", e);
        }
    }
}
