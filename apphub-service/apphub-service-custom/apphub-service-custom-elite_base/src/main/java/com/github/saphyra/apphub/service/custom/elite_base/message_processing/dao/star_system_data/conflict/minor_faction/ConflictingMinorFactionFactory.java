package com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.star_system_data.conflict.minor_faction;

import com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.minor_faction.MinorFaction;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.structure.journal.ConflictFaction;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.util.MinorFactionIdResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ConflictingMinorFactionFactory {
    private final MinorFactionIdResolver minorFactionIdResolver;

    public ConflictingMinorFaction create(LocalDateTime timestamp, UUID conflictId, ConflictFaction faction, List<MinorFaction> minorFactions) {
        return ConflictingMinorFaction.builder()
            .conflictId(conflictId)
            .factionId(minorFactionIdResolver.getMinorFactionId(timestamp, faction.getFactionName(), minorFactions))
            .wonDays(faction.getWonDays())
            .stake(faction.getStake())
            .build();
    }
}
