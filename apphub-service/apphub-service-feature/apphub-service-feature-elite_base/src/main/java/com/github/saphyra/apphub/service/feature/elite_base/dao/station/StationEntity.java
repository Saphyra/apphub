package com.github.saphyra.apphub.service.feature.elite_base.dao.station;

import com.github.saphyra.apphub.service.feature.elite_base.dao.Allegiance;
import com.github.saphyra.apphub.service.feature.elite_base.dao.EconomyEnum;
import com.github.saphyra.apphub.service.feature.elite_base.dao.StationType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.SCHEMA;
import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.TABLE_STATION;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
@Table(schema = SCHEMA, name = TABLE_STATION)
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
    private EconomyEnum economy;
    private String controllingFactionId;
}
