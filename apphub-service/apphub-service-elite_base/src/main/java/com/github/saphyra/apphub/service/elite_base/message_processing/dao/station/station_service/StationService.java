package com.github.saphyra.apphub.service.elite_base.message_processing.dao.station.station_service;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Data
public class StationService {
    private final UUID stationId;
    private StationServiceEnum service;
}
