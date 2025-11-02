package com.github.saphyra.apphub.lib.encryption.impl;

import com.github.saphyra.apphub.lib.common_util.Base64Encoder;
import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.lib.encryption.base.DefaultEncryptor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@ExtendWith(MockitoExtension.class)
public class DefaultStringEncryptorTest {
    private static final String TEST_ENTITY = "test_entity";
    private static final String KEY = "key";
    private static final String ENTITY_ID = "entity-id";
    private static final String COLUMN = "column";
    private static final String FAKE_ENTITY_ID = "fake-entity-id";
    private static final String FAKE_COLUMN = "fake-column";

    @Spy
    private final Base64Encoder base64Encoder = new Base64Encoder();

    @Spy
    private final ObjectMapperWrapper objectMapperWrapper = new ObjectMapperWrapper(new ObjectMapper());

    @InjectMocks
    private DefaultStringEncryptor underTest;

    @Test
    void encryptDecrypt() {
        String encrypted = underTest.encrypt(TEST_ENTITY, KEY, ENTITY_ID, COLUMN);
        assertThat(underTest.decrypt(encrypted, KEY, ENTITY_ID, COLUMN)).isEqualTo(TEST_ENTITY);
    }

    @Test
    void oldSchema() {
        String encrypted = new DefaultEncryptor(base64Encoder, KEY)
            .encrypt(TEST_ENTITY);

        assertThat(underTest.decrypt(encrypted, KEY, ENTITY_ID, COLUMN)).isEqualTo(TEST_ENTITY);
    }

    @Test
    void entityIdMismatch() {
        String encrypted = underTest.encrypt(TEST_ENTITY, KEY, FAKE_ENTITY_ID, COLUMN);

        Throwable ex = catchThrowable(() -> underTest.decrypt(encrypted, KEY, ENTITY_ID, COLUMN));

        assertThat(ex).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void columnMismatch() {
        String encrypted = underTest.encrypt(TEST_ENTITY, KEY, ENTITY_ID, FAKE_COLUMN);

        Throwable ex = catchThrowable(() -> underTest.decrypt(encrypted, KEY, ENTITY_ID, COLUMN));

        assertThat(ex).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void decryptNull() {
        assertThat(underTest.decrypt(null, KEY, ENTITY_ID, COLUMN)).isNull();
    }
}