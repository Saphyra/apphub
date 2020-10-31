package com.github.saphyra.apphub.lib.encryption.impl;

import com.github.saphyra.apphub.lib.common_util.Base64Encoder;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;

public class StringEncryptorTest {
    private static final String TEST_ENTITY = "test_entity";
    private static final String KEY = "key";

    private StringEncryptor underTest;

    @Before
    public void setUp() {
        underTest = new StringEncryptor(new Base64Encoder());
    }

    @Test
    public void testEncryptEntityShouldReturnNullWhenNull() {
        assertNull(underTest.encryptEntity(null, KEY));
    }

    @Test
    public void testDecryptEntityShouldReturnNullWhenNull() {
        assertNull(underTest.decryptEntity(null, KEY));
    }

    @Test
    public void testShouldEncryptAndDecrypt() {
        String encrypted = underTest.encryptEntity(TEST_ENTITY, KEY);
        assertNotEquals(TEST_ENTITY, encrypted);
        String decrypted = underTest.decrypt(encrypted, KEY);
        assertEquals(TEST_ENTITY, decrypted);
    }
}