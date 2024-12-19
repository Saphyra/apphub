package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.construction_area.building_module;

import com.github.saphyra.apphub.api.skyxplore.response.game.planet.overview.surface.building.BuildingModuleResponse;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.module.BuildingModuleDataService;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.ConstructionConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction.DeconstructionConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class BuildingModuleQueryService {
    private final BuildingModuleDataService buildingModuleDataService;
    private final GameDao gameDao;
    private final ConstructionConverter constructionConverter;
    private final DeconstructionConverter deconstructionConverter;

    public List<BuildingModuleResponse> getBuildingModulesOfConstructionArea(UUID userId, UUID constructionAreaId) {
        GameData gameData = gameDao.findByUserIdValidated(userId)
            .getData();

        return gameData.getBuildingModules()
            .getByConstructionAreaId(constructionAreaId)
            .stream()
            .map(buildingModule -> BuildingModuleResponse.builder()
                .buildingModuleId(buildingModule.getBuildingModuleId())
                .dataId(buildingModule.getDataId())
                .buildingModuleCategory(buildingModuleDataService.get(buildingModule.getDataId()).getCategory().name())
                .construction(gameData.getConstructions().findByExternalReference(buildingModule.getBuildingModuleId()).map(constructionConverter::toResponse).orElse(null))
                .deconstruction(gameData.getDeconstructions().findByExternalReference(buildingModule.getBuildingModuleId()).map(deconstructionConverter::toResponse).orElse(null))
                .build())
            .collect(Collectors.toList());
    }
}
