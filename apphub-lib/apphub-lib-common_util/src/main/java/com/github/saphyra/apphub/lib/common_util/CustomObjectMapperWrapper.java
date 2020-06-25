package com.github.saphyra.apphub.lib.common_util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.saphyra.util.ObjectMapperWrapper;

import java.io.IOException;
import java.net.URL;

public class CustomObjectMapperWrapper extends ObjectMapperWrapper {
    private final ObjectMapper objectMapper;

    public CustomObjectMapperWrapper(ObjectMapper objectMapper) {
        super(objectMapper);
        this.objectMapper = objectMapper;
    }

    public <V> V readValue(URL url, Class<V> clazz) {
        try {
            return objectMapper.readValue(url, clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
