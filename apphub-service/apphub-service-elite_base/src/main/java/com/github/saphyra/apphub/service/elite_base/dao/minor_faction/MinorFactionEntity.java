package com.github.saphyra.apphub.service.elite_base.dao.minor_faction;

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
@Table(schema = "elite_base", name = "minor_faction")
class MinorFactionEntity {
    @Id
    private String id;
    private String lastUpdate;
    private String factionName;
    @Enumerated(EnumType.STRING)
    private FactionState state;
}
