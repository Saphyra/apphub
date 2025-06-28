package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.construction_area;

import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.GameDataItem;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SurfaceType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.BuildingModuleCategory;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.module.BuildingModuleDataService;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
class AvailableBuildingModulesService {
    private final GameDao gameDao;
    private final BuildingModuleDataService buildingModuleDataService;

    List<String> getAvailableBuildings(UUID userId, UUID constructionAreaId, String buildingModuleCategory) {
        BuildingModuleCategory category = ValidationUtil.convertToEnumChecked(buildingModuleCategory, BuildingModuleCategory::valueOf, "buildingModuleCategory");

        GameData gameData = gameDao.findByUserIdValidated(userId)
            .getData();

        SurfaceType surfaceType = gameData.getSurfaces()
            .findByIdValidated(gameData.getConstructionAreas().findByIdValidated(constructionAreaId).getSurfaceId())
            .getSurfaceType();

        return buildingModuleDataService.values()
            .stream()
            .filter(buildingModuleData -> buildingModuleData.getCategory() == category)
            .filter(buildingModuleData -> buildingModuleData.getSupportedSurfacesRestriction().isEmpty() || buildingModuleData.getSupportedSurfacesRestriction().contains(surfaceType))
            .map(GameDataItem::getId)
            .collect(Collectors.toList());
    }
}
