package com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_allocation;

import com.github.saphyra.apphub.api.skyxplore.model.game.BuildingAllocationModel;
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
public class BuildingAllocationConverter implements GameDataToModelConverter {
    public List<BuildingAllocationModel> toModel(UUID gameId, Collection<BuildingAllocation> buildingAllocations) {
        return buildingAllocations.stream()
            .map(allocation -> toModel(gameId, allocation))
            .collect(Collectors.toList());
    }

    public BuildingAllocationModel toModel(UUID gameId, BuildingAllocation allocation) {
        BuildingAllocationModel model = new BuildingAllocationModel();

        model.setId(allocation.getBuildingAllocationId());
        model.setGameId(gameId);
        model.setType(GameItemType.BUILDING_ALLOCATION);

        model.setBuildingId(allocation.getBuildingId());
        model.setProcessId(allocation.getProcessId());

        return model;
    }

    @Override
    public List<BuildingAllocationModel> convert(UUID gameId, GameData gameData) {
        return toModel(gameId, gameData.getBuildingAllocations());
    }
}
