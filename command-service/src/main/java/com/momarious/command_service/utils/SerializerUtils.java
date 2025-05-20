package com.momarious.command_service.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.momarious.command_service.exception.JsonSerDeException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public final class SerializerUtils {

  private static final Logger LOG = LoggerFactory.getLogger(SerializerUtils.class);

  private static final ObjectMapper objectMapper = JsonMapper.builder()
      .addModule(new ParameterNamesModule())
      .addModule(new Jdk8Module())
      .addModule(new JavaTimeModule())
      .build();

  private SerializerUtils() {
  }

  public static byte[] serializeToJsonBytes(final Object object) {
    try {
      return objectMapper.writeValueAsBytes(object);
    } catch (JsonProcessingException e) {
      LOG.error("(serializeToJsonBytes): {}", e.getMessage(), e);
      throw new JsonSerDeException("Failed to serialize object", e);
    }
  }

  public static <T> T deserializeFromJsonBytes(final byte[] jsonBytes, final Class<T> valueType) {
    try {
      return objectMapper.readValue(jsonBytes, valueType);
    } catch (IOException e) {
      LOG.error("(deserializeFromJsonBytes): {}", e.getMessage(), e);
      throw new JsonSerDeException("Failed to deserialize JSON bytes", e);
    }
  }

}
