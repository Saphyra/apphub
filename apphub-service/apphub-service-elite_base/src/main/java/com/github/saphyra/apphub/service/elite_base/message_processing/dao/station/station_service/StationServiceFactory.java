package com.github.saphyra.apphub.service.elite_base.message_processing.dao.station.station_service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class StationServiceFactory {
    StationService create(UUID stationId, StationServiceEnum service) {
        return StationService.builder()
            .stationId(stationId)
            .service(service)
            .build();
    }
}
