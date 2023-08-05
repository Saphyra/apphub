package com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_allocation;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_allocation.BuildingAllocation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class BuildingAllocationFactory {
    private final IdGenerator idGenerator;

    public BuildingAllocation create(UUID buildingId, UUID processId){
        return BuildingAllocation.builder()
            .buildingAllocationId(idGenerator.randomUuid())
            .buildingId(buildingId)
            .processId(processId)
            .build();
    }
}
