package com.github.saphyra.apphub.service.feature.elite_base.dao.star_system.conflict;

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
import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.TABLE_MINOR_FACTION_CONFLICT;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
@Table(schema = SCHEMA, name = TABLE_MINOR_FACTION_CONFLICT)
class MinorFactionConflictEntity {
    @Id
    private String id;
    private String starSystemId;
    @Enumerated(EnumType.STRING)
    private WarStatus status;
    @Enumerated(EnumType.STRING)
    private WarType warType;
}
