package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.building;

import com.github.saphyra.apphub.api.skyxplore.game.server.SkyXploreGameBuildingController;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.building.construction.CancelConstructionService;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.building.construction.ConstructNewBuildingService;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.building.construction.UpgradeBuildingService;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.building.deconstruct.CancelDeconstructionService;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.building.deconstruct.DeconstructBuildingService;
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
    private final DeconstructBuildingService deconstructBuildingService;
    private final CancelDeconstructionService cancelDeconstructionService;

    @Override
    public void constructNewBuilding(OneParamRequest<String> dataId, UUID planetId, UUID surfaceId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to build a {} to surface {} of planet {}", accessTokenHeader.getUserId(), dataId.getValue(), surfaceId, planetId);
        constructNewBuildingService.constructNewBuilding(accessTokenHeader.getUserId(), dataId.getValue(), planetId, surfaceId);
    }

    @Override
    public void upgradeBuilding(UUID planetId, UUID buildingId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to upgrade building {} on planet {}", accessTokenHeader.getUserId(), buildingId, planetId);
        upgradeBuildingService.upgradeBuilding(accessTokenHeader.getUserId(), planetId, buildingId);
    }

    @Override
    public void cancelConstruction(UUID planetId, UUID buildingId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to cancel construction of building {} on planet {}", accessTokenHeader.getUserId(), buildingId, planetId);
        cancelConstructionService.cancelConstructionOfBuilding(accessTokenHeader.getUserId(), planetId, buildingId);
    }

    @Override
    public void deconstructBuilding(UUID planetId, UUID buildingId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to deconstruct building {} on planet {}", accessTokenHeader.getUserId(), buildingId, planetId);
        deconstructBuildingService.deconstructBuilding(accessTokenHeader.getUserId(), planetId, buildingId);
    }

    @Override
    public void cancelDeconstruction(UUID planetId, UUID buildingId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to cancel deconstruction of building {} on planet {}", accessTokenHeader.getUserId(), buildingId, planetId);
        cancelDeconstructionService.cancelDeconstructionOfBuilding(accessTokenHeader.getUserId(), planetId, buildingId);
    }
}
