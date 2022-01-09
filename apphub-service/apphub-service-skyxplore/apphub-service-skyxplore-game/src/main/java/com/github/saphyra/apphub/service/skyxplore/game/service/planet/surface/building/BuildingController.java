package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.building;

import com.github.saphyra.apphub.api.skyxplore.game.server.SkyXploreGameBuildingController;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.SurfaceResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.building.construction.CancelConstructionService;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.building.construction.ConstructNewBuildingService;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.building.construction.UpgradeBuildingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
public class BuildingController implements SkyXploreGameBuildingController {
    private final ConstructNewBuildingService constructNewBuildingService;
    private final UpgradeBuildingService upgradeBuildingService;
    private final CancelConstructionService cancelConstructionService;

    @Override
    public SurfaceResponse constructNewBuilding(OneParamRequest<String> dataId, UUID planetId, UUID surfaceId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to build a {} to surface {} of planet {}", accessTokenHeader.getUserId(), dataId.getValue(), surfaceId, planetId);
        return constructNewBuildingService.constructNewBuilding(accessTokenHeader.getUserId(), dataId.getValue(), planetId, surfaceId);
    }

    @Override
    public SurfaceResponse upgradeBuilding(UUID planetId, UUID buildingId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to upgrade building {} on planet {}", accessTokenHeader.getUserId(), buildingId, planetId);
        return upgradeBuildingService.upgradeBuilding(accessTokenHeader.getUserId(), planetId, buildingId);
    }

    @Override
    public SurfaceResponse cancelConstruction(UUID planetId, UUID buildingId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to cancel construction of building {} on planet {}", accessTokenHeader.getUserId(), buildingId, planetId);
        return cancelConstructionService.cancelConstructionOfBuilding(accessTokenHeader.getUserId(), planetId, buildingId);
    }
}
