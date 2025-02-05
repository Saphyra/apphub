package com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.station.station_economy;

import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Slf4j
class StationEconomyConverter extends ConverterBase<StationEconomyEntity, StationEconomy> {
    private final UuidConverter uuidConverter;

    @Override
    protected StationEconomyEntity processDomainConversion(StationEconomy domain) {
        return StationEconomyEntity.builder()
            .id(StationEconomyEntityId.builder()
                .stationId(uuidConverter.convertDomain(domain.getStationId()))
                .economy(domain.getEconomy())
                .build())
            .proportion(domain.getProportion())
            .build();
    }

    @Override
    protected StationEconomy processEntityConversion(StationEconomyEntity entity) {
        return StationEconomy.builder()
            .stationId(uuidConverter.convertEntity(entity.getId().getStationId()))
            .economy(entity.getId().getEconomy())
            .proportion(entity.getProportion())
            .build();
    }
}
