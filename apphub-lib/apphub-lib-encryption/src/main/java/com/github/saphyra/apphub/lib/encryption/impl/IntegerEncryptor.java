package com.github.saphyra.apphub.lib.encryption.impl;

import com.github.saphyra.apphub.lib.encryption.base.AbstractEncryptor;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class IntegerEncryptor implements AbstractEncryptor<Integer> {
    private final StringEncryptor stringEncryptor;

    @Override
    public String encrypt(Integer entity, String key, String entityId, String column) {
        String stringified = Optional.ofNullable(entity)
            .map(Object::toString)
            .orElse(null);
        return stringEncryptor.encrypt(stringified, key, entityId, column);
    }

    @Override
    public Integer decrypt(String entity, String key, String entityId, String column) {
        return Optional.ofNullable(stringEncryptor.decrypt(entity, key, entityId, column))
            .map(Integer::parseInt)
            .orElse(null);
    }
}
