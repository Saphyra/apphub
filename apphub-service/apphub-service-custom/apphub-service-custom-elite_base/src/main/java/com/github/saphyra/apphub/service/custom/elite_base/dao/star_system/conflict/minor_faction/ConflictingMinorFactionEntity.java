package com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.conflict.minor_faction;

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
    private String minorFactionId;
    private Integer wonDays;
    private String stake;
}
