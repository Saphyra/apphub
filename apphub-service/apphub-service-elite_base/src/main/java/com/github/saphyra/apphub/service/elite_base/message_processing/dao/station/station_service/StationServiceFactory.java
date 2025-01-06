package com.github.saphyra.apphub.service.elite_base.message_processing.dao.station.station_service;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class StationServiceFactory {
    private final IdGenerator idGenerator;

    StationService create(UUID stationId, StationServiceEnum service) {
        return StationService.builder()
            .id(idGenerator.randomUuid())
            .stationId(stationId)
            .service(service)
            .build();
    }
}
