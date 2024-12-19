package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.construction_area.building_module;

import com.github.saphyra.apphub.api.skyxplore.game.server.game.solar_system.planet.surface.construction_area.SkyXploreGamePlanetSurfaceConstructionAreaBuildingModuleController;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.overview.surface.building.BuildingModuleResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
public class SkyXploreGamePlanetSurfaceConstructionAreaBuildingModuleControllerImpl implements SkyXploreGamePlanetSurfaceConstructionAreaBuildingModuleController {
    private final BuildingModuleQueryService buildingModuleQueryService;
    private final ConstructBuildingModuleService constructBuildingModuleService;
    private final CancelConstructionOfBuildingModuleService cancelConstructionOfBuildingModuleService;
    private final DeconstructBuildingModuleService deconstructBuildingModuleService;
    private final CancelDeconstructionOfBuildingModuleService cancelDeconstructionOfBuildingModuleService;

    @Override
    public List<BuildingModuleResponse> getBuildingModules(UUID constructionAreaId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to know the buildingModules of constructionArea {}", accessTokenHeader.getUserId(), constructionAreaId);
        return buildingModuleQueryService.getBuildingModulesOfConstructionArea(accessTokenHeader.getUserId(), constructionAreaId);
    }

    @Override
    public List<BuildingModuleResponse> constructBuildingModule(OneParamRequest<String> buildingModuleDataId, UUID constructionAreaId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to construct buildingModule {} on constructionArea {}", accessTokenHeader.getUserId(), buildingModuleDataId.getValue(), constructionAreaId);

        constructBuildingModuleService.constructBuildingModule(accessTokenHeader.getUserId(), constructionAreaId, buildingModuleDataId.getValue());

        return getBuildingModules(constructionAreaId, accessTokenHeader);
    }

    @Override
    public List<BuildingModuleResponse> cancelConstructionOfBuildingModule(UUID constructionId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to cancel construction {} of buildingModule", accessTokenHeader.getUserId(), constructionId);

        UUID constructionAreaId = cancelConstructionOfBuildingModuleService.cancelConstruction(accessTokenHeader.getUserId(), constructionId);

        return getBuildingModules(constructionAreaId, accessTokenHeader);
    }

    @Override
    public List<BuildingModuleResponse> deconstructBuildingModule(UUID buildingModuleId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to deconstruct buildingModule {}", accessTokenHeader.getUserId(), buildingModuleId);

        UUID constructionAreaId = deconstructBuildingModuleService.deconstructBuildingModule(accessTokenHeader.getUserId(), buildingModuleId);

        return getBuildingModules(constructionAreaId, accessTokenHeader);
    }

    @Override
    public List<BuildingModuleResponse> cancelDeconstructionOfBuildingModule(UUID deconstructionId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to cancel of deconstruction {} of buildingModule", accessTokenHeader.getUserId(), deconstructionId);

        UUID constructionAreaId = cancelDeconstructionOfBuildingModuleService.cancelDeconstruction(accessTokenHeader.getUserId(), deconstructionId);

        return getBuildingModules(constructionAreaId, accessTokenHeader);
    }
}
