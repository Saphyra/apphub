package com.github.saphyra.apphub.lib.encryption.impl;

import com.github.saphyra.apphub.lib.encryption.base.AbstractEncryptor;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BooleanEncryptor extends AbstractEncryptor<Boolean> {
    private final StringEncryptor stringEncryptor;


    @Override
    protected String encrypt(Boolean entity, String key) {
        return stringEncryptor.encrypt(entity.toString(), key);
    }

    @Override
    protected Boolean decrypt(String entity, String key) {
        return Boolean.parseBoolean(stringEncryptor.decrypt(entity, key));
    }
}
