package com.github.saphyra.apphub.service.custom.elite_base.dao.station.station_service;

import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class StationServiceConverter extends ConverterBase<StationServiceEntity, StationService> {
    private final UuidConverter uuidConverter;

    @Override
    protected StationServiceEntity processDomainConversion(StationService domain) {
        return StationServiceEntity.builder()
            .stationId(uuidConverter.convertDomain(domain.getStationId()))
            .service(domain.getService())
            .build();
    }

    @Override
    protected StationService processEntityConversion(StationServiceEntity entity) {
        return StationService.builder()
            .stationId(uuidConverter.convertEntity(entity.getStationId()))
            .service(entity.getService())
            .build();
    }
}
