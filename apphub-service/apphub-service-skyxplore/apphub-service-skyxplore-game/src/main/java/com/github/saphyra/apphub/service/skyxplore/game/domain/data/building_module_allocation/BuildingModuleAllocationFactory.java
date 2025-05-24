package com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_module_allocation;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.skyxplore.game.domain.GameProgressDiff;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class BuildingModuleAllocationFactory {
    private final IdGenerator idGenerator;
    private final BuildingModuleAllocationConverter buildingModuleAllocationConverter;

    public BuildingModuleAllocation create(UUID buildingModuleId, UUID processId) {
        return BuildingModuleAllocation.builder()
            .buildingModuleAllocationId(idGenerator.randomUuid())
            .buildingModuleId(buildingModuleId)
            .processId(processId)
            .build();
    }

    //TODO unit test
    public void save(GameProgressDiff progressDiff, GameData gameData, UUID buildingModuleId, UUID processId) {
        BuildingModuleAllocation buildingModuleAllocation = create(buildingModuleId, processId);

        gameData.getBuildingModuleAllocations()
            .add(buildingModuleAllocation);

        progressDiff.save(buildingModuleAllocationConverter.toModel(gameData.getGameId(), buildingModuleAllocation));
    }
}
