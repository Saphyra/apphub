package com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.dao.storage_box;

import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.encryption.impl.StringEncryptor;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class StorageBoxConverter extends ConverterBase<StorageBoxEntity, StorageBox> {
    static final String COLUMN_NAME = "name";

    private final UuidConverter uuidConverter;
    private final StringEncryptor stringEncryptor;
    private final AccessTokenProvider accessTokenProvider;

    @Override
    protected StorageBoxEntity processDomainConversion(StorageBox domain) {
        String userId = accessTokenProvider.getUserIdAsString();
        String storageBoxId = uuidConverter.convertDomain(domain.getStorageBoxId());

        return StorageBoxEntity.builder()
            .storageBoxId(storageBoxId)
            .userId(uuidConverter.convertDomain(domain.getUserId()))
            .name(stringEncryptor.encrypt(domain.getName(), userId, storageBoxId, COLUMN_NAME))
            .build();
    }

    @Override
    protected StorageBox processEntityConversion(StorageBoxEntity entity) {
        String userId = accessTokenProvider.getUserIdAsString();

        return StorageBox.builder()
            .storageBoxId(uuidConverter.convertEntity(entity.getStorageBoxId()))
            .userId(uuidConverter.convertEntity(entity.getUserId()))
            .name(stringEncryptor.decrypt(entity.getName(), userId, entity.getStorageBoxId(), COLUMN_NAME))
            .build();
    }
}
