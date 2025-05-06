package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.construction_area;

import com.github.saphyra.apphub.api.skyxplore.game.server.game.solar_system.planet.surface.SkyXplorePlanetSurfaceConstructionAreaController;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.construction_area.common.CancelDeconstructionFacade;
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
    public void constructConstructionArea(OneParamRequest<String> constructionAreaDataId, UUID surfaceId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to construct a {} on surface {}", accessTokenHeader.getUserId(), constructionAreaDataId.getValue(), surfaceId);

        constructConstructionAreaService.constructConstructionArea(accessTokenHeader.getUserId(), surfaceId, constructionAreaDataId.getValue());
    }

    @Override
    public void cancelConstructionAreaConstruction(UUID constructionId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to cancel constructionArea construction {}", accessTokenHeader.getUserId(), constructionId);

        cancelConstructionAreaConstructionService.cancelConstruction(accessTokenHeader.getUserId(), constructionId);
    }

    @Override
    public void deconstructConstructionArea(UUID constructionAreaId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to deconstruct constructionArea {}", accessTokenHeader.getUserId(), constructionAreaId);

        deconstructConstructionAreaService.deconstructConstructionArea(accessTokenHeader.getUserId(), constructionAreaId);
    }

    @Override
    public void cancelDeconstructConstructionArea(UUID deconstructionId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to cancel deconstruction {}", accessTokenHeader.getUserId(), deconstructionId);

        cancelDeconstructionFacade.cancelDeconstructionOfConstructionArea(accessTokenHeader.getUserId(), deconstructionId);
    }

    @Override
    public List<String> getAvailableBuildingModules(UUID constructionAreaId, String buildingModuleCategory, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to know which buildings they can build in the {} slot of constructionArea {}", accessTokenHeader.getUserId(), buildingModuleCategory, constructionAreaId);

        return availableBuildingModulesService.getAvailableBuildings(accessTokenHeader.getUserId(), constructionAreaId, buildingModuleCategory);
    }
}
