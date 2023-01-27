package com.github.saphyra.apphub.service.platform.encryption.encryption_key.dao;

import com.github.saphyra.apphub.api.platform.encryption.model.DataType;
import com.github.saphyra.apphub.api.platform.encryption.model.EncryptionKey;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class EncryptionKeyConverterTest {
    private static final UUID EXTERNAL_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String ENCRYPTION_KEY = "encryption-key";
    private static final String EXTERNAL_ID_STRING = "external-is";
    private static final String USER_ID_STRING = "user-id";

    @Mock
    private UuidConverter uuidConverter;

    @InjectMocks
    private EncryptionKeyConverter underTest;

    @Test
    public void convertDomain() {
        EncryptionKey encryptionKey = EncryptionKey.builder()
            .externalId(EXTERNAL_ID)
            .dataType(DataType.TEST)
            .userId(USER_ID)
            .encryptionKey(ENCRYPTION_KEY)
            .build();

        given(uuidConverter.convertDomain(EXTERNAL_ID)).willReturn(EXTERNAL_ID_STRING);
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);

        EncryptionKeyEntity result = underTest.convertDomain(encryptionKey);

        assertThat(result.getPk().getExternalId()).isEqualTo(EXTERNAL_ID_STRING);
        assertThat(result.getPk().getDataType()).isEqualTo(DataType.TEST.name());
        assertThat(result.getUserId()).isEqualTo(USER_ID_STRING);
        assertThat(result.getEncryptionKey()).isEqualTo(ENCRYPTION_KEY);
    }

    @Test
    public void convertEntity() {
        EncryptionKeyEntity entity = EncryptionKeyEntity.builder()
            .pk(EncryptionKeyPk.builder()
                .externalId(EXTERNAL_ID_STRING)
                .dataType(DataType.TEST.name())
                .build())
            .userId(USER_ID_STRING)
            .encryptionKey(ENCRYPTION_KEY)
            .build();

        given(uuidConverter.convertEntity(EXTERNAL_ID_STRING)).willReturn(EXTERNAL_ID);
        given(uuidConverter.convertEntity(USER_ID_STRING)).willReturn(USER_ID);

        EncryptionKey result = underTest.convertEntity(entity);

        assertThat(result.getExternalId()).isEqualTo(EXTERNAL_ID);
        assertThat(result.getDataType()).isEqualTo(DataType.TEST);
        assertThat(result.getUserId()).isEqualTo(USER_ID);
        assertThat(result.getEncryptionKey()).isEqualTo(ENCRYPTION_KEY);
    }
}