package com.github.saphyra.apphub.lib.encryption.impl;

import com.github.saphyra.apphub.lib.encryption.base.Encryptor;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class DoubleEncryptor implements Encryptor<Double> {
    private final StringEncryptor stringEncryptor;

    @Override
    public String encrypt(Double entity, String key, String entityId, String column) {
        String stringified = Optional.ofNullable(entity)
            .map(Object::toString)
            .orElse(null);
        return stringEncryptor.encrypt(stringified, key, entityId, column);
    }

    @Override
    public Double decrypt(String entity, String key, String entityId, String column) {
        return Optional.ofNullable(stringEncryptor.decrypt(entity, key, entityId, column))
            .map(Double::parseDouble)
            .orElse(null);
    }
}
