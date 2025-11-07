package com.github.saphyra.apphub.lib.common_util;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class ObjectMapperWrapper {
    @Getter
    private final ObjectMapper objectMapper;

    public ObjectMapperWrapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public <T> T readValue(InputStream in, TypeReference<T> type) {
        return objectMapper.readValue(in, type);
    }

    public <T> T readValue(String source, Class<T> type) {
        return objectMapper.readValue(source, type);
    }

    public <T> T readValue(String source, TypeReference<T> type) {
        return objectMapper.readValue(source, type);
    }

    public <T> List<T> readArrayValue(String source, Class<T[]> type) {
        return new ArrayList<>(Arrays.asList(objectMapper.readValue(source, type)));
    }

    public String writeValueAsString(Object value) {
        return objectMapper.writeValueAsString(value);
    }

    public String writeValueAsPrettyString(Object value) {
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(value);
    }

    public <V> V readValue(URL url, Class<V> clazz) {
        try {
            return objectMapper.readValue(url.openStream(), clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> T convertValue(Object o, Class<T> clazz) {
        return objectMapper.convertValue(o, clazz);
    }

    public <T> T convertValue(Object o, TypeReference<T> typeReference) {
        return objectMapper.convertValue(o, typeReference);
    }

    @SneakyThrows
    public JsonNode readTree(String message) {
        return objectMapper.readTree(message);
    }
}
