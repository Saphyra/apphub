package com.github.saphyra.apphub.service.custom.elite_base.message_processing.saver;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.common_util.LazyLoadedField;
import com.github.saphyra.apphub.service.custom.elite_base.dao.Allegiance;
import com.github.saphyra.apphub.service.custom.elite_base.dao.EconomyEnum;
import com.github.saphyra.apphub.service.custom.elite_base.dao.FactionStateEnum;
import com.github.saphyra.apphub.service.custom.elite_base.dao.StationType;
import com.github.saphyra.apphub.service.custom.elite_base.dao.minor_faction.MinorFaction;
import com.github.saphyra.apphub.service.custom.elite_base.dao.station.Station;
import com.github.saphyra.apphub.service.custom.elite_base.dao.station.StationDao;
import com.github.saphyra.apphub.service.custom.elite_base.dao.station.StationFactory;
import com.github.saphyra.apphub.service.custom.elite_base.dao.station.station_economy.StationEconomy;
import com.github.saphyra.apphub.service.custom.elite_base.dao.station.station_economy.StationEconomyFactory;
import com.github.saphyra.apphub.service.custom.elite_base.dao.station.station_service.StationServiceEnum;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.structure.Economy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
public class StationSaver {
    private final StationDao stationDao;
    private final StationFactory stationFactory;
    private final StationEconomyFactory stationEconomyFactory;
    private final IdGenerator idGenerator;
    private final MinorFactionSaver minorFactionSaver;

    public void save(LocalDateTime timestamp, UUID starSystemId, UUID bodyId, String stationName, Long marketId, Allegiance allegiance, EconomyEnum economy, String[] stationServices, Economy[] economies) {
        save(timestamp, starSystemId, bodyId, stationName, null, marketId, allegiance, economy, stationServices, economies, null, null);
    }

    public synchronized Station save(
        LocalDateTime timestamp,
        UUID starSystemId,
        UUID bodyId,
        String stationName,
        StationType stationType,
        Long marketId,
        Allegiance allegiance,
        EconomyEnum economy,
        String[] stationServices,
        Economy[] economies,
        String controllingFactionName,
        FactionStateEnum controllingFactionState
    ) {
        if (isNull(marketId)) {
            throw new IllegalArgumentException("marketId must not be null.");
        }

        if (EconomyEnum.CARRIER == economy || StationType.FLEET_CARRIER == stationType) {
            throw new IllegalArgumentException("Carrier must not be saved as station");
        }

        log.info("Saving station {}", stationName);

        List<StationServiceEnum> parsedServices = Optional.ofNullable(stationServices)
            .map(ss -> Arrays.stream(ss).map(StationServiceEnum::parse).toList())
            .orElse(null);
        UUID stationId = idGenerator.randomUuid();
        List<StationEconomy> parsedEconomies = Optional.ofNullable(economies)
            .map(es -> Arrays.stream(es).map(e -> stationEconomyFactory.create(stationId, e)).toList())
            .orElse(null);
        UUID controllingFactionId = Optional.ofNullable(controllingFactionName)
            .map(s -> minorFactionSaver.save(timestamp, controllingFactionName, controllingFactionState))
            .map(MinorFaction::getId)
            .orElse(null);

        Station station = stationDao.findByMarketId(marketId)
            .orElseGet(() -> {
                Station created = stationFactory.create(
                    stationId,
                    timestamp,
                    starSystemId,
                    bodyId,
                    stationName,
                    stationType,
                    marketId,
                    allegiance,
                    economy,
                    parsedServices,
                    parsedEconomies,
                    controllingFactionId
                );
                log.debug("Saving new {}", created);
                stationDao.save(created);
                return created;
            });

        updateFields(timestamp, station, bodyId, allegiance, economy, parsedServices, parsedEconomies, stationType, controllingFactionId, marketId, starSystemId);

        log.debug("Saved station {}", stationName);

        return station;
    }

    private void updateFields(
        LocalDateTime timestamp,
        Station station,
        UUID bodyId,
        Allegiance allegiance,
        EconomyEnum economy,
        List<StationServiceEnum> stationServices,
        List<StationEconomy> economies,
        StationType stationType,
        UUID controllingFactionId,
        Long marketId,
        UUID starSystemId
    ) {
        if (timestamp.isBefore(station.getLastUpdate())) {
            log.debug("Station {} has newer data than {}", station.getId(), timestamp);
            return;
        }

        List.of(
                new UpdateHelper(starSystemId, station::getStarSystemId, () -> station.setStarSystemId(starSystemId)),
                new UpdateHelper(marketId, station::getMarketId, () -> station.setMarketId(marketId)),
                new UpdateHelper(timestamp, station::getLastUpdate, () -> station.setLastUpdate(timestamp)),
                new UpdateHelper(bodyId, station::getBodyId, () -> station.setBodyId(bodyId)),
                new UpdateHelper(allegiance, station::getAllegiance, () -> station.setAllegiance(allegiance)),
                new UpdateHelper(economy, station::getEconomy, () -> station.setEconomy(economy)),
                new UpdateHelper(stationServices, station::getServices, () -> station.setServices(LazyLoadedField.loaded(stationServices))),
                new UpdateHelper(economies, station::getEconomies, () -> station.setEconomies(LazyLoadedField.loaded(economies))),
                new UpdateHelper(stationType, station::getType, () -> station.setType(stationType)),
                new UpdateHelper(controllingFactionId, station::getControllingFactionId, () -> station.setControllingFactionId(controllingFactionId))
            )
            .forEach(UpdateHelper::modify);

        stationDao.save(station);
    }
}
