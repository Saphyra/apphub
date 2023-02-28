package com.github.saphyra.apphub.service.skyxplore.game.domain.map;

import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@NoArgsConstructor
public class CitizenAllocations extends HashMap<UUID, UUID> {
    public CitizenAllocations(Map<UUID, UUID> map) {
        putAll(map);
    }

    public void releaseByProcessId(UUID processId) {
        List<UUID> keys = entrySet()
            .stream()
            .filter(entry -> entry.getValue().equals(processId))
            .map(Entry::getKey)
            .toList();

        keys.forEach(this::remove);
    }

    public Optional<UUID> findByProcessId(UUID processId) {
        return entrySet()
            .stream()
            .filter(entry -> entry.getValue().equals(processId))
            .map(Entry::getKey)
            .findFirst();
    }
}
