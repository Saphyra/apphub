package com.github.saphyra.apphub.service.skyxplore.game.domain.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

//TODO unit test
public class BuildingAllocations extends HashMap<UUID, List<UUID>> {
    public void add(UUID buildingId, UUID processId) {
        List<UUID> allocations = getOrDefault(buildingId, new ArrayList<>());
        allocations.add(processId);
        put(buildingId, allocations);
    }

    public Optional<UUID> findByProcessId(UUID processId) {
        return entrySet()
            .stream()
            .filter(entry -> entry.getValue().contains(processId))
            .map(Entry::getKey)
            .findFirst();
    }

    public void releaseByProcessId(UUID processId) {
        values()
            .forEach(processIds -> processIds.remove(processId));
    }
}
