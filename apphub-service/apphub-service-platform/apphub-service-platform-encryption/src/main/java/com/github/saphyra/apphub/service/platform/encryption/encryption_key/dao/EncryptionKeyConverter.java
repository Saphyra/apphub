package com.github.saphyra.apphub.service.platform.encryption.encryption_key.dao;

import com.github.saphyra.apphub.api.platform.encryption.model.DataType;
import com.github.saphyra.apphub.api.platform.encryption.model.EncryptionKey;
import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class EncryptionKeyConverter extends ConverterBase<EncryptionKeyEntity, EncryptionKey> {
    private final UuidConverter uuidConverter;

    @Override
    protected EncryptionKeyEntity processDomainConversion(EncryptionKey domain) {
        return EncryptionKeyEntity.builder()
            .pk(EncryptionKeyPk.builder()
                .externalId(uuidConverter.convertDomain(domain.getExternalId()))
                .dataType(domain.getDataType().name())
                .build())
            .userId(uuidConverter.convertDomain(domain.getUserId()))
            .encryptionKey(domain.getEncryptionKey())
            .build();
    }

    @Override
    protected EncryptionKey processEntityConversion(EncryptionKeyEntity entity) {
        return EncryptionKey.builder()
            .externalId(uuidConverter.convertEntity(entity.getPk().getExternalId()))
            .dataType(DataType.valueOf(entity.getPk().getDataType()))
            .userId(uuidConverter.convertEntity(entity.getUserId()))
            .encryptionKey(entity.getEncryptionKey())
            .build();
    }
}
