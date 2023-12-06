package com.github.saphyra.apphub.lib.encryption.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class IntegerEncryptorTest {
    private static final String KEY = "key";
    private static final String ENCRYPTED_STRING = "encrypted_string";
    private static final String ENTITY_ID = "entity-id";
    private static final String COLUMN = "column";

    @Mock
    private StringEncryptor stringEncryptor;

    @InjectMocks
    private IntegerEncryptor underTest;

    @Test
    void encrypt() {
        //GIVEN
        when(stringEncryptor.encrypt("2", KEY, ENTITY_ID, COLUMN)).thenReturn(ENCRYPTED_STRING);
        //WHEN
        String result = underTest.encrypt(2, KEY, ENTITY_ID, COLUMN);
        //THEN
        assertThat(result).isEqualTo(ENCRYPTED_STRING);
    }

    @Test
    void encrypt_null() {
        //GIVEN
        when(stringEncryptor.encrypt(null, KEY, ENTITY_ID, COLUMN)).thenReturn(ENCRYPTED_STRING);
        //WHEN
        String result = underTest.encrypt(null, KEY, ENTITY_ID, COLUMN);
        //THEN
        assertThat(result).isEqualTo(ENCRYPTED_STRING);
    }

    @Test
    void decrypt() {
        //GIVEN
        when(stringEncryptor.decrypt(ENCRYPTED_STRING, KEY, ENTITY_ID, COLUMN)).thenReturn("2");
        //WHEN
        Integer result = underTest.decrypt(ENCRYPTED_STRING, KEY, ENTITY_ID, COLUMN);
        //THEN
        assertThat(result).isEqualTo(2);
    }

    @Test
    void decrypt_null() {
        //GIVEN
        when(stringEncryptor.decrypt(null, KEY, ENTITY_ID, COLUMN)).thenReturn(null);
        //WHEN
        Integer result = underTest.decrypt(null, KEY, ENTITY_ID, COLUMN);
        //THEN
        assertThat(result).isNull();
    }
}