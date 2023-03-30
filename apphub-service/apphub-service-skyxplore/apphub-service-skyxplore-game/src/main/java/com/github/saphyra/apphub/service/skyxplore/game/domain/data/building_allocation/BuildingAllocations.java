package com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_allocation;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.Vector;
import java.util.stream.Collectors;

public class BuildingAllocations extends Vector<BuildingAllocation> {
    public Optional<BuildingAllocation> findByProcessId(UUID processId) {
        return stream()
            .filter(buildingAllocation -> buildingAllocation.getProcessId().equals(processId))
            .findAny();
    }

    public void deleteByProcessId(UUID processId) {
        removeIf(buildingAllocation -> buildingAllocation.getProcessId().equals(processId));
    }

    public List<BuildingAllocation> getByBuildingId(UUID buildingId) {
        return stream()
            .filter(buildingAllocation -> buildingAllocation.getBuildingId().equals(buildingId))
            .collect(Collectors.toList());
    }
}
