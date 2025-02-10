package com.github.saphyra.apphub.service.custom.elite_base.dao.station.station_economy;

import com.github.saphyra.apphub.service.custom.elite_base.dao.EconomyEnum;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class StationEconomyEntityId implements Serializable {
    private String stationId;
    @Enumerated(EnumType.STRING)
    private EconomyEnum economy;
}
