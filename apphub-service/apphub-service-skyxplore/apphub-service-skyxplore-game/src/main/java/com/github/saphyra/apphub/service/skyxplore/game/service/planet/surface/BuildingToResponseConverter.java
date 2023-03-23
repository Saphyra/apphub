package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface;

import com.github.saphyra.apphub.api.skyxplore.response.game.planet.ConstructionResponse;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.DeconstructionResponse;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.SurfaceBuildingResponse;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building.Building;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class BuildingToResponseConverter {
    private final ConstructionToResponseConverter constructionToResponseConverter;
    private final DeconstructionToResponseConverter deconstructionToResponseConverter;

    public SurfaceBuildingResponse convert(GameData gameData, Building building) {
        ConstructionResponse constructionResponse = gameData.getConstructions()
            .findByExternalReference(building.getBuildingId())
            .map(constructionToResponseConverter::convert)
            .orElse(null);

        DeconstructionResponse deconstructionResponse = gameData.getDeconstructions()
            .findByExternalReference(building.getBuildingId())
            .map(deconstructionToResponseConverter::convert)
            .orElse(null);

        return SurfaceBuildingResponse.builder()
            .buildingId(building.getBuildingId())
            .dataId(building.getDataId())
            .level(building.getLevel())
            .construction(constructionResponse)
            .deconstruction(deconstructionResponse)
            .build();
    }
}