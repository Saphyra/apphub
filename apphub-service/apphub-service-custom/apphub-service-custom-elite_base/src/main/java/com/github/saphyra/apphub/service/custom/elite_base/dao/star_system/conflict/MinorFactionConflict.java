package com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.conflict;

import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.conflict.minor_faction.ConflictingMinorFaction;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Collections;
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
    @Builder.Default
    private final List<ConflictingMinorFaction> conflictingMinorFactions = Collections.emptyList();
}
