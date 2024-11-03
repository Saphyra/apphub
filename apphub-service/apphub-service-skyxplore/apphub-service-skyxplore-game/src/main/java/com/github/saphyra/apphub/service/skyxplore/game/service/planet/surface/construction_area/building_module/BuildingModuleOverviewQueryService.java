package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.construction_area.building_module;

import com.github.saphyra.apphub.api.skyxplore.response.game.planet.BuildingModuleOverviewResponse;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.construction_area.ConstructionAreaDataService;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_module.BuildingModule;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction_area.ConstructionArea;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class BuildingModuleOverviewQueryService {
    private final ConstructionAreaDataService constructionAreaDataService;

    public Map<String, BuildingModuleOverviewResponse> getBuildingModuleOverview(GameData gameData, String constructionAreaDataId, List<ConstructionArea> constructionAreas) {
        return constructionAreaDataService.get(constructionAreaDataId)
            .getSlots()
            .entrySet()
            .stream()
            .collect(Collectors.toMap(entry -> entry.getKey().name(), entry -> {
                Map<String, Integer> modules = constructionAreas.stream()
                    .flatMap(constructionArea -> gameData.getBuildingModules().getByConstructionAreaId(constructionArea.getConstructionAreaId()).stream())
                    .collect(Collectors.groupingBy(BuildingModule::getDataId))
                    .entrySet()
                    .stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().size()));

                return BuildingModuleOverviewResponse.builder()
                    .availableSlots(constructionAreas.size() * entry.getValue())
                    .usedSlots(modules.values().stream().mapToInt(i -> i).sum())
                    .modules(modules)
                    .build();
            }));
    }
}
