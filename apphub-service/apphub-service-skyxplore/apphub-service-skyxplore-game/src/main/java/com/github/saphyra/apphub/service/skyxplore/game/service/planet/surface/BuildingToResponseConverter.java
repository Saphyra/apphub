package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface;

import com.github.saphyra.apphub.api.skyxplore.response.game.planet.SurfaceBuildingResponse;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Building;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class BuildingToResponseConverter {
    private final ConstructionToResponseConverter constructionToResponseConverter;
    private final DeconstructionToResponseConverter deconstructionToResponseConverter;

    public SurfaceBuildingResponse convert(Building building) {
        return Optional.ofNullable(building)
            .map(b -> SurfaceBuildingResponse.builder()
                .buildingId(b.getBuildingId())
                .dataId(b.getDataId())
                .level(b.getLevel())
                .construction(Optional.ofNullable(building.getConstruction()).map(constructionToResponseConverter::convert).orElse(null))
                .deconstruction(Optional.ofNullable(building.getDeconstruction()).map(deconstructionToResponseConverter::convert).orElse(null))
                .build())
            .orElse(null);
    }
}
