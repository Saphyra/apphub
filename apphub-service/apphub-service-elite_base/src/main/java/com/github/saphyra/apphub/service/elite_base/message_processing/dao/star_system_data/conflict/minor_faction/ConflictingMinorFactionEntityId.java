package com.github.saphyra.apphub.service.elite_base.message_processing.dao.star_system_data.conflict.minor_faction;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
class ConflictingMinorFactionEntityId implements Serializable {
    private String conflictId;
    private String factionId;
}
