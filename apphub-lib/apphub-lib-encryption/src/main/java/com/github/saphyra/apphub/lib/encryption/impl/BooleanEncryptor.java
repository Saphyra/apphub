package com.github.saphyra.apphub.lib.encryption.impl;

import com.github.saphyra.apphub.lib.encryption.base.AbstractEncryptor;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class BooleanEncryptor implements AbstractEncryptor<Boolean> {
    private final StringEncryptor stringEncryptor;

    @Override
    public String encrypt(Boolean entity, String key, String entityId, String column) {
        String stringified = Optional.ofNullable(entity)
            .map(Object::toString)
            .orElse(null);
        return stringEncryptor.encrypt(stringified, key, entityId, column);
    }

    @Override
    public Boolean decrypt(String entity, String key, String entityId, String column) {
        return Optional.ofNullable(stringEncryptor.decrypt(entity, key, entityId, column))
            .map(Boolean::parseBoolean)
            .orElse(null);
    }
}
