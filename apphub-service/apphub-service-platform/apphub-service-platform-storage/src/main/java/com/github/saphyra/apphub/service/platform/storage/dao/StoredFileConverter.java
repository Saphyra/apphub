package com.github.saphyra.apphub.service.platform.storage.dao;

import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.encryption.impl.IntegerEncryptor;
import com.github.saphyra.apphub.lib.encryption.impl.StringEncryptor;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class StoredFileConverter extends ConverterBase<StoredFileEntity, StoredFile> {
    private final UuidConverter uuidConverter;
    private final StringEncryptor stringEncryptor;
    private final AccessTokenProvider accessTokenProvider;
    private final IntegerEncryptor integerEncryptor;

    @Override
    protected StoredFileEntity processDomainConversion(StoredFile domain) {
        String userId = accessTokenProvider.getUserIdAsString();
        return StoredFileEntity.builder()
            .storedFileId(uuidConverter.convertDomain(domain.getStoredFileId()))
            .userId(uuidConverter.convertDomain(domain.getUserId()))
            .createdAt(domain.getCreatedAt())
            .fileUploaded(domain.isFileUploaded())
            .extension(stringEncryptor.encryptEntity(domain.getExtension(), userId))
            .size(integerEncryptor.encryptEntity(domain.getSize(), userId))
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
            .extension(stringEncryptor.decryptEntity(entity.getExtension(), userId))
            .size(integerEncryptor.decryptEntity(entity.getSize(), userId))
            .build();
    }
}
