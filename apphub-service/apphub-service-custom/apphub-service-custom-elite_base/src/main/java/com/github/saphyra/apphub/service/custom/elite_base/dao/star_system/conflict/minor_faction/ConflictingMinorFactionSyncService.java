package com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.conflict.minor_faction;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ConflictingMinorFactionSyncService {
    private final ConflictingMinorFactionDao conflictingMinorFactionDao;

    public void sync(UUID conflictId, List<ConflictingMinorFaction> newFactions) {
        List<ConflictingMinorFaction> existingFactions = conflictingMinorFactionDao.getByConflictId(conflictId);

        List<ConflictingMinorFaction> toDelete = existingFactions.stream()
            .filter(conflictingMinorFaction -> !newFactions.contains(conflictingMinorFaction))
            .toList();
        List<ConflictingMinorFaction> toSave = newFactions.stream()
            .filter(conflictingMinorFaction -> !existingFactions.contains(conflictingMinorFaction))
            .toList();

        conflictingMinorFactionDao.deleteAll(toDelete);
        conflictingMinorFactionDao.saveAll(toSave);
    }
}
