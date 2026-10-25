package com.github.saphyra.apphub.service.feature.elite_base.processor.dao.star_system.conflict.minor_faction;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.github.saphyra.apphub.service.feature.elite_base.processor.common.DatabaseConstants.SCHEMA;
import static com.github.saphyra.apphub.service.feature.elite_base.processor.common.DatabaseConstants.TABLE_CONFLICTING_MINOR_FACTION_V2;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
@Table(schema = SCHEMA, name = TABLE_CONFLICTING_MINOR_FACTION_V2)
@IdClass(ConflictingMinorFactionEntityId.class)
class ConflictingMinorFactionEntity {
    @Id
    private String conflictId;
    @Id
    private String minorFactionId;
    private Integer wonDays;
    private String stake;
}
