package com.github.saphyra.apphub.service.elite_base.message_processing.dao.station.station_economy;

import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Slf4j
//TODO unit test
class StationEconomyConverter extends ConverterBase<StationEconomyEntity, StationEconomy> {
    private final UuidConverter uuidConverter;

    @Override
    protected StationEconomyEntity processDomainConversion(StationEconomy domain) {
        return StationEconomyEntity.builder()
            .id(uuidConverter.convertDomain(domain.getId()))
            .stationId(uuidConverter.convertDomain(domain.getStationId()))
            .economy(domain.getEconomy())
            .proportion(domain.getProportion())
            .build();
    }

    @Override
    protected StationEconomy processEntityConversion(StationEconomyEntity entity) {
        return StationEconomy.builder()
            .id(uuidConverter.convertEntity(entity.getId()))
            .stationId(uuidConverter.convertEntity(entity.getStationId()))
            .economy(entity.getEconomy())
            .proportion(entity.getProportion())
            .build();
    }
}
