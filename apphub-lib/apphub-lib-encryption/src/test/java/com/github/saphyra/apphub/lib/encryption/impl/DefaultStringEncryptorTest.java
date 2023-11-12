package com.github.saphyra.apphub.lib.encryption.impl;

import com.github.saphyra.apphub.lib.common_util.Base64Encoder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DefaultStringEncryptorTest {
    private static final String TEST_ENTITY = "test_entity";
    private static final String KEY = "key";

    private DefaultStringEncryptor underTest;

    @BeforeEach
    public void setUp() {
        underTest = new DefaultStringEncryptor(new Base64Encoder());
    }

    @Test
    public void testEncryptEntityShouldReturnNullWhenNull() {
        assertThat(underTest.encryptEntity(null, KEY)).isNull();
    }

    @Test
    public void testDecryptEntityShouldReturnNullWhenNull() {
        assertThat(underTest.decryptEntity(null, KEY)).isNull();
    }

    @Test
    public void testShouldEncryptAndDecrypt() {
        String encrypted = underTest.encryptEntity(TEST_ENTITY, KEY);

        assertThat(encrypted).isNotEqualTo(TEST_ENTITY);

        String decrypted = underTest.decrypt(encrypted, KEY);

        assertThat(decrypted).isEqualTo(TEST_ENTITY);
    }
}