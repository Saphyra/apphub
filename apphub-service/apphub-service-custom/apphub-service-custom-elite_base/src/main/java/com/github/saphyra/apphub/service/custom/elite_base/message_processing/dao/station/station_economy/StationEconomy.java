package com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.station.station_economy;

import com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.EconomyEnum;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Data
public class StationEconomy {
    private UUID stationId;
    private EconomyEnum economy;
    private Double proportion;
}
