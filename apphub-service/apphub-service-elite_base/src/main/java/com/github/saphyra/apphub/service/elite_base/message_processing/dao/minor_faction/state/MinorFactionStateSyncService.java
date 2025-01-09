package com.github.saphyra.apphub.service.elite_base.message_processing.dao.minor_faction.state;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
@Slf4j
public class MinorFactionStateSyncService {
    private final MinorFactionStateDao minorFactionStateDao;

    public void sync(UUID minorFactionId, List<MinorFactionState> activeStates, List<MinorFactionState> pendingStates, List<MinorFactionState> recoveringStates) {
        List<MinorFactionState> newStates = Stream.of(activeStates, pendingStates, recoveringStates)
            .flatMap(Collection::stream)
            .toList();
        List<MinorFactionState> existingStates = minorFactionStateDao.getByMinorFactionId(minorFactionId);

        List<MinorFactionState> toDelete = existingStates.stream()
            .filter(minorFactionState -> !newStates.contains(minorFactionState))
            .toList();
        List<MinorFactionState> toSave = newStates.stream()
            .filter(minorFactionState -> !existingStates.contains(minorFactionState))
            .toList();

        minorFactionStateDao.deleteAll(toDelete);
        minorFactionStateDao.saveAll(toSave);
    }
}
