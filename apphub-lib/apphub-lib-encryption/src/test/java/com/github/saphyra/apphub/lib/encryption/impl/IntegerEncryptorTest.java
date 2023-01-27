package com.github.saphyra.apphub.lib.encryption.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class IntegerEncryptorTest {
    private static final String KEY = "key";
    private static final String ENCRYPTED_STRING = "encrypted_string";

    @Mock
    private StringEncryptor stringEncryptor;

    @InjectMocks
    private IntegerEncryptor underTest;

    @Test
    public void testEncryptEntityShouldTransformAndCallStringEncryptor() {
        //GIVEN
        when(stringEncryptor.encryptEntity("2", KEY)).thenReturn(ENCRYPTED_STRING);
        //WHEN
        String result = underTest.encryptEntity(2, KEY);
        //THEN
        verify(stringEncryptor).encryptEntity("2", KEY);

        assertThat(result).isEqualTo(ENCRYPTED_STRING);
    }

    @Test
    public void testDecryptEntityShouldCallStringEncryptorAndTransform() {
        //GIVEN
        when(stringEncryptor.decryptEntity(ENCRYPTED_STRING, KEY)).thenReturn("2");
        //WHEN
        Integer result = underTest.decryptEntity(ENCRYPTED_STRING, KEY);
        //THEN
        verify(stringEncryptor).decryptEntity(ENCRYPTED_STRING, KEY);

        assertThat(result).isEqualTo(2);
    }
}