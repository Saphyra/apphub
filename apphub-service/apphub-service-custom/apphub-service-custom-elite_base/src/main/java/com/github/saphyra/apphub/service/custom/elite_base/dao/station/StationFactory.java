package com.github.saphyra.apphub.service.custom.elite_base.dao.station;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.common_util.LazyLoadedField;
import com.github.saphyra.apphub.service.custom.elite_base.dao.Allegiance;
import com.github.saphyra.apphub.service.custom.elite_base.dao.EconomyEnum;
import com.github.saphyra.apphub.service.custom.elite_base.dao.StationType;
import com.github.saphyra.apphub.service.custom.elite_base.dao.station.station_economy.StationEconomy;
import com.github.saphyra.apphub.service.custom.elite_base.dao.station.station_economy.StationEconomyFactory;
import com.github.saphyra.apphub.service.custom.elite_base.dao.station.station_service.StationServiceEnum;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.structure.Economy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class StationFactory {
    private final IdGenerator idGenerator;
    private final StationEconomyFactory stationEconomyFactory;

    public Station create(
        LocalDateTime timestamp,
        UUID starSystemId,
        UUID bodyId,
        String stationName,
        StationType stationType,
        Long marketId,
        Allegiance allegiance,
        EconomyEnum economy,
        List<StationServiceEnum> services,
        List<Economy> economies,
        UUID controllingFactionId
    ) {
        UUID stationId = idGenerator.randomUuid();

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
            .economies(LazyLoadedField.loaded(parseEconomies(stationId, economies)))
            .build();
    }

    private List<StationEconomy> parseEconomies(UUID stationId, List<Economy> economies) {
        return Optional.ofNullable(economies)
            .map(es -> es.stream().map(e -> stationEconomyFactory.create(stationId, e)).toList())
            .orElse(null);
    }
}
