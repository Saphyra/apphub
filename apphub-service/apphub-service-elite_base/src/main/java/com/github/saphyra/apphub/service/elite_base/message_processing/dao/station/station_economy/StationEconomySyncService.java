package com.github.saphyra.apphub.service.elite_base.message_processing.dao.station.station_economy;

import com.github.saphyra.apphub.service.elite_base.message_processing.dao.EconomyEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class StationEconomySyncService {
    private final StationEconomyDao stationEconomyDao;

    public void sync(UUID stationId, List<StationEconomy> economies) {
        if (isNull(economies)) {
            return;
        }

        List<StationEconomy> existingEconomies = stationEconomyDao.getByStationId(stationId);
        Map<EconomyEnum, Double> existingEconomyMapping = existingEconomies.stream()
            .collect(Collectors.toMap(StationEconomy::getEconomy, StationEconomy::getProportion, Double::sum));
        Map<EconomyEnum, Double> newEconomyMapping = economies.stream()
            .collect(Collectors.toMap(StationEconomy::getEconomy, StationEconomy::getProportion, Double::sum));

        List<StationEconomy> toSave = economies.stream()
            .filter(stationEconomy -> !Objects.equals(existingEconomyMapping.get(stationEconomy.getEconomy()), stationEconomy.getProportion()))
            .toList();
        List<StationEconomy> toDelete = existingEconomies.stream()
            .filter(stationEconomy -> !Objects.equals(newEconomyMapping.get(stationEconomy.getEconomy()), stationEconomy.getProportion()))
            .toList();

        stationEconomyDao.saveAll(toSave);
        stationEconomyDao.deleteAll(toDelete);
    }
}
