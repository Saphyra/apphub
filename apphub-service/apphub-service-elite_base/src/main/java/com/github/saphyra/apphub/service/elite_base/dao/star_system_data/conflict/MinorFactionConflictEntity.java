package com.github.saphyra.apphub.service.elite_base.dao.star_system_data.conflict;

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
@Table(schema = "elite_base", name = "minor_faction_conflict")
class MinorFactionConflictEntity {
    @Id
    private String id;
    private String starSystemId;
    @Enumerated(EnumType.STRING)
    private WarStatus status;
    @Enumerated(EnumType.STRING)
    private WarType warType;
}
