package com.github.saphyra.apphub.service.elite_base.dao.station.station_economy;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Data
public class StationEconomy {
    private UUID id;
    private UUID stationId;
    private StationEconomyEnum economy;
    private Double proportion;
}
