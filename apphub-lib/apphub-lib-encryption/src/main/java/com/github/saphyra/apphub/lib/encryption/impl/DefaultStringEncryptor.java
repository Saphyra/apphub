package com.github.saphyra.apphub.lib.encryption.impl;

import com.github.saphyra.apphub.lib.common_util.Base64Encoder;
import com.github.saphyra.apphub.lib.encryption.base.DefaultEncryptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class DefaultStringEncryptor extends StringEncryptor {
    private final Base64Encoder base64Encoder;

    @Override
    protected String encrypt(String entity, String key) {
        DefaultEncryptor encryption = new DefaultEncryptor(base64Encoder, key);
        return encryption.encrypt(entity);
    }

    @Override
    protected String decrypt(String entity, String key) {
        DefaultEncryptor decryption = new DefaultEncryptor(base64Encoder, key);
        return decryption.decrypt(entity);
    }
}
