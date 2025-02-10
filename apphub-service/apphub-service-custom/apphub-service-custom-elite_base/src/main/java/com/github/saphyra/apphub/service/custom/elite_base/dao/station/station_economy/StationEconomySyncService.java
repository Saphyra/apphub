package com.github.saphyra.apphub.service.custom.elite_base.dao.station.station_economy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
public class StationEconomySyncService {
    private final StationEconomyDao stationEconomyDao;

    public void sync(UUID stationId, List<StationEconomy> economies) {
        if (isNull(economies)) {
            return;
        }

        List<StationEconomy> existingEconomies = stationEconomyDao.getByStationId(stationId);

        List<StationEconomy> toSave = economies.stream()
            .filter(stationEconomy -> !existingEconomies.contains(stationEconomy))
            .toList();
        List<StationEconomy> toDelete = existingEconomies.stream()
            .filter(stationEconomy -> !economies.contains(stationEconomy))
            .toList();

        stationEconomyDao.saveAll(toSave);
        stationEconomyDao.deleteAll(toDelete);
    }
}
