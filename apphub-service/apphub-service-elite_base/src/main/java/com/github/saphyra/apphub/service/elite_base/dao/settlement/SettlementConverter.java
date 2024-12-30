package com.github.saphyra.apphub.service.elite_base.dao.settlement;

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
class SettlementConverter extends ConverterBase<SettlementEntity, Settlement> {
    private final UuidConverter uuidConverter;
    private final DateTimeConverter dateTimeConverter;

    @Override
    protected SettlementEntity processDomainConversion(Settlement domain) {
        return SettlementEntity.builder()
            .id(uuidConverter.convertDomain(domain.getId()))
            .lastUpdate(dateTimeConverter.convertDomain(domain.getLastUpdate()))
            .starSystemId(uuidConverter.convertDomain(domain.getStarSystemId()))
            .bodyId(uuidConverter.convertDomain(domain.getBodyId()))
            .settlementName(domain.getSettlementName())
            .marketId(domain.getMarketId())
            .longitude(domain.getLongitude())
            .latitude(domain.getLatitude())
            .build();
    }

    @Override
    protected Settlement processEntityConversion(SettlementEntity entity) {
        return Settlement.builder()
            .id(uuidConverter.convertEntity(entity.getId()))
            .lastUpdate(dateTimeConverter.convertToLocalDateTime(entity.getLastUpdate()))
            .starSystemId(uuidConverter.convertEntity(entity.getStarSystemId()))
            .bodyId(uuidConverter.convertEntity(entity.getBodyId()))
            .settlementName(entity.getSettlementName())
            .marketId(entity.getMarketId())
            .longitude(entity.getLongitude())
            .latitude(entity.getLatitude())
            .build();
    }
}
