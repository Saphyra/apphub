package com.github.saphyra.apphub.lib.encryption.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class LocalTimeEncryptorTest {
    private static final String ENCRYPTED = "encrypted";
    private static final LocalTime TIME = LocalTime.now();
    private static final String KEY = "key";
    private static final String ENTITY_ID = "entity_id";
    private static final String COLUMN = "column";

    @Mock
    private StringEncryptor stringEncryptor;

    @InjectMocks
    private LocalTimeEncryptor underTest;

    @Test
    void encrypt() {
        given(stringEncryptor.encrypt(TIME.toString(), KEY, ENTITY_ID, COLUMN)).willReturn(ENCRYPTED);

        assertThat(underTest.encrypt(TIME, KEY, ENTITY_ID, COLUMN)).isEqualTo(ENCRYPTED);
    }

    @Test
    void decrypt() {
        given(stringEncryptor.decrypt(ENCRYPTED, KEY, ENTITY_ID, COLUMN)).willReturn(TIME.toString());

        assertThat(underTest.decrypt(ENCRYPTED, KEY, ENTITY_ID, COLUMN)).isEqualTo(TIME);
    }
}