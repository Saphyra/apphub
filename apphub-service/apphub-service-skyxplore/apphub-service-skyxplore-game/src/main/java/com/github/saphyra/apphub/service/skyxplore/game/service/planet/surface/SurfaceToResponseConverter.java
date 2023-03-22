package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface;

import com.github.saphyra.apphub.api.skyxplore.response.game.planet.ConstructionResponse;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.SurfaceBuildingResponse;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.SurfaceResponse;
import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class SurfaceToResponseConverter {
    private final BuildingToResponseConverter buildingToResponseConverter;
    private final ConstructionToResponseConverter constructionToResponseConverter;

    public SurfaceResponse convert(GameData gameData, UUID surfaceId) {
        Surface surface = gameData.getSurfaces()
            .findById(surfaceId);

        Coordinate coordinate = gameData.getCoordinates()
            .findByReferenceId(surfaceId);

        SurfaceBuildingResponse buildingResponse = gameData.getBuildings()
            .findBySurfaceId(surfaceId)
            .map(buildingToResponseConverter::convert)
            .orElse(null);

        ConstructionResponse terraformation = gameData.getConstructions()
            .findByExternalReference(surfaceId)
            .map(constructionToResponseConverter::convert)
            .orElse(null);

        return SurfaceResponse.builder()
            .surfaceId(surface.getSurfaceId())
            .coordinate(coordinate)
            .surfaceType(surface.getSurfaceType().name())
            .building(buildingResponse)
            .terraformation(terraformation)
            .build();
    }
}
