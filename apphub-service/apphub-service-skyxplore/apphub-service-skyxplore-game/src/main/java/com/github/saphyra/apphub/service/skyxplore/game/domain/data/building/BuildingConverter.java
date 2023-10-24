package com.github.saphyra.apphub.service.skyxplore.game.domain.data.building;

import com.github.saphyra.apphub.api.skyxplore.model.game.BuildingModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.ConstructionResponse;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.DeconstructionResponse;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.SurfaceBuildingResponse;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameDataToModelConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.ConstructionConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction.DeconstructionConverter;
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
public class BuildingConverter implements GameDataToModelConverter {
    private final ConstructionConverter constructionConverter;
    private final DeconstructionConverter deconstructionConverter;

    public List<BuildingModel> toModel(UUID gameId, Collection<Building> buildings) {
        return buildings.stream()
            .map(building -> toModel(gameId, building))
            .collect(Collectors.toList());
    }

    public BuildingModel toModel(UUID gameId, Building building) {
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

    public SurfaceBuildingResponse toResponse(GameData gameData, Building building) {
        ConstructionResponse constructionResponse = gameData.getConstructions()
            .findByExternalReference(building.getBuildingId())
            .map(constructionConverter::toResponse)
            .orElse(null);

        DeconstructionResponse deconstructionResponse = gameData.getDeconstructions()
            .findByExternalReference(building.getBuildingId())
            .map(deconstructionConverter::toResponse)
            .orElse(null);

        return SurfaceBuildingResponse.builder()
            .buildingId(building.getBuildingId())
            .dataId(building.getDataId())
            .level(building.getLevel())
            .construction(constructionResponse)
            .deconstruction(deconstructionResponse)
            .build();
    }

    @Override
    public List<BuildingModel> convert(UUID gameId, GameData gameData) {
        return toModel(gameId, gameData.getBuildings());
    }
}
