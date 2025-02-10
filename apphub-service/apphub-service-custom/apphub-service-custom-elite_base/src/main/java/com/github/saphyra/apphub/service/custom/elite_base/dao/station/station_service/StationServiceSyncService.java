package com.github.saphyra.apphub.service.custom.elite_base.dao.station.station_service;

import jakarta.transaction.Transactional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
public class StationServiceSyncService {
    private final StationServiceDao stationServiceDao;
    private final StationServiceFactory stationServiceFactory;

    @Transactional
    public void sync(@NonNull UUID stationId, List<StationServiceEnum> services) {
        if (isNull(services)) {
            return;
        }

        List<StationService> newServices = services.stream()
            .map(stationServiceEnum -> stationServiceFactory.create(stationId, stationServiceEnum))
            .toList();
        List<StationService> existingServices = stationServiceDao.getByStationId(stationId);

        List<StationService> toCreate = newServices.stream()
            .filter(stationService -> !existingServices.contains(stationService))
            .toList();
        List<StationService> toDelete = existingServices.stream()
            .filter(stationService -> !newServices.contains(stationService))
            .toList();

        stationServiceDao.saveAll(toCreate);
        stationServiceDao.deleteAll(toDelete);
    }
}
