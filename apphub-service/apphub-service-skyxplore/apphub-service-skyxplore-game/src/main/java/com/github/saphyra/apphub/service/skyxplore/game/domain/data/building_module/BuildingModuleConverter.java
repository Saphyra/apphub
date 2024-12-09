package com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_module;

import com.github.saphyra.apphub.api.skyxplore.model.game.BuildingModuleModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameDataToModelConverter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
//TODO unit test
public class BuildingModuleConverter implements GameDataToModelConverter {
    @Override
    public List<BuildingModuleModel> convert(UUID gameId, GameData gameData) {
        return gameData.getBuildingModules()
            .stream()
            .map(buildingModule -> convert(gameId, buildingModule))
            .toList();
    }

    public BuildingModuleModel convert(UUID gameId, BuildingModule buildingModule) {
        BuildingModuleModel model = new BuildingModuleModel();
        model.setId(buildingModule.getBuildingModuleId());
        model.setGameId(gameId);
        model.setType(GameItemType.BUILDING_MODULE);
        model.setLocation(buildingModule.getLocation());
        model.setConstructionAreaId(buildingModule.getConstructionAreaId());
        model.setDataId(buildingModule.getDataId());
        return model;
    }
}
