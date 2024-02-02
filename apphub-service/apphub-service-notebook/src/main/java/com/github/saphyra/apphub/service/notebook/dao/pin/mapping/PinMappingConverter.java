package com.github.saphyra.apphub.service.notebook.dao.pin.mapping;

import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class PinMappingConverter extends ConverterBase<PinMappingEntity, PinMapping> {
    private final UuidConverter uuidConverter;

    @Override
    protected PinMappingEntity processDomainConversion(PinMapping domain) {
        return PinMappingEntity.builder()
            .pinMappingId(uuidConverter.convertDomain(domain.getPinMappingId()))
            .userId(uuidConverter.convertDomain(domain.getUserId()))
            .pinGroupId(uuidConverter.convertDomain(domain.getPinGroupId()))
            .listItemId(uuidConverter.convertDomain(domain.getListItemId()))
            .build();
    }

    @Override
    protected PinMapping processEntityConversion(PinMappingEntity entity) {
        return PinMapping.builder()
            .pinMappingId(uuidConverter.convertEntity(entity.getPinMappingId()))
            .userId(uuidConverter.convertEntity(entity.getUserId()))
            .pinGroupId(uuidConverter.convertEntity(entity.getPinGroupId()))
            .listItemId(uuidConverter.convertEntity(entity.getListItemId()))
            .build();
    }
}
