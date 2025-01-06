package com.github.saphyra.apphub.service.elite_base.dao.station;

import com.github.saphyra.apphub.lib.common_util.LazyLoadedField;
import com.github.saphyra.apphub.service.elite_base.dao.Allegiance;
import com.github.saphyra.apphub.service.elite_base.dao.StationType;
import com.github.saphyra.apphub.service.elite_base.dao.station.station_economy.StationEconomy;
import com.github.saphyra.apphub.service.elite_base.dao.EconomyEnum;
import com.github.saphyra.apphub.service.elite_base.dao.station.station_service.StationServiceEnum;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Data
@Builder
public class Station {
    private final UUID id;
    private LocalDateTime lastUpdate;
    private UUID starSystemId;
    private UUID bodyId;
    private String stationName;
    private StationType type;
    private Long marketId;
    private Allegiance allegiance;
    private EconomyEnum economy;
    private UUID controllingFactionId;
    private LazyLoadedField<List<StationServiceEnum>> services;
    private LazyLoadedField<List<StationEconomy>> economies;

    public List<StationServiceEnum> getServices() {
        return services.get();
    }

    public List<StationEconomy> getEconomies() {
        return economies.get();
    }
}
