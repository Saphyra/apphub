package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.construction_area;

import com.github.saphyra.apphub.api.skyxplore.response.game.planet.ConstructionAreaOverviewResponse;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction_area.ConstructionArea;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surface;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.construction_area.building_module.BuildingModuleOverviewQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class ConstructionAreaOverviewQueryService {
    private final BuildingModuleOverviewQueryService buildingModuleOverviewQueryService;

    public List<ConstructionAreaOverviewResponse> getConstructionAreaOverview(GameData gameData, List<Surface> surfaces) {
        return surfaces.stream()
            .flatMap(surface -> gameData.getConstructionAreas().findBySurfaceId(surface.getSurfaceId()).stream())
            .collect(Collectors.groupingBy(ConstructionArea::getDataId))
            .entrySet()
            .stream()
            .map(entry -> ConstructionAreaOverviewResponse.builder()
                .dataId(entry.getKey())
                .buildingModules(buildingModuleOverviewQueryService.getBuildingModuleOverview(gameData, entry.getKey(), entry.getValue()))
                .build())
            .collect(Collectors.toList());
    }
}
