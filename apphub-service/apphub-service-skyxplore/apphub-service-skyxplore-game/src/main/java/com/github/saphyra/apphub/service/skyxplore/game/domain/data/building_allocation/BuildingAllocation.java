package com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_allocation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
public class BuildingAllocation { // TODO create model and save to database
    private final UUID buildingAllocationId;
    private final UUID buildingId;
    private final UUID processId;
}
