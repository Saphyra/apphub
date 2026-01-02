package com.github.saphyra.apphub.lib.encryption.impl;

import com.github.saphyra.apphub.lib.common_util.collection.StringStringMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;

import static java.util.Objects.isNull;

@RequiredArgsConstructor
@Slf4j
public class RevealingStringEncryptor implements StringEncryptor {
    static final String ENCRYPTED = "encrypted";
    static final String RAW = "raw";
    static final String KEY = "key";

    private final DefaultStringEncryptor defaultStringEncryptor;
    private final ObjectMapper objectMapper;

    @Override
    public String encrypt(String entity, String key, String entityId, String column) {
        String encrypted = defaultStringEncryptor.encrypt(entity, key, entityId, column);
        StringStringMap map = new StringStringMap();
        map.put(ENCRYPTED, encrypted);
        map.put(RAW, entity);
        map.put(KEY, key);
        return objectMapper.writeValueAsString(map);
    }

    @Override
    public String decrypt(String entity, String key, String entityId, String column) {
        if (isNull(entity)) {
            return null;
        }
        StringStringMap map = objectMapper.readValue(entity, StringStringMap.class);
        return defaultStringEncryptor.decrypt(map.get(ENCRYPTED), key, entityId, column);
    }
}
