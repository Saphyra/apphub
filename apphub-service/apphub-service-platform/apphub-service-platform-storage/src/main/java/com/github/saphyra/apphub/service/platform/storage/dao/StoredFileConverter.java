package com.github.saphyra.apphub.service.platform.storage.dao;

import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.encryption.impl.LongEncryptor;
import com.github.saphyra.apphub.lib.encryption.impl.StringEncryptor;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class StoredFileConverter extends ConverterBase<StoredFileEntity, StoredFile> {
    static final String COLUMN_FILE_NAME = "file-name";
    static final String COLUMN_SIZE = "size";

    private final UuidConverter uuidConverter;
    private final StringEncryptor stringEncryptor;
    private final AccessTokenProvider accessTokenProvider;
    private final LongEncryptor longEncryptor;

    @Override
    protected StoredFileEntity processDomainConversion(StoredFile domain) {
        String userId = accessTokenProvider.getUserIdAsString();
        String storedFileId = uuidConverter.convertDomain(domain.getStoredFileId());
        return StoredFileEntity.builder()
            .storedFileId(storedFileId)
            .userId(uuidConverter.convertDomain(domain.getUserId()))
            .createdAt(domain.getCreatedAt())
            .fileUploaded(domain.isFileUploaded())
            .fileName(stringEncryptor.encrypt(domain.getFileName(), userId, storedFileId, COLUMN_FILE_NAME))
            .size(longEncryptor.encrypt(domain.getSize(), userId, storedFileId, COLUMN_SIZE))
            .build();
    }

    @Override
    protected StoredFile processEntityConversion(StoredFileEntity entity) {
        String userId = accessTokenProvider.getUserIdAsString();
        return StoredFile.builder()
            .storedFileId(uuidConverter.convertEntity(entity.getStoredFileId()))
            .userId(uuidConverter.convertEntity(entity.getUserId()))
            .createdAt(entity.getCreatedAt())
            .fileUploaded(entity.isFileUploaded())
            .fileName(stringEncryptor.decrypt(entity.getFileName(), userId, entity.getStoredFileId(), COLUMN_FILE_NAME))
            .size(longEncryptor.decrypt(entity.getSize(), userId, entity.getStoredFileId(), COLUMN_SIZE))
            .build();
    }
}
