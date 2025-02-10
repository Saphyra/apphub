package com.github.saphyra.apphub.service.custom.elite_base.dao.station;

import com.github.saphyra.apphub.lib.common_util.LazyLoadedField;
import com.github.saphyra.apphub.service.custom.elite_base.dao.Allegiance;
import com.github.saphyra.apphub.service.custom.elite_base.dao.EconomyEnum;
import com.github.saphyra.apphub.service.custom.elite_base.dao.station.station_economy.StationEconomy;
import com.github.saphyra.apphub.service.custom.elite_base.dao.station.station_service.StationServiceEnum;
import com.github.saphyra.apphub.service.custom.elite_base.dao.StationType;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class StationFactory {
    public Station create(
        @NonNull UUID stationId,
        LocalDateTime timestamp,
        UUID starSystemId,
        UUID bodyId,
        String stationName,
        StationType stationType,
        Long marketId,
        Allegiance allegiance,
        EconomyEnum economy,
        List<StationServiceEnum> services,
        List<StationEconomy> economies,
        UUID controllingFactionId
    ) {
        return Station.builder()
            .id(stationId)
            .lastUpdate(timestamp)
            .starSystemId(starSystemId)
            .bodyId(bodyId)
            .stationName(stationName)
            .type(stationType)
            .marketId(marketId)
            .allegiance(allegiance)
            .economy(economy)
            .controllingFactionId(controllingFactionId)
            .services(LazyLoadedField.loaded(services))
            .economies(LazyLoadedField.loaded(economies))
            .build();
    }
}
