package com.github.saphyra.apphub.service.feature.elite_base.dao.star_system.powerplay_conflict;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.SCHEMA;
import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.TABLE_POWERPLAY_CONFLICT;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
@Table(schema = SCHEMA, name = TABLE_POWERPLAY_CONFLICT)
class PowerplayConflictEntity {
    @EmbeddedId
    private PowerplayConflictEntityId id;

    private Double conflictProgress;
}
