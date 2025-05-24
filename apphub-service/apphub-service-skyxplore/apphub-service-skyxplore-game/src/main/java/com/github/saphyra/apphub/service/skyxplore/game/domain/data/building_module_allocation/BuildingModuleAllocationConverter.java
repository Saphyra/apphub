package com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_module_allocation;

import com.github.saphyra.apphub.api.skyxplore.model.game.BuildingModuleAllocationModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameDataToModelConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class BuildingModuleAllocationConverter implements GameDataToModelConverter {
    public List<BuildingModuleAllocationModel> toModel(UUID gameId, Collection<BuildingModuleAllocation> buildingModuleAllocations) {
        return buildingModuleAllocations.stream()
            .map(allocation -> toModel(gameId, allocation))
            .collect(Collectors.toList());
    }

    public BuildingModuleAllocationModel toModel(UUID gameId, BuildingModuleAllocation allocation) {
        BuildingModuleAllocationModel model = new BuildingModuleAllocationModel();

        model.setId(allocation.getBuildingModuleAllocationId());
        model.setGameId(gameId);
        model.setType(GameItemType.BUILDING_MODULE_ALLOCATION);

        model.setBuildingModuleId(allocation.getBuildingModuleId());
        model.setProcessId(allocation.getProcessId());

        return model;
    }

    @Override
    public List<BuildingModuleAllocationModel> convert(UUID gameId, GameData gameData) {
        return toModel(gameId, gameData.getBuildingModuleAllocations());
    }
}
