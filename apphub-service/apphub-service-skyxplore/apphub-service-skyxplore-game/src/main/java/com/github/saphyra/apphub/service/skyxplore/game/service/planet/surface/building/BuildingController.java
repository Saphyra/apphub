package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.building;

import com.github.saphyra.apphub.api.skyxplore.game.server.SkyXploreGameBuildingController;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.building.construction.CancelConstructionService;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.building.deconstruct.CancelDeconstructionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
public class BuildingController implements SkyXploreGameBuildingController {
    private final CancelConstructionService cancelConstructionService;
    private final CancelDeconstructionService cancelDeconstructionService;

    @Override
    public void constructNewBuilding(OneParamRequest<String> dataId, UUID planetId, UUID surfaceId, AccessTokenHeader accessTokenHeader) {
    }

    @Override
    public void upgradeBuilding(UUID planetId, UUID buildingId, AccessTokenHeader accessTokenHeader) {
    }

    @Override
    public void cancelConstruction(UUID planetId, UUID buildingId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to cancel construction of building {} on planet {}", accessTokenHeader.getUserId(), buildingId, planetId);
        cancelConstructionService.cancelConstructionOfBuilding(accessTokenHeader.getUserId(), buildingId);
    }

    @Override
    public void deconstructBuilding(UUID planetId, UUID buildingId, AccessTokenHeader accessTokenHeader) {
    }

    @Override
    public void cancelDeconstruction(UUID planetId, UUID buildingId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to cancel deconstruction of building {} on planet {}", accessTokenHeader.getUserId(), buildingId, planetId);
        cancelDeconstructionService.cancelDeconstructionOfBuilding(accessTokenHeader.getUserId(), buildingId);
    }
}
