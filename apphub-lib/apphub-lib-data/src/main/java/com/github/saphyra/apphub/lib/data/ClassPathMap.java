package com.github.saphyra.apphub.lib.data;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ClassPathMap<K, V> extends HashMap<K, V> {
    public ClassPathMap(ObjectMapperWrapper objectMapper, String fileLocation) {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileLocation);
        TypeReference<Map<K, V>> ref = new TypeReference<>() {
        };
        putAll(objectMapper.readValue(inputStream, ref));
    }
}
