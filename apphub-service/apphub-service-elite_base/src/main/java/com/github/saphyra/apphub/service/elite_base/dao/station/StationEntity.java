package com.github.saphyra.apphub.service.elite_base.dao.station;

import com.github.saphyra.apphub.service.elite_base.dao.Allegiance;
import com.github.saphyra.apphub.service.elite_base.dao.StationType;
import com.github.saphyra.apphub.service.elite_base.dao.station.station_economy.StationEconomyEnum;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
@Table(schema = "elite_base", name = "station")
class StationEntity {
    @Id
    private String id;
    private String lastUpdate;
    private String starSystemId;
    private String bodyId;
    private String stationName;
    @Enumerated(EnumType.STRING)
    private StationType type;
    private Long marketId;
    @Enumerated(EnumType.STRING)
    private Allegiance allegiance;
    @Enumerated(EnumType.STRING)
    private StationEconomyEnum economy;
}
