package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.loader.impl;

import com.github.saphyra.apphub.api.skyxplore.model.game.BuildingModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building.Building;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.GameItemLoader;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BuildingLoader extends AutoLoader<BuildingModel, Building> {
    public BuildingLoader(GameItemLoader gameItemLoader) {
        super(gameItemLoader);
    }

    @Override
    protected GameItemType getGameItemType() {
        return GameItemType.BUILDING;
    }

    @Override
    protected Class<BuildingModel[]> getArrayClass() {
        return BuildingModel[].class;
    }

    @Override
    protected void addToGameData(GameData gameData, List<Building> items) {
        gameData.getBuildings()
            .addAll(items);
    }

    @Override
    protected Building convert(BuildingModel model) {
        return Building.builder()
            .buildingId(model.getId())
            .location(model.getLocation())
            .surfaceId(model.getSurfaceId())
            .dataId(model.getDataId())
            .level(model.getLevel())
            .build();
    }
}
