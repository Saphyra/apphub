package com.github.saphyra.apphub.service.elite_base.message_processing.dao.station;

import com.github.saphyra.apphub.lib.common_util.DateTimeConverter;
import com.github.saphyra.apphub.lib.common_util.LazyLoadedField;
import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.service.elite_base.message_processing.dao.station.station_economy.StationEconomyDao;
import com.github.saphyra.apphub.service.elite_base.message_processing.dao.station.station_economy.StationEconomySyncService;
import com.github.saphyra.apphub.service.elite_base.message_processing.dao.station.station_service.StationService;
import com.github.saphyra.apphub.service.elite_base.message_processing.dao.station.station_service.StationServiceDao;
import com.github.saphyra.apphub.service.elite_base.message_processing.dao.station.station_service.StationServiceSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class StationConverter extends ConverterBase<StationEntity, Station> {
    private final UuidConverter uuidConverter;
    private final DateTimeConverter dateTimeConverter;
    private final StationEconomySyncService stationEconomySyncService;
    private final StationServiceSyncService stationServiceSyncService;
    private final StationEconomyDao stationEconomyDao;
    private final StationServiceDao stationServiceDao;

    @Override
    protected StationEntity processDomainConversion(Station domain) {
        stationServiceSyncService.sync(domain.getId(), domain.getServices());
        stationEconomySyncService.sync(domain.getId(), domain.getEconomies());

        return StationEntity.builder()
            .id(uuidConverter.convertDomain(domain.getId()))
            .lastUpdate(dateTimeConverter.convertDomain(domain.getLastUpdate()))
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
            .lastUpdate(dateTimeConverter.convertToLocalDateTime(entity.getLastUpdate()))
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
