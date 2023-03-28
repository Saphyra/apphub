package com.github.saphyra.apphub.service.skyxplore.game.service.save.converter;

import com.github.saphyra.apphub.api.skyxplore.model.game.BuildingModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building.Building;
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
public class BuildingToModelConverter {
    public List<BuildingModel> convert(UUID gameId, Collection<Building> buildings) {
        return buildings.stream()
            .map(building -> convert(gameId, building))
            .collect(Collectors.toList());
    }

    public BuildingModel convert(UUID gameId, Building building) {
        BuildingModel model = new BuildingModel();
        model.setId(building.getBuildingId());
        model.setGameId(gameId);
        model.setType(GameItemType.BUILDING);
        model.setDataId(building.getDataId());
        model.setLevel(building.getLevel());
        model.setSurfaceId(building.getSurfaceId());
        model.setLocation(building.getLocation());
        return model;
    }
}
