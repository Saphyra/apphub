package com.github.saphyra.apphub.service.custom.elite_base.dao.star_system_data.powerplay_conflict;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class PowerplayConflictSyncService {
    private final PowerplayConflictDao powerplayConflictDao;

    public void sync(UUID starSystemId, List<PowerplayConflict> powerplayConflicts) {
        List<PowerplayConflict> existingConflicts = powerplayConflictDao.getByStarSystemId(starSystemId);

        List<PowerplayConflict> toDelete = existingConflicts.stream()
            .filter(powerplayConflict -> !powerplayConflicts.contains(powerplayConflict))
            .toList();
        List<PowerplayConflict> toSave = powerplayConflicts.stream()
            .filter(powerplayConflict -> !existingConflicts.contains(powerplayConflict))
            .toList();

        powerplayConflictDao.deleteAll(toDelete);
        powerplayConflictDao.saveAll(toSave);
    }
}
