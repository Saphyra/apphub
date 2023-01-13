package com.github.saphyra.apphub.service.platform.storage.dao;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.encryption.impl.LongEncryptor;
import com.github.saphyra.apphub.lib.encryption.impl.StringEncryptor;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class StoredFileConverterTest {
    private static final UUID STORED_FILE_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final LocalDateTime CREATED_AT = LocalDateTime.now();
    private static final String FILE_NAME = "file-name";
    private static final String EXTENSION = "extension";
    private static final long SIZE = 2345;
    private static final String ACCESS_TOKEN_USER_ID = "access-token-user-id";
    private static final String STORED_FILE_ID_STRING = "stored-file-id";
    private static final String USER_ID_STRING = "user-id";
    private static final String ENCRYPTED_FILE_NAME = "encrypted-file-name";
    private static final String ENCRYPTED_EXTENSION = "encrypted-extension";
    private static final String ENCRYPTED_SIZE = "encrypted-size";

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private StringEncryptor stringEncryptor;

    @Mock
    private AccessTokenProvider accessTokenProvider;

    @Mock
    private LongEncryptor longEncryptor;

    @InjectMocks
    private StoredFileConverter underTest;

    @Test
    public void convertDomain() {
        StoredFile storedFile = StoredFile.builder()
            .storedFileId(STORED_FILE_ID)
            .userId(USER_ID)
            .createdAt(CREATED_AT)
            .fileUploaded(true)
            .fileName(FILE_NAME)
            .extension(EXTENSION)
            .size(SIZE)
            .build();

        given(accessTokenProvider.getUserIdAsString()).willReturn(ACCESS_TOKEN_USER_ID);

        given(uuidConverter.convertDomain(STORED_FILE_ID)).willReturn(STORED_FILE_ID_STRING);
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(stringEncryptor.encryptEntity(FILE_NAME, ACCESS_TOKEN_USER_ID)).willReturn(ENCRYPTED_FILE_NAME);
        given(stringEncryptor.encryptEntity(EXTENSION, ACCESS_TOKEN_USER_ID)).willReturn(ENCRYPTED_EXTENSION);
        given(longEncryptor.encryptEntity(SIZE, ACCESS_TOKEN_USER_ID)).willReturn(ENCRYPTED_SIZE);

        StoredFileEntity result = underTest.convertDomain(storedFile);

        assertThat(result.getStoredFileId()).isEqualTo(STORED_FILE_ID_STRING);
        assertThat(result.getUserId()).isEqualTo(USER_ID_STRING);
        assertThat(result.getCreatedAt()).isEqualTo(CREATED_AT);
        assertThat(result.isFileUploaded()).isTrue();
        assertThat(result.getFileName()).isEqualTo(ENCRYPTED_FILE_NAME);
        assertThat(result.getExtension()).isEqualTo(ENCRYPTED_EXTENSION);
        assertThat(result.getSize()).isEqualTo(ENCRYPTED_SIZE);
    }

    @Test
    public void convertEntity() {
        StoredFileEntity storedFile = StoredFileEntity.builder()
            .storedFileId(STORED_FILE_ID_STRING)
            .userId(USER_ID_STRING)
            .createdAt(CREATED_AT)
            .fileUploaded(true)
            .fileName(ENCRYPTED_FILE_NAME)
            .extension(ENCRYPTED_EXTENSION)
            .size(ENCRYPTED_SIZE)
            .build();

        given(accessTokenProvider.getUserIdAsString()).willReturn(ACCESS_TOKEN_USER_ID);

        given(uuidConverter.convertEntity(STORED_FILE_ID_STRING)).willReturn(STORED_FILE_ID);
        given(uuidConverter.convertEntity(USER_ID_STRING)).willReturn(USER_ID);
        given(stringEncryptor.decryptEntity(ENCRYPTED_FILE_NAME, ACCESS_TOKEN_USER_ID)).willReturn(FILE_NAME);
        given(stringEncryptor.decryptEntity(ENCRYPTED_EXTENSION, ACCESS_TOKEN_USER_ID)).willReturn(EXTENSION);
        given(longEncryptor.decryptEntity(ENCRYPTED_SIZE, ACCESS_TOKEN_USER_ID)).willReturn(SIZE);

        StoredFile result = underTest.convertEntity(storedFile);

        assertThat(result.getStoredFileId()).isEqualTo(STORED_FILE_ID);
        assertThat(result.getUserId()).isEqualTo(USER_ID);
        assertThat(result.getCreatedAt()).isEqualTo(CREATED_AT);
        assertThat(result.isFileUploaded()).isTrue();
        assertThat(result.getFileName()).isEqualTo(FILE_NAME);
        assertThat(result.getExtension()).isEqualTo(EXTENSION);
        assertThat(result.getSize()).isEqualTo(SIZE);
    }
}