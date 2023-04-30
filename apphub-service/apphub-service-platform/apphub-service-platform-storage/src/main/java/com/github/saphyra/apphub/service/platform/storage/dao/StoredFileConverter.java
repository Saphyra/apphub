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
    private final UuidConverter uuidConverter;
    private final StringEncryptor stringEncryptor;
    private final AccessTokenProvider accessTokenProvider;
    private final LongEncryptor longEncryptor;

    @Override
    protected StoredFileEntity processDomainConversion(StoredFile domain) {
        String userId = accessTokenProvider.getUserIdAsString();
        return StoredFileEntity.builder()
            .storedFileId(uuidConverter.convertDomain(domain.getStoredFileId()))
            .userId(uuidConverter.convertDomain(domain.getUserId()))
            .createdAt(domain.getCreatedAt())
            .fileUploaded(domain.isFileUploaded())
            .fileName(stringEncryptor.encryptEntity(domain.getFileName(), userId))
            .size(longEncryptor.encryptEntity(domain.getSize(), userId))
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
            .fileName(stringEncryptor.decryptEntity(entity.getFileName(), userId))
            .size(longEncryptor.decryptEntity(entity.getSize(), userId))
            .build();
    }
}
