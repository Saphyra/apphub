package com.github.saphyra.apphub.lib.encryption.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
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
        assertEquals(ENCRYPTED_STRING, result);
    }

    @Test
    public void testDecryptEntityShouldCallStringEncryptorAndTransform() {
        //GIVEN
        when(stringEncryptor.decryptEntity(ENCRYPTED_STRING, KEY)).thenReturn("2");
        //WHEN
        Integer result = underTest.decryptEntity(ENCRYPTED_STRING, KEY);
        //THEN
        verify(stringEncryptor).decryptEntity(ENCRYPTED_STRING, KEY);
        assertEquals((Integer) 2, result);
    }
}