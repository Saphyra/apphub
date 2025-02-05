package com.github.saphyra.apphub.service.custom.elite_base.message_processing.util;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.minor_faction.MinorFaction;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.star_system_data.conflict.MinorFactionConflict;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.star_system_data.conflict.WarStatus;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.star_system_data.conflict.WarType;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.star_system_data.conflict.minor_faction.ConflictingMinorFactionFactory;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.structure.journal.EdConflict;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
public class ConflictMapper {
    private final IdGenerator idGenerator;
    private final ConflictingMinorFactionFactory conflictingMinorFactionFactory;

    public List<MinorFactionConflict> mapConflicts(LocalDateTime timestamp, UUID starSystemId, EdConflict[] conflictsArr, List<MinorFaction> minorFactions) {
        if (isNull(conflictsArr)) {
            return null;
        }

        return Arrays.stream(conflictsArr)
            .map(edConflict -> mapConflict(timestamp, starSystemId, edConflict, minorFactions))
            .toList();
    }

    private MinorFactionConflict mapConflict(LocalDateTime timestamp, UUID starSystemId, EdConflict edConflict, List<MinorFaction> minorFactions) {
        UUID conflictId = idGenerator.randomUuid();
        return MinorFactionConflict.builder()
            .id(conflictId)
            .starSystemId(starSystemId)
            .status(WarStatus.parse(edConflict.getStatus()))
            .warType(WarType.parse(edConflict.getWarType()))
            .conflictingMinorFactions(List.of(
                conflictingMinorFactionFactory.create(timestamp, conflictId, edConflict.getFaction1(), minorFactions),
                conflictingMinorFactionFactory.create(timestamp, conflictId, edConflict.getFaction2(), minorFactions)
            ))
            .build();
    }
}
