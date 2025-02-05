package com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.minor_faction.state;

import com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.FactionStateEnum;
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

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
@Table(schema = "elite_base", name = "minor_faction_state")
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
