package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.loader.impl;

import com.github.saphyra.apphub.api.skyxplore.model.game.BuildingModuleAllocationModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_module_allocation.BuildingModuleAllocation;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.GameItemLoader;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BuildingModuleAllocationLoader extends AutoLoader<BuildingModuleAllocationModel, BuildingModuleAllocation> {
    public BuildingModuleAllocationLoader(GameItemLoader gameItemLoader) {
        super(gameItemLoader);
    }

    @Override
    protected GameItemType getGameItemType() {
        return GameItemType.BUILDING_MODULE_ALLOCATION;
    }

    @Override
    protected Class<BuildingModuleAllocationModel[]> getArrayClass() {
        return BuildingModuleAllocationModel[].class;
    }

    @Override
    protected void addToGameData(GameData gameData, List<BuildingModuleAllocation> items) {
        gameData.getBuildingModuleAllocations()
            .addAll(items);
    }

    @Override
    protected BuildingModuleAllocation convert(BuildingModuleAllocationModel model) {
        return BuildingModuleAllocation.builder()
            .buildingModuleAllocationId(model.getId())
            .buildingModuleId(model.getBuildingModuleId())
            .processId(model.getProcessId())
            .build();
    }
}
