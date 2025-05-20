package com.momarious.command_service.utils;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;
import com.momarious.command_service.exception.ProtobufParseException;

public class ProtobufUtils {

    private ProtobufUtils() {
    }

    public static String convertToJsonString(byte[] data, Message.Builder builder) {
        try {
            return JsonFormat.printer().includingDefaultValueFields().print(builder.mergeFrom(data).build());
        } catch (InvalidProtocolBufferException e) {
            throw new ProtobufParseException("Failed to parse Protobuf", e);
        }
    }
}
