package com.github.saphyra.apphub.service.elite_base.message_processing.dao.star_system_data.conflict;

import com.github.saphyra.apphub.service.elite_base.message_processing.dao.star_system_data.conflict.minor_faction.ConflictingMinorFaction;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Data
@Builder
@EqualsAndHashCode(exclude = "id")
public class MinorFactionConflict {
    private final UUID id;
    private final UUID starSystemId;
    private final WarStatus status;
    private final WarType warType;
    private final List<ConflictingMinorFaction> conflictingMinorFactions;
}
