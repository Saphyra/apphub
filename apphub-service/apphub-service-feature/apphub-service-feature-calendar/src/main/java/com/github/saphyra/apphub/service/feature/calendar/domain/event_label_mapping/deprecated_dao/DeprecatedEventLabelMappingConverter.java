package com.github.saphyra.apphub.service.feature.calendar.domain.event_label_mapping.deprecated_dao;

import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@Deprecated(forRemoval = true)
class DeprecatedEventLabelMappingConverter extends ConverterBase<DeprecatedEventLabelMappingEntity, DeprecatedEventLabelMapping> {
    protected final UuidConverter uuidConverter;

    @Override
    protected DeprecatedEventLabelMappingEntity processDomainConversion(DeprecatedEventLabelMapping domain) {
        return DeprecatedEventLabelMappingEntity.builder()
            .eventId(uuidConverter.convertDomain(domain.getEventId()))
            .labelId(uuidConverter.convertDomain(domain.getLabelId()))
            .userId(uuidConverter.convertDomain(domain.getUserId()))
            .build();
    }

    @Override
    protected DeprecatedEventLabelMapping processEntityConversion(DeprecatedEventLabelMappingEntity entity) {
        return DeprecatedEventLabelMapping.builder()
            .eventId(uuidConverter.convertEntity(entity.getEventId()))
            .labelId(uuidConverter.convertEntity(entity.getLabelId()))
            .userId(uuidConverter.convertEntity(entity.getUserId()))
            .build();
    }
}
