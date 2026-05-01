package com.github.saphyra.apphub.service.feature.skyxplore.game.service.planet.surface.construction_area;

import com.github.saphyra.apphub.api.feature.skyxplore.game.server.game.solar_system.planet.surface.SkyXplorePlanetSurfaceConstructionAreaController;
import com.github.saphyra.apphub.lib.common_domain.AccessToken;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.service.feature.skyxplore.game.service.planet.surface.construction_area.common.CancelDeconstructionFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
class ConstructionAreaController implements SkyXplorePlanetSurfaceConstructionAreaController {
    private final ConstructConstructionAreaService constructConstructionAreaService;
    private final CancelConstructionAreaConstructionService cancelConstructionAreaConstructionService;
    private final DeconstructConstructionAreaService deconstructConstructionAreaService;
    private final CancelDeconstructionFacade cancelDeconstructionFacade;
    private final AvailableBuildingModulesService availableBuildingModulesService;

    @Override
    public void constructConstructionArea(OneParamRequest<String> constructionAreaDataId, UUID surfaceId, AccessToken accessToken) {
        log.info("{} wants to construct a {} on surface {}", accessToken.getUserId(), constructionAreaDataId.getValue(), surfaceId);

        constructConstructionAreaService.constructConstructionArea(accessToken.getUserId(), surfaceId, constructionAreaDataId.getValue());
    }

    @Override
    public void cancelConstructionAreaConstruction(UUID constructionId, AccessToken accessToken) {
        log.info("{} wants to cancel constructionArea construction {}", accessToken.getUserId(), constructionId);

        cancelConstructionAreaConstructionService.cancelConstruction(accessToken.getUserId(), constructionId);
    }

    @Override
    public void deconstructConstructionArea(UUID constructionAreaId, AccessToken accessToken) {
        log.info("{} wants to deconstruct constructionArea {}", accessToken.getUserId(), constructionAreaId);

        deconstructConstructionAreaService.deconstructConstructionArea(accessToken.getUserId(), constructionAreaId);
    }

    @Override
    public void cancelDeconstructConstructionArea(UUID deconstructionId, AccessToken accessToken) {
        log.info("{} wants to cancel deconstruction {}", accessToken.getUserId(), deconstructionId);

        cancelDeconstructionFacade.cancelDeconstructionOfConstructionArea(accessToken.getUserId(), deconstructionId);
    }

    @Override
    public List<String> getAvailableBuildingModules(UUID constructionAreaId, String buildingModuleCategory, AccessToken accessToken) {
        log.info("{} wants to know which buildings they can build in the {} slot of constructionArea {}", accessToken.getUserId(), buildingModuleCategory, constructionAreaId);

        return availableBuildingModulesService.getAvailableBuildings(accessToken.getUserId(), constructionAreaId, buildingModuleCategory);
    }
}
