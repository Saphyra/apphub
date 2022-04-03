package com.github.saphyra.apphub.service.skyxplore.game.domain.map;

import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.Vector;

import static java.util.Objects.isNull;

//TODO unit test
@NoArgsConstructor
public class BuildingAllocations extends HashMap<UUID, List<UUID>> {
    public BuildingAllocations(Map<UUID, List<UUID>> map) {
        putAll(map);
    }

    @Override
    public List<UUID> get(Object key) {
        List<UUID> stored = super.get(key);
        if (isNull(stored)) {
            return new Vector<>();
        }
        return stored;
    }

    public void add(UUID buildingId, UUID processId) {
        List<UUID> allocations = getOrDefault(buildingId, new Vector<>());
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
