package com.github.saphyra.apphub.service.platform.encryption.shared_data.dao;

import com.github.saphyra.apphub.api.platform.encryption.model.AccessMode;
import com.github.saphyra.apphub.api.platform.encryption.model.DataType;
import com.github.saphyra.apphub.api.platform.encryption.model.SharedData;
import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class SharedDataConverter extends ConverterBase<SharedDataEntity, SharedData> {
    private final UuidConverter uuidConverter;

    @Override
    protected SharedDataEntity processDomainConversion(SharedData domain) {
        return SharedDataEntity.builder()
            .sharedDataId(uuidConverter.convertDomain(domain.getSharedDataId()))
            .externalId(uuidConverter.convertDomain(domain.getExternalId()))
            .dataType(domain.getDataType().name())
            .sharedWith(uuidConverter.convertDomain(domain.getSharedWith()))
            .publicData(domain.getPublicData())
            .accessMode(domain.getAccessMode().name())
            .build();
    }

    @Override
    protected SharedData processEntityConversion(SharedDataEntity entity) {
        return SharedData.builder()
            .sharedDataId(uuidConverter.convertEntity(entity.getSharedDataId()))
            .externalId(uuidConverter.convertEntity(entity.getExternalId()))
            .dataType(DataType.valueOf(entity.getDataType()))
            .sharedWith(uuidConverter.convertEntity(entity.getSharedWith()))
            .publicData(entity.isPublicData())
            .accessMode(AccessMode.valueOf(entity.getAccessMode()))
            .build();
    }
}
