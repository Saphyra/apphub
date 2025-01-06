package com.github.saphyra.apphub.service.elite_base.message_processing.dao.star_system_data.conflict.minor_faction;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
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
@Table(schema = "elite_base", name = "conflicting_minor_faction")
@IdClass(ConflictingMinorFactionEntityId.class)
class ConflictingMinorFactionEntity {
    @Id
    private String conflictId;
    @Id
    private String factionId;
    private Integer wonDays;
    private String stake;
}
