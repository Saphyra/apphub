package com.github.saphyra.apphub.lib.encryption.impl;

import com.github.saphyra.apphub.lib.encryption.base.Encryptor;
import lombok.RequiredArgsConstructor;

import java.time.LocalTime;
import java.util.Optional;

@RequiredArgsConstructor
//TODO unit test
public class LocalTimeEncryptor implements Encryptor<LocalTime> {
    private final StringEncryptor stringEncryptor;

    @Override
    public String encrypt(LocalTime entity, String key, String entityId, String column) {
        String stringifiedData = Optional.ofNullable(entity)
            .map(LocalTime::toString)
            .orElse(null);

        return stringEncryptor.encrypt(stringifiedData, key, entityId, column);
    }

    @Override
    public LocalTime decrypt(String entity, String key, String entityId, String column) {
        String decrypted = stringEncryptor.decrypt(entity, key, entityId, column);

        return Optional.ofNullable(decrypted)
            .map(LocalTime::parse)
            .orElse(null);
    }
}
