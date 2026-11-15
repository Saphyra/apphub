package com.github.saphyra.apphub.service.feature.elite_base.processor.dao.station;

import com.github.saphyra.apphub.lib.common_util.LazyLoadedField;
import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.service.feature.elite_base.processor.dao.station.station_economy.StationEconomyDao;
import com.github.saphyra.apphub.service.feature.elite_base.processor.dao.station.station_service.StationService;
import com.github.saphyra.apphub.service.feature.elite_base.processor.dao.station.station_service.StationServiceDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
class StationConverter extends ConverterBase<StationEntity, Station> {
    private final UuidConverter uuidConverter;
    private final StationEconomyDao stationEconomyDao;
    private final StationServiceDao stationServiceDao;

    @Override
    protected StationEntity processDomainConversion(Station domain) {

        return StationEntity.builder()
            .id(uuidConverter.convertDomain(domain.getId()))
            .starSystemId(uuidConverter.convertDomain(domain.getStarSystemId()))
            .bodyId(uuidConverter.convertDomain(domain.getBodyId()))
            .stationName(domain.getStationName())
            .type(domain.getType())
            .marketId(domain.getMarketId())
            .allegiance(domain.getAllegiance())
            .economy(domain.getEconomy())
            .controllingFactionId(uuidConverter.convertDomain(domain.getControllingFactionId()))
            .build();
    }

    @Override
    protected Station processEntityConversion(StationEntity entity) {
        UUID stationId = uuidConverter.convertEntity(entity.getId());
        return Station.builder()
            .id(stationId)
            .starSystemId(uuidConverter.convertEntity(entity.getStarSystemId()))
            .bodyId(uuidConverter.convertEntity(entity.getBodyId()))
            .stationName(entity.getStationName())
            .type(entity.getType())
            .marketId(entity.getMarketId())
            .allegiance(entity.getAllegiance())
            .economy(entity.getEconomy())
            .services(new LazyLoadedField<>(() -> stationServiceDao.getByStationId(stationId).stream().map(StationService::getService).toList()))
            .economies(new LazyLoadedField<>(() -> stationEconomyDao.getByStationId(stationId)))
            .controllingFactionId(uuidConverter.convertEntity(entity.getControllingFactionId()))
            .build();
    }
}
