package com.github.saphyra.apphub.lib.encryption.impl;

import com.github.saphyra.apphub.lib.encryption.base.AbstractEncryptor;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.Optional;

@RequiredArgsConstructor
public class LocalDateEncryptor implements AbstractEncryptor<LocalDate> {
    private final StringEncryptor stringEncryptor;

    @Override
    public String encrypt(LocalDate entity, String key, String entityId, String column) {
        String stringifiedData = Optional.ofNullable(entity)
            .map(LocalDate::toString)
            .orElse(null);

        return stringEncryptor.encrypt(stringifiedData, key, entityId, column);
    }

    @Override
    public LocalDate decrypt(String entity, String key, String entityId, String column) {
        String decrypted = stringEncryptor.decrypt(entity, key, entityId, column);

        return Optional.ofNullable(decrypted)
            .map(LocalDate::parse)
            .orElse(null);
    }
}
