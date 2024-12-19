package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.loader.impl;

import com.github.saphyra.apphub.api.skyxplore.model.game.BuildingModuleModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_module.BuildingModule;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.GameItemLoader;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
class BuildingModuleLoader extends AutoLoader<BuildingModuleModel, BuildingModule> {
    public BuildingModuleLoader(GameItemLoader gameItemLoader) {
        super(gameItemLoader);
    }

    @Override
    protected GameItemType getGameItemType() {
        return GameItemType.BUILDING_MODULE;
    }

    @Override
    protected Class<BuildingModuleModel[]> getArrayClass() {
        return BuildingModuleModel[].class;
    }

    @Override
    protected void addToGameData(GameData gameData, List<BuildingModule> items) {
        gameData.getBuildingModules()
            .addAll(items);
    }

    @Override
    protected BuildingModule convert(BuildingModuleModel model) {
        return BuildingModule.builder()
            .buildingModuleId(model.getId())
            .location(model.getLocation())
            .constructionAreaId(model.getConstructionAreaId())
            .dataId(model.getDataId())
            .build();
    }
}
