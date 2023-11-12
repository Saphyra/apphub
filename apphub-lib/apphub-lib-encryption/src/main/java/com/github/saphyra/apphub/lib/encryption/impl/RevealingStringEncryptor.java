package com.github.saphyra.apphub.lib.encryption.impl;

import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.lib.common_util.collection.StringStringMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class RevealingStringEncryptor extends StringEncryptor {
    static final String ENCRYPTED = "encrypted";
    static final String RAW = "raw";
    static final String KEY = "key";

    private final DefaultStringEncryptor defaultStringEncryptor;
    private final ObjectMapperWrapper objectMapperWrapper;

    @Override
    protected String encrypt(String entity, String key) {
        String encrypted = defaultStringEncryptor.encryptEntity(entity, key);
        StringStringMap map = new StringStringMap();
        map.put(ENCRYPTED, encrypted);
        map.put(RAW, entity);
        map.put(KEY, key);
        return objectMapperWrapper.writeValueAsString(map);
    }

    @Override
    protected String decrypt(String entity, String key) {
        StringStringMap map = objectMapperWrapper.readValue(entity, StringStringMap.class);
        return defaultStringEncryptor.decryptEntity(map.get(ENCRYPTED), key);
    }
}
