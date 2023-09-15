package com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.SurfaceModel;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.ConstructionResponse;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.SurfaceBuildingResponse;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.SurfaceResponse;
import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameDataToModelConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building.BuildingConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.ConstructionConverter;
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
public class SurfaceConverter implements GameDataToModelConverter {
    private final BuildingConverter buildingConverter;
    private final ConstructionConverter constructionConverter;

    public List<SurfaceModel> toModel(UUID gameId, Collection<Surface> surfaces) {
        return surfaces.stream()
            .map(surface -> toModel(gameId, surface))
            .collect(Collectors.toList());
    }

    public SurfaceModel toModel(UUID gameId, Surface surface) {
        SurfaceModel model = new SurfaceModel();
        model.setId(surface.getSurfaceId());
        model.setGameId(gameId);
        model.setType(GameItemType.SURFACE);
        model.setPlanetId(surface.getPlanetId());
        model.setSurfaceType(surface.getSurfaceType().name());
        return model;
    }

    public SurfaceResponse toResponse(GameData gameData, UUID surfaceId) {
        Surface surface = gameData.getSurfaces()
            .findBySurfaceId(surfaceId);

        return toResponse(gameData, surface);
    }

    public SurfaceResponse toResponse(GameData gameData, Surface surface) {
        Coordinate coordinate = gameData.getCoordinates()
            .findByReferenceId(surface.getSurfaceId());

        SurfaceBuildingResponse buildingResponse = gameData.getBuildings()
            .findBySurfaceId(surface.getSurfaceId())
            .map(building -> buildingConverter.toResponse(gameData, building))
            .orElse(null);

        ConstructionResponse terraformation = gameData.getConstructions()
            .findByExternalReference(surface.getSurfaceId())
            .map(constructionConverter::toResponse)
            .orElse(null);

        return SurfaceResponse.builder()
            .surfaceId(surface.getSurfaceId())
            .coordinate(coordinate)
            .surfaceType(surface.getSurfaceType().name())
            .building(buildingResponse)
            .terraformation(terraformation)
            .build();
    }

    @Override
    public List<SurfaceModel> convert(UUID gameId, GameData gameData) {
        return toModel(gameId, gameData.getSurfaces());
    }
}
