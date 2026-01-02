package com.github.saphyra.apphub.lib.data;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ClassPathMap<K, V> extends HashMap<K, V> {
    public ClassPathMap(ObjectMapper objectMapper, String fileLocation) {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileLocation);
        TypeReference<Map<K, V>> ref = new TypeReference<>() {
        };
        putAll(objectMapper.readValue(inputStream, ref));
    }
}
