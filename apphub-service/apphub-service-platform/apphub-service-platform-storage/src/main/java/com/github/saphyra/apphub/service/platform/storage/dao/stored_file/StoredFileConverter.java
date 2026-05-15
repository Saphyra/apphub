package com.github.saphyra.apphub.service.platform.storage.dao.stored_file;

import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.encryption.impl.LongEncryptor;
import com.github.saphyra.apphub.lib.encryption.impl.StringEncryptor;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.github.saphyra.apphub.service.platform.storage.dao.stored_file.StoredFileConstants.COLUMN_FILE_NAME;
import static com.github.saphyra.apphub.service.platform.storage.dao.stored_file.StoredFileConstants.COLUMN_SIZE;

@Component
@RequiredArgsConstructor
//TODO unit test
class StoredFileConverter extends ConverterBase<StoredFileEntity, StoredFile> {
    private final UuidConverter uuidConverter;
    private final StringEncryptor stringEncryptor;
    private final AccessTokenProvider accessTokenProvider;
    private final LongEncryptor longEncryptor;
    private final DateTimeUtil dateTimeUtil;

    @Override
    protected StoredFileEntity processDomainConversion(StoredFile domain) {
        String userId = accessTokenProvider.getUserIdAsString();
        String storedFileId = uuidConverter.convertDomain(domain.getStoredFileId());
        return StoredFileEntity.builder()
            .storedFileId(storedFileId)
            .userId(uuidConverter.convertDomain(domain.getUserId()))
            .createdAt(dateTimeUtil.toEpochSecond(domain.getCreatedAt()))
            .expiration(dateTimeUtil.toEpochSecond(domain.getExpiration()))
            .fileName(stringEncryptor.encrypt(domain.getFileName(), userId, storedFileId, COLUMN_FILE_NAME))
            .size(longEncryptor.encrypt(domain.getSize(), userId, storedFileId, COLUMN_SIZE))
            .storage(domain.getStorage().name())
            .build();
    }

    @Override
    protected StoredFile processEntityConversion(StoredFileEntity entity) {
        String userId = accessTokenProvider.getUserIdAsString();
        return StoredFile.builder()
            .storedFileId(uuidConverter.convertEntity(entity.getStoredFileId()))
            .userId(uuidConverter.convertEntity(entity.getUserId()))
            .createdAt(dateTimeUtil.fromEpochSecond(entity.getCreatedAt()))
            .expiration(dateTimeUtil.fromEpochSecond(entity.getExpiration()))
            .fileName(stringEncryptor.decrypt(entity.getFileName(), userId, entity.getStoredFileId(), COLUMN_FILE_NAME))
            .size(longEncryptor.decrypt(entity.getSize(), userId, entity.getStoredFileId(), COLUMN_SIZE))
            .storage(Storage.valueOf(entity.getStorage()))
            .build();
    }
}
