package com.github.saphyra.apphub.service.elite_base.message_processing.saver;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.common_util.LazyLoadedField;
import com.github.saphyra.apphub.service.elite_base.dao.Allegiance;
import com.github.saphyra.apphub.service.elite_base.dao.station.Station;
import com.github.saphyra.apphub.service.elite_base.dao.station.StationDao;
import com.github.saphyra.apphub.service.elite_base.dao.station.StationFactory;
import com.github.saphyra.apphub.service.elite_base.dao.StationType;
import com.github.saphyra.apphub.service.elite_base.dao.station.station_economy.StationEconomy;
import com.github.saphyra.apphub.service.elite_base.dao.station.station_economy.StationEconomyEnum;
import com.github.saphyra.apphub.service.elite_base.dao.station.station_economy.StationEconomyFactory;
import com.github.saphyra.apphub.service.elite_base.dao.station.station_service.StationServiceEnum;
import com.github.saphyra.apphub.service.elite_base.message_processing.structure.Economy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static io.micrometer.common.util.StringUtils.isBlank;
import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unite test
public class StationSaver {
    private final StationDao stationDao;
    private final StationFactory stationFactory;
    private final StationEconomyFactory stationEconomyFactory;
    private final IdGenerator idGenerator;

    public Station save(LocalDateTime timestamp, UUID starSystemId, String stationName, StationType stationType, Long marketId, Economy[] economies) {
        return save(timestamp, starSystemId, null, stationName, stationType, marketId, null, null, new String[0], economies);
    }

    public synchronized Station save(
        LocalDateTime timestamp,
        UUID starSystemId,
        UUID bodyId,
        String stationName,
        StationType stationType,
        Long marketId,
        Allegiance allegiance,
        StationEconomyEnum economy,
        String[] stationServices,
        Economy[] economies
    ) {
        if (isNull(starSystemId) || isBlank(stationName)) {
            throw new RuntimeException("StarSystemId and Station name must not be blank.");
        }

        List<StationServiceEnum> parsedServices = Arrays.stream(stationServices)
            .map(StationServiceEnum::parse)
            .toList();
        UUID stationId = idGenerator.randomUuid();
        List<StationEconomy> parsedEconomies = Arrays.stream(economies)
            .map(e -> stationEconomyFactory.create(stationId, e))
            .toList();

        Station station = stationDao.findByStarSystemIdAndStationName(starSystemId, stationName)
            .orElseGet(() -> {
                Station created = stationFactory.create(stationId, timestamp, starSystemId, bodyId, stationName, stationType, marketId, allegiance, economy, parsedServices, parsedEconomies);
                log.info("Saving new {}", created);
                stationDao.save(created);
                return created;
            });

        updateFields(timestamp, station, bodyId, allegiance, economy, parsedServices, parsedEconomies);

        return station;
    }

    private void updateFields(LocalDateTime timestamp, Station station, UUID bodyId, Allegiance allegiance, StationEconomyEnum economy, List<StationServiceEnum> stationServices, List<StationEconomy> economies) {
        if (timestamp.isBefore(station.getLastUpdate())) {
            log.debug("Station {} has newer data than {}", station.getId(), timestamp);
            return;
        }

        List.of(
                new UpdateHelper(timestamp, station::getLastUpdate, () -> station.setLastUpdate(timestamp)),
                new UpdateHelper(bodyId, station::getBodyId, () -> station.setBodyId(bodyId)),
                new UpdateHelper(allegiance, station::getAllegiance, () -> station.setAllegiance(allegiance)),
                new UpdateHelper(economy, station::getEconomy, () -> station.setEconomy(economy)),
                new UpdateHelper(stationServices, station::getServices, () -> station.setServices(LazyLoadedField.loaded(stationServices))),
                new UpdateHelper(economies, station::getEconomies, () -> station.setEconomies(LazyLoadedField.loaded(economies)))
            )
            .forEach(UpdateHelper::modify);

        stationDao.save(station);
    }
}
