package com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction_area;

import com.github.saphyra.apphub.api.skyxplore.model.game.ConstructionAreaModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameDataToModelConverter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
//TODO unit test
public class ConstructionAreaConverter implements GameDataToModelConverter {
    @Override
    public List<ConstructionAreaModel> convert(UUID gameId, GameData gameData) {
        return gameData.getConstructionAreas()
            .stream()
            .map(constructionArea -> convert(gameId, constructionArea))
            .toList();
    }

    private ConstructionAreaModel convert(UUID gameId, ConstructionArea buildingModule) {
        ConstructionAreaModel model = new ConstructionAreaModel();
        model.setId(buildingModule.getConstructionAreaId());
        model.setGameId(gameId);
        model.setType(GameItemType.BUILDING_MODULE);
        model.setLocation(buildingModule.getLocation());
        model.setSurfaceId(buildingModule.getSurfaceId());
        model.setDataId(buildingModule.getDataId());
        return model;
    }
}
