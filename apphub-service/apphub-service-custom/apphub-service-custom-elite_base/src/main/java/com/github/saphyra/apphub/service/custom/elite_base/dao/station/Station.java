package com.github.saphyra.apphub.service.custom.elite_base.dao.station;

import com.github.saphyra.apphub.lib.common_util.LazyLoadedField;
import com.github.saphyra.apphub.service.custom.elite_base.dao.Allegiance;
import com.github.saphyra.apphub.service.custom.elite_base.dao.EconomyEnum;
import com.github.saphyra.apphub.service.custom.elite_base.dao.station.station_economy.StationEconomy;
import com.github.saphyra.apphub.service.custom.elite_base.dao.station.station_service.StationServiceEnum;
import com.github.saphyra.apphub.service.custom.elite_base.dao.StationType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Data
@Builder
@ToString(exclude = {"services", "economies"})
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
    @Builder.Default
    private LazyLoadedField<List<StationServiceEnum>> services = LazyLoadedField.loaded(List.of());
    @Builder.Default
    private LazyLoadedField<List<StationEconomy>> economies = LazyLoadedField.loaded(List.of());

    public List<StationServiceEnum> getServices() {
        return services.get();
    }

    public List<StationEconomy> getEconomies() {
        return economies.get();
    }
}
