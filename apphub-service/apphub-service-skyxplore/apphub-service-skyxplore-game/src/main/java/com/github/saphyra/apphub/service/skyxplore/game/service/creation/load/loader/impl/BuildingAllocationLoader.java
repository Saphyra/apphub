package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.loader.impl;

import com.github.saphyra.apphub.api.skyxplore.model.game.BuildingAllocationModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_allocation.BuildingAllocation;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.GameItemLoader;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BuildingAllocationLoader extends AutoLoader<BuildingAllocationModel, BuildingAllocation> {
    public BuildingAllocationLoader(GameItemLoader gameItemLoader) {
        super(gameItemLoader);
    }

    @Override
    protected GameItemType getGameItemType() {
        return GameItemType.BUILDING_ALLOCATION;
    }

    @Override
    protected Class<BuildingAllocationModel[]> getArrayClass() {
        return BuildingAllocationModel[].class;
    }

    @Override
    protected void addToGameData(GameData gameData, List<BuildingAllocation> items) {
        gameData.getBuildingAllocations()
            .addAll(items);
    }

    @Override
    protected BuildingAllocation convert(BuildingAllocationModel model) {
        return BuildingAllocation.builder()
            .buildingAllocationId(model.getId())
            .buildingId(model.getBuildingId())
            .processId(model.getProcessId())
            .build();
    }
}
