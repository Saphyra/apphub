package com.github.saphyra.apphub.lib.common_util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class ObjectMapperWrapper {
    @Getter
    private final ObjectMapper objectMapper;

    public <T> T readValue(InputStream in, TypeReference<T> type) {
        try {
            return objectMapper.readValue(in, type);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> T readValue(String source, Class<T> type) {
        try {
            return objectMapper.readValue(source, type);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> T readValue(String source, TypeReference<T> type) {
        try {
            return objectMapper.readValue(source, type);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> List<T> readArrayValue(String source, Class<T[]> type) {
        try {
            return new ArrayList<>(Arrays.asList(objectMapper.readValue(source, type)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String writeValueAsString(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public String writeValueAsPrettyString(Object value) {
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(value);
        } catch (JsonProcessingException e) {
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

    public <T> T convertValue(Object o, Class<T> clazz) {
        return objectMapper.convertValue(o, clazz);
    }
}
