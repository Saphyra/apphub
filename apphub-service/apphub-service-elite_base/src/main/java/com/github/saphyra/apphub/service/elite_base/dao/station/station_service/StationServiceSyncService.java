package com.github.saphyra.apphub.service.elite_base.dao.station.station_service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class StationServiceSyncService {
    private final StationServiceDao stationServiceDao;
    private final StationServiceFactory stationServiceFactory;

    @Transactional
    public void sync(UUID stationId, List<StationServiceEnum> services) {
        if (isNull(services)) {
            return;
        }

        List<StationService> existingServices = stationServiceDao.getByStationId(stationId);
        List<StationServiceEnum> existingServicesEnum = existingServices.stream()
            .map(StationService::getService)
            .toList();

        List<StationService> toCreate = services.stream()
            .filter(stationServiceEnum -> !existingServicesEnum.contains(stationServiceEnum))
            .map(stationServiceEnum -> stationServiceFactory.create(stationId, stationServiceEnum))
            .toList();
        List<StationService> toDelete = existingServices.stream()
            .filter(stationService -> !services.contains(stationService.getService()))
            .toList();

        stationServiceDao.saveAll(toCreate);
        stationServiceDao.deleteAll(toDelete);
    }
}
