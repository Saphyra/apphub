package com.github.saphyra.apphub.service.skyxplore.game.service.save.converter;

import com.github.saphyra.apphub.api.skyxplore.model.game.BuildingModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Building;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class BuildingToModelConverter {
    public BuildingModel convert(Building building, UUID gameId) {
        BuildingModel model = new BuildingModel();
        model.setId(building.getBuildingId());
        model.setGameId(gameId);
        model.setType(GameItemType.BUILDING);
        model.setDataId(building.getDataId());
        model.setLevel(building.getLevel());
        model.setSurfaceId(building.getSurfaceId());
        return model;
    }
}
