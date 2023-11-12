package com.github.saphyra.apphub.lib.encryption.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class BooleanEncryptorTest {
    private static final String KEY = "key";
    private static final String ENTITY_ID = "entity-id";
    private static final String COLUMN = "column";
    private static final String ENCRYPTED_ENTITY = "encrypted-entity";
    @Mock
    private StringEncryptor stringEncryptor;

    @InjectMocks
    private BooleanEncryptor underTest;

    @Test
    void encrypt() {
        given(stringEncryptor.encrypt(Boolean.TRUE.toString(), KEY, ENTITY_ID, COLUMN)).willReturn(ENCRYPTED_ENTITY);

        assertThat(underTest.encrypt(true, KEY, ENTITY_ID, COLUMN)).isEqualTo(ENCRYPTED_ENTITY);
    }

    @Test
    void encrypt_null() {
        given(stringEncryptor.encrypt(null, KEY, ENTITY_ID, COLUMN)).willReturn(ENCRYPTED_ENTITY);

        assertThat(underTest.encrypt(null, KEY, ENTITY_ID, COLUMN)).isEqualTo(ENCRYPTED_ENTITY);
    }

    @Test
    void decrypt() {
        given(stringEncryptor.decrypt(ENCRYPTED_ENTITY, KEY, ENTITY_ID, COLUMN)).willReturn(Boolean.TRUE.toString());

        assertThat(underTest.decrypt(ENCRYPTED_ENTITY, KEY, ENTITY_ID, COLUMN)).isTrue();
    }

    @Test
    void decrypt_null() {
        given(stringEncryptor.decrypt(ENCRYPTED_ENTITY, KEY, ENTITY_ID, COLUMN)).willReturn(null);

        assertThat(underTest.decrypt(ENCRYPTED_ENTITY, KEY, ENTITY_ID, COLUMN)).isNull();
    }
}