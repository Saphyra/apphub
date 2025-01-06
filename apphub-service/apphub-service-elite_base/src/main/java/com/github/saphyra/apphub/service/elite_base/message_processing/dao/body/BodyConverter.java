package com.github.saphyra.apphub.service.elite_base.message_processing.dao.body;

import com.github.saphyra.apphub.lib.common_util.DateTimeConverter;
import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class BodyConverter extends ConverterBase<BodyEntity, Body> {
    private final UuidConverter uuidConverter;
    private final DateTimeConverter dateTimeConverter;

    @Override
    protected BodyEntity processDomainConversion(Body domain) {
        return BodyEntity.builder()
            .id(uuidConverter.convertDomain(domain.getId()))
            .lastUpdate(dateTimeConverter.convertDomain(domain.getLastUpdate()))
            .starSystemId(uuidConverter.convertDomain(domain.getStarSystemId()))
            .type(domain.getType())
            .bodyId(domain.getBodyId())
            .bodyName(domain.getBodyName())
            .distanceFromStar(domain.getDistanceFromStar())
            .build();
    }

    @Override
    protected Body processEntityConversion(BodyEntity entity) {
        return Body.builder()
            .id(uuidConverter.convertEntity(entity.getId()))
            .lastUpdate(dateTimeConverter.convertToLocalDateTime(entity.getLastUpdate()))
            .starSystemId(uuidConverter.convertEntity(entity.getStarSystemId()))
            .type(entity.getType())
            .bodyId(entity.getBodyId())
            .bodyName(entity.getBodyName())
            .distanceFromStar(entity.getDistanceFromStar())
            .build();
    }
}
