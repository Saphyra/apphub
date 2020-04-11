package com.github.saphyra.apphub.lib.common_util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.saphyra.util.ObjectMapperWrapper;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;

public class CustomObjectMapperWrapper extends ObjectMapperWrapper {
    private final ObjectMapper objectMapper;

    public CustomObjectMapperWrapper(ObjectMapper objectMapper) {
        super(objectMapper);
        this.objectMapper = objectMapper;
    }

    public <T> Collection<? extends T> readValue(InputStream inputStream, TypeReference<? extends Collection<T>> ref) {
        try {
            return objectMapper.readValue(inputStream, ref);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public <V> V readValue(URL url, Class<V> clazz) {
        try {
            return objectMapper.readValue(url, clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
