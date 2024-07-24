package com.github.saphyra.apphub.lib.encryption.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class LocalDateEncryptorTest {
    private static final LocalDate DATE = LocalDate.now();
    private static final String KEY = "key";
    private static final String ENTITY_ID = "entity-id";
    private static final String COLUMN = "column";
    private static final String ENCRYPTED_DATE = "encrpyted-date";

    @Mock
    private StringEncryptor stringEncryptor;

    @InjectMocks
    private LocalDateEncryptor underTest;

    @Test
    void encrypt() {
        given(stringEncryptor.encrypt(DATE.toString(), KEY, ENTITY_ID, COLUMN)).willReturn(ENCRYPTED_DATE);

        assertThat(underTest.encrypt(DATE, KEY, ENTITY_ID, COLUMN)).isEqualTo(ENCRYPTED_DATE);
    }

    @Test
    void decrypt() {
        given(stringEncryptor.decrypt(ENCRYPTED_DATE, KEY, ENTITY_ID, COLUMN)).willReturn(DATE.toString());

        assertThat(underTest.decrypt(ENCRYPTED_DATE, KEY, ENTITY_ID, COLUMN)).isEqualTo(DATE);
    }
}