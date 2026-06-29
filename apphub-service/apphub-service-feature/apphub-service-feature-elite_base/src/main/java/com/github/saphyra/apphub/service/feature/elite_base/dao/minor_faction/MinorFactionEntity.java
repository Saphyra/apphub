package com.github.saphyra.apphub.service.feature.elite_base.dao.minor_faction;

import com.github.saphyra.apphub.service.feature.elite_base.dao.Allegiance;
import com.github.saphyra.apphub.service.feature.elite_base.dao.FactionStateEnum;
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
import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.TABLE_MINOR_FACTION;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
@Table(schema = SCHEMA, name = TABLE_MINOR_FACTION)
class MinorFactionEntity {
    @Id
    private String id;
    private String lastUpdate;
    private String factionName;
    @Enumerated(EnumType.STRING)
    private FactionStateEnum state;
    private Double influence;
    @Enumerated(EnumType.STRING)
    private Allegiance allegiance;
}
