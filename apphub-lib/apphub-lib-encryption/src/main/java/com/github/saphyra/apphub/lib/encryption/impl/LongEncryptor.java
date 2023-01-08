package com.github.saphyra.apphub.lib.encryption.impl;

import com.github.saphyra.apphub.lib.encryption.base.AbstractEncryptor;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LongEncryptor extends AbstractEncryptor<Long> {
    private final StringEncryptor stringEncryptor;

    @Override
    protected String encrypt(Long entity, String key) {
        return stringEncryptor.encryptEntity(entity.toString(), key);
    }

    @Override
    protected Long decrypt(String entity, String key) {
        return Long.valueOf(stringEncryptor.decryptEntity(entity, key));
    }
}
