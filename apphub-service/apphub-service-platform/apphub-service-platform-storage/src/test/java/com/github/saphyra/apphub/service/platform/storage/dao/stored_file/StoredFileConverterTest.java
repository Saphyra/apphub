package com.github.saphyra.apphub.service.platform.storage.dao.stored_file;

import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.encryption.impl.LongEncryptor;
import com.github.saphyra.apphub.lib.encryption.impl.StringEncryptor;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.github.saphyra.apphub.service.platform.storage.dao.stored_file.StoredFileConstants.COLUMN_FILE_NAME;
import static com.github.saphyra.apphub.service.platform.storage.dao.stored_file.StoredFileConstants.COLUMN_SIZE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class StoredFileConverterTest {
    private static final UUID STORED_FILE_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String STORED_FILE_ID_STRING = "stored-file-id";
    private static final String USER_ID_STRING = "user-id";
    private static final LocalDateTime CREATED_AT = LocalDateTime.now();
    private static final LocalDateTime EXPIRATION = LocalDateTime.now().plusDays(1);
    private static final long CREATED_AT_EPOCH = 1000L;
    private static final long EXPIRATION_EPOCH = 2000L;
    private static final String FILE_NAME = "file-name";
    private static final String FILE_NAME_ENCRYPTED = "file-name-encrypted";
    private static final long SIZE = 12345L;
    private static final String SIZE_ENCRYPTED = "size-encrypted";
    private static final Storage STORAGE = Storage.FTP;

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private StringEncryptor stringEncryptor;

    @Mock
    private AccessTokenProvider accessTokenProvider;

    @Mock
    private LongEncryptor longEncryptor;

    @Mock
    private DateTimeUtil dateTimeUtil;

    @InjectMocks
    private StoredFileConverter underTest;

    @Test
    void convertDomain() {
        StoredFile domain = StoredFile.builder()
            .storedFileId(STORED_FILE_ID)
            .userId(USER_ID)
            .createdAt(CREATED_AT)
            .expiration(EXPIRATION)
            .fileName(FILE_NAME)
            .size(SIZE)
            .storage(STORAGE)
            .build();

        given(accessTokenProvider.getUserIdAsString()).willReturn(USER_ID_STRING);
        given(uuidConverter.convertDomain(STORED_FILE_ID)).willReturn(STORED_FILE_ID_STRING);
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(dateTimeUtil.toEpochSecond(CREATED_AT)).willReturn(CREATED_AT_EPOCH);
        given(dateTimeUtil.toEpochSecond(EXPIRATION)).willReturn(EXPIRATION_EPOCH);
        given(stringEncryptor.encrypt(FILE_NAME, USER_ID_STRING, STORED_FILE_ID_STRING, COLUMN_FILE_NAME)).willReturn(FILE_NAME_ENCRYPTED);
        given(longEncryptor.encrypt(SIZE, USER_ID_STRING, STORED_FILE_ID_STRING, COLUMN_SIZE)).willReturn(SIZE_ENCRYPTED);

        assertThat(underTest.convertDomain(domain))
            .returns(STORED_FILE_ID_STRING, StoredFileEntity::getStoredFileId)
            .returns(USER_ID_STRING, StoredFileEntity::getUserId)
            .returns(CREATED_AT_EPOCH, StoredFileEntity::getCreatedAt)
            .returns(EXPIRATION_EPOCH, StoredFileEntity::getExpiration)
            .returns(FILE_NAME_ENCRYPTED, StoredFileEntity::getFileName)
            .returns(SIZE_ENCRYPTED, StoredFileEntity::getSize)
            .returns(STORAGE.name(), StoredFileEntity::getStorage);
    }

    @Test
    void convertEntity() {
        StoredFileEntity entity = StoredFileEntity.builder()
            .storedFileId(STORED_FILE_ID_STRING)
            .userId(USER_ID_STRING)
            .createdAt(CREATED_AT_EPOCH)
            .expiration(EXPIRATION_EPOCH)
            .fileName(FILE_NAME_ENCRYPTED)
            .size(SIZE_ENCRYPTED)
            .storage(STORAGE.name())
            .build();

        given(accessTokenProvider.getUserIdAsString()).willReturn(USER_ID_STRING);
        given(uuidConverter.convertEntity(STORED_FILE_ID_STRING)).willReturn(STORED_FILE_ID);
        given(uuidConverter.convertEntity(USER_ID_STRING)).willReturn(USER_ID);
        given(dateTimeUtil.fromEpochSecond(CREATED_AT_EPOCH)).willReturn(CREATED_AT);
        given(dateTimeUtil.fromEpochSecond(EXPIRATION_EPOCH)).willReturn(EXPIRATION);
        given(stringEncryptor.decrypt(FILE_NAME_ENCRYPTED, USER_ID_STRING, STORED_FILE_ID_STRING, COLUMN_FILE_NAME)).willReturn(FILE_NAME);
        given(longEncryptor.decrypt(SIZE_ENCRYPTED, USER_ID_STRING, STORED_FILE_ID_STRING, COLUMN_SIZE)).willReturn(SIZE);

        assertThat(underTest.convertEntity(entity))
            .returns(STORED_FILE_ID, StoredFile::getStoredFileId)
            .returns(USER_ID, StoredFile::getUserId)
            .returns(CREATED_AT, StoredFile::getCreatedAt)
            .returns(EXPIRATION, StoredFile::getExpiration)
            .returns(FILE_NAME, StoredFile::getFileName)
            .returns(SIZE, StoredFile::getSize)
            .returns(STORAGE, StoredFile::getStorage);
    }

    @Test
    void convertEntity_expirationIsZero() {
        StoredFileEntity entity = StoredFileEntity.builder()
            .storedFileId(STORED_FILE_ID_STRING)
            .userId(USER_ID_STRING)
            .createdAt(CREATED_AT_EPOCH)
            .expiration(0L)
            .fileName(FILE_NAME_ENCRYPTED)
            .size(SIZE_ENCRYPTED)
            .storage(STORAGE.name())
            .build();

        given(accessTokenProvider.getUserIdAsString()).willReturn(USER_ID_STRING);
        given(uuidConverter.convertEntity(STORED_FILE_ID_STRING)).willReturn(STORED_FILE_ID);
        given(uuidConverter.convertEntity(USER_ID_STRING)).willReturn(USER_ID);
        given(dateTimeUtil.fromEpochSecond(CREATED_AT_EPOCH)).willReturn(CREATED_AT);
        given(stringEncryptor.decrypt(FILE_NAME_ENCRYPTED, USER_ID_STRING, STORED_FILE_ID_STRING, COLUMN_FILE_NAME)).willReturn(FILE_NAME);
        given(longEncryptor.decrypt(SIZE_ENCRYPTED, USER_ID_STRING, STORED_FILE_ID_STRING, COLUMN_SIZE)).willReturn(SIZE);

        assertThat(underTest.convertEntity(entity))
            .returns(STORED_FILE_ID, StoredFile::getStoredFileId)
            .returns(USER_ID, StoredFile::getUserId)
            .returns(CREATED_AT, StoredFile::getCreatedAt)
            .returns(null, StoredFile::getExpiration)
            .returns(FILE_NAME, StoredFile::getFileName)
            .returns(SIZE, StoredFile::getSize)
            .returns(STORAGE, StoredFile::getStorage);
    }
}