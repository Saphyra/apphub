package com.github.saphyra.apphub.service.feature.elite_base.dao.minor_faction.state;

import com.github.saphyra.apphub.service.feature.elite_base.dao.FactionStateEnum;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.SCHEMA;
import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.TABLE_MINOR_FACTION_STATE;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
@Table(schema = SCHEMA, name = TABLE_MINOR_FACTION_STATE)
@IdClass(MinorFactionStateEntityId.class)
class MinorFactionStateEntity {
    @Id
    private String minorFactionId;
    @Id
    @Enumerated(EnumType.STRING)
    private StateStatus status;
    @Id
    @Enumerated(EnumType.STRING)
    private FactionStateEnum state;
    private Integer trend;
}
