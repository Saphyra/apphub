package com.github.saphyra.apphub.service.skyxplore.game.service.save.converter;

import com.github.saphyra.apphub.api.skyxplore.model.game.BuildingModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Building;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class BuildingToModelConverter {
    public BuildingModel convert(Building building, Game game) {
        BuildingModel model = new BuildingModel();
        model.setId(building.getBuildingId());
        model.setGameId(game.getGameId());
        model.setType(GameItemType.BUILDING);
        model.setDataId(building.getDataId());
        model.setLevel(building.getLevel());
        model.setSurfaceId(building.getSurfaceId());
        return model;
    }
}