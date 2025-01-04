package com.github.saphyra.apphub.service.elite_base.dao.star_system_data.conflict;

import com.github.saphyra.apphub.service.elite_base.dao.star_system_data.conflict.minor_faction.ConflictingMinorFactionDao;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class MinorFactionConflictSyncService {
    private final MinorFactionConflictDao minorFactionConflictDao;
    private final ConflictingMinorFactionDao conflictingMinorFactionDao;

    @Transactional
    public void sync(UUID starSystemId, List<MinorFactionConflict> newConflicts) {
        List<MinorFactionConflict> existingConflicts = minorFactionConflictDao.getByStarSystemId(starSystemId);

        List<UUID> newConflictIds = newConflicts.stream()
            .map(MinorFactionConflict::getId)
            .toList();
        List<UUID> existingConflictIds = existingConflicts.stream()
            .map(MinorFactionConflict::getId)
            .toList();

        List<UUID> toDelete = existingConflictIds.stream()
            .filter(uuid -> !newConflictIds.contains(uuid))
            .toList();

        toDelete.forEach(this::delete);

        minorFactionConflictDao.saveAll(newConflicts);
    }

    private void delete(UUID conflictId) {
        conflictingMinorFactionDao.deleteByConflictId(conflictId);
        minorFactionConflictDao.deleteById(conflictId);
    }
}
