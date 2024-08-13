package com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.dao.storage_box;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.encryption.impl.StringEncryptor;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.dao.storage_box.StorageBoxConverter.COLUMN_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class StorageBoxConverterTest {
    private static final UUID STORAGE_BOX_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String NAME = "name";
    private static final String STORAGE_BOX_ID_STRING = "storage-box-id";
    private static final String USER_ID_STRING = "user-id";
    private static final String USER_ID_FROM_ACCESS_TOKEN = "user-id-from-access-token";
    private static final String ENCRYPTED_NAME = "encrypted-name";

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private StringEncryptor stringEncryptor;

    @Mock
    private AccessTokenProvider accessTokenProvider;

    @InjectMocks
    private StorageBoxConverter underTest;

    @Test
    void convertDomain() {
        StorageBox domain = StorageBox.builder()
            .storageBoxId(STORAGE_BOX_ID)
            .userId(USER_ID)
            .name(NAME)
            .build();

        given(uuidConverter.convertDomain(STORAGE_BOX_ID)).willReturn(STORAGE_BOX_ID_STRING);
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(accessTokenProvider.getUserIdAsString()).willReturn(USER_ID_FROM_ACCESS_TOKEN);

        given(stringEncryptor.encrypt(NAME, USER_ID_FROM_ACCESS_TOKEN, STORAGE_BOX_ID_STRING, COLUMN_NAME)).willReturn(ENCRYPTED_NAME);

        assertThat(underTest.convertDomain(domain))
            .returns(STORAGE_BOX_ID_STRING, StorageBoxEntity::getStorageBoxId)
            .returns(USER_ID_STRING, StorageBoxEntity::getUserId)
            .returns(ENCRYPTED_NAME, StorageBoxEntity::getName);
    }

    @Test
    void convertEntity() {
        StorageBoxEntity entity = StorageBoxEntity.builder()
            .storageBoxId(STORAGE_BOX_ID_STRING)
            .userId(USER_ID_STRING)
            .name(ENCRYPTED_NAME)
            .build();

        given(uuidConverter.convertEntity(STORAGE_BOX_ID_STRING)).willReturn(STORAGE_BOX_ID);
        given(uuidConverter.convertEntity(USER_ID_STRING)).willReturn(USER_ID);
        given(accessTokenProvider.getUserIdAsString()).willReturn(USER_ID_FROM_ACCESS_TOKEN);

        given(stringEncryptor.decrypt(ENCRYPTED_NAME, USER_ID_FROM_ACCESS_TOKEN, STORAGE_BOX_ID_STRING, COLUMN_NAME)).willReturn(NAME);

        assertThat(underTest.convertEntity(entity))
            .returns(STORAGE_BOX_ID, StorageBox::getStorageBoxId)
            .returns(USER_ID, StorageBox::getUserId)
            .returns(NAME, StorageBox::getName);
    }
}