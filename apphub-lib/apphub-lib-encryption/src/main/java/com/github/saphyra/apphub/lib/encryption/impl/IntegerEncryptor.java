package com.github.saphyra.apphub.lib.encryption.impl;

import com.github.saphyra.apphub.lib.encryption.base.AbstractEncryptor;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class IntegerEncryptor extends AbstractEncryptor<Integer> {
    private final StringEncryptor stringEncryptor;

    @Override
    protected String encrypt(Integer entity, String key) {
        return stringEncryptor.encryptEntity(entity.toString(), key);
    }

    @Override
    protected Integer decrypt(String entity, String key) {
        return Integer.valueOf(stringEncryptor.decryptEntity(entity, key));
    }
}
