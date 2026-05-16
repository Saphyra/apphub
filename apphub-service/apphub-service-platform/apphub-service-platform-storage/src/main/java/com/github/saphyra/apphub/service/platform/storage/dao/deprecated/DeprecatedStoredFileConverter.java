package com.github.saphyra.apphub.service.platform.storage.dao.deprecated;

import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.encryption.impl.LongEncryptor;
import com.github.saphyra.apphub.lib.encryption.impl.StringEncryptor;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import com.github.saphyra.apphub.service.platform.storage.dao.stored_file.StoredFile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
@Deprecated(forRemoval = true)
class DeprecatedStoredFileConverter extends ConverterBase<DeprecatedStoredFileEntity, StoredFile> {
    static final String COLUMN_FILE_NAME = "file-name";
    static final String COLUMN_SIZE = "size";

    private final UuidConverter uuidConverter;
    private final StringEncryptor stringEncryptor;
    private final AccessTokenProvider accessTokenProvider;
    private final LongEncryptor longEncryptor;

    @Override
    protected DeprecatedStoredFileEntity processDomainConversion(StoredFile domain) {
        String userId = accessTokenProvider.getUserIdAsString();
        String storedFileId = uuidConverter.convertDomain(domain.getStoredFileId());
        return DeprecatedStoredFileEntity.builder()
            .storedFileId(storedFileId)
            .userId(uuidConverter.convertDomain(domain.getUserId()))
            .createdAt(domain.getCreatedAt())
            .fileUploaded(domain.isFileUploaded())
            .fileName(stringEncryptor.encrypt(domain.getFileName(), userId, storedFileId, COLUMN_FILE_NAME))
            .size(longEncryptor.encrypt(domain.getSize(), userId, storedFileId, COLUMN_SIZE))
            .storage(domain.getStorage())
            .build();
    }

    @Override
    protected StoredFile processEntityConversion(DeprecatedStoredFileEntity entity) {
        String userId = accessTokenProvider.getUserIdAsString();
        return StoredFile.builder()
            .storedFileId(uuidConverter.convertEntity(entity.getStoredFileId()))
            .userId(uuidConverter.convertEntity(entity.getUserId()))
            .createdAt(entity.getCreatedAt())
            .expiration(entity.isFileUploaded() ? null: LocalDateTime.now())
            .fileName(stringEncryptor.decrypt(entity.getFileName(), userId, entity.getStoredFileId(), COLUMN_FILE_NAME))
            .size(longEncryptor.decrypt(entity.getSize(), userId, entity.getStoredFileId(), COLUMN_SIZE))
            .storage(entity.getStorage())
            .build();
    }
}
