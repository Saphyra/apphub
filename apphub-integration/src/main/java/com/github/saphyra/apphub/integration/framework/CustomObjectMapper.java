package com.github.saphyra.apphub.integration.framework;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.saphyra.util.ObjectMapperWrapper;

public class CustomObjectMapper extends ObjectMapperWrapper {
    private final ObjectMapper objectMapper;

    public CustomObjectMapper(ObjectMapper objectMapper) {
        super(objectMapper);
        this.objectMapper = objectMapper;
    }

    public <T> T convertValue(Object in, Class<T> type) {
        return objectMapper.convertValue(in, type);
    }
}
