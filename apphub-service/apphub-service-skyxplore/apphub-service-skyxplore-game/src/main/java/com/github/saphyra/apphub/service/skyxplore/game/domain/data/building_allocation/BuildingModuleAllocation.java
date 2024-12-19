package com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_allocation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
public class BuildingModuleAllocation {
    private final UUID buildingAllocationId;
    private final UUID buildingModuleId;
    private final UUID processId;
}
