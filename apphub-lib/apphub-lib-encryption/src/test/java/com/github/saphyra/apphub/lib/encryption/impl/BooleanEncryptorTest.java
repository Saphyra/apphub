package com.github.saphyra.apphub.lib.encryption.impl;

import com.github.saphyra.apphub.lib.common_util.Base64Encoder;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class BooleanEncryptorTest {
    private BooleanEncryptor underTest;

    private final StringEncryptor stringEncryptor = new StringEncryptor(new Base64Encoder());

    @Before
    public void setUp() {
        underTest = new BooleanEncryptor(stringEncryptor);
    }

    @Test
    public void encryptDecrypt() {
        String encrypted = underTest.encrypt(true, "key");
        boolean decrypted = underTest.decrypt(encrypted, "key");
        assertThat(decrypted).isTrue();
    }
}