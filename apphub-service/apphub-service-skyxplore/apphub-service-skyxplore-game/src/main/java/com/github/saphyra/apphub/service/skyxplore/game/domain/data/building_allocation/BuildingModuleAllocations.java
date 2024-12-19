package com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_allocation;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.Vector;
import java.util.stream.Collectors;

public class BuildingModuleAllocations extends Vector<BuildingModuleAllocation> {
    public Optional<BuildingModuleAllocation> findByProcessId(UUID processId) {
        return stream()
            .filter(buildingAllocation -> buildingAllocation.getProcessId().equals(processId))
            .findAny();
    }

    public List<BuildingModuleAllocation> getByBuildingModuleId(UUID buildingModuleId) {
        return stream()
            .filter(buildingAllocation -> buildingAllocation.getBuildingModuleId().equals(buildingModuleId))
            .collect(Collectors.toList());
    }
}
