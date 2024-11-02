package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.construction_area;

import com.github.saphyra.apphub.api.skyxplore.game.server.game.solar_system.planet.surface.SkyXplorePlanetSurfaceConstructionAreaController;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class ConstructionAreaController implements SkyXplorePlanetSurfaceConstructionAreaController {
    private final ConstructConstructionAreaService constructConstructionAreaService;
    private final CancelConstructionAreaConstructionService cancelConstructionAreaConstructionService;
    private final DeconstructConstructionAreaService deconstructConstructionAreaService;
    private final CancelDeconstructConstructionAreaService cancelDeconstructConstructionAreaService;

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

        cancelDeconstructConstructionAreaService.cancelDeconstruction(accessTokenHeader.getUserId(), deconstructionId);
    }
}
