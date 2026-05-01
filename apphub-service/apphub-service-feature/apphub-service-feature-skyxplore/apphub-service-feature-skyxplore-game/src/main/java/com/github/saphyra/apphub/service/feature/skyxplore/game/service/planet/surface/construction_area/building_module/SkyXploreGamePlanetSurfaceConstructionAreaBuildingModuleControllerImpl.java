package com.github.saphyra.apphub.service.feature.skyxplore.game.service.planet.surface.construction_area.building_module;

import com.github.saphyra.apphub.api.feature.skyxplore.game.server.game.solar_system.planet.surface.construction_area.SkyXploreGamePlanetSurfaceConstructionAreaBuildingModuleController;
import com.github.saphyra.apphub.api.feature.skyxplore.response.game.planet.overview.surface.building.BuildingModuleResponse;
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
public class SkyXploreGamePlanetSurfaceConstructionAreaBuildingModuleControllerImpl implements SkyXploreGamePlanetSurfaceConstructionAreaBuildingModuleController {
    private final BuildingModuleQueryService buildingModuleQueryService;
    private final ConstructBuildingModuleService constructBuildingModuleService;
    private final CancelConstructionOfBuildingModuleService cancelConstructionOfBuildingModuleService;
    private final DeconstructBuildingModuleService deconstructBuildingModuleService;
    private final CancelDeconstructionFacade cancelDeconstructionFacade;

    @Override
    public List<BuildingModuleResponse> getBuildingModules(UUID constructionAreaId, AccessToken accessToken) {
        log.info("{} wants to know the buildingModules of constructionArea {}", accessToken.getUserId(), constructionAreaId);
        return buildingModuleQueryService.getBuildingModulesOfConstructionArea(accessToken.getUserId(), constructionAreaId);
    }

    @Override
    public List<BuildingModuleResponse> constructBuildingModule(OneParamRequest<String> buildingModuleDataId, UUID constructionAreaId, AccessToken accessToken) {
        log.info("{} wants to construct buildingModule {} on constructionArea {}", accessToken.getUserId(), buildingModuleDataId.getValue(), constructionAreaId);

        constructBuildingModuleService.constructBuildingModule(accessToken.getUserId(), constructionAreaId, buildingModuleDataId.getValue());

        return getBuildingModules(constructionAreaId, accessToken);
    }

    @Override
    public List<BuildingModuleResponse> cancelConstructionOfBuildingModule(UUID constructionId, AccessToken accessToken) {
        log.info("{} wants to cancel construction {} of buildingModule", accessToken.getUserId(), constructionId);

        UUID constructionAreaId = cancelConstructionOfBuildingModuleService.cancelConstruction(accessToken.getUserId(), constructionId);

        return getBuildingModules(constructionAreaId, accessToken);
    }

    @Override
    public List<BuildingModuleResponse> deconstructBuildingModule(UUID buildingModuleId, AccessToken accessToken) {
        log.info("{} wants to deconstruct buildingModule {}", accessToken.getUserId(), buildingModuleId);

        UUID constructionAreaId = deconstructBuildingModuleService.deconstructBuildingModule(accessToken.getUserId(), buildingModuleId);

        return getBuildingModules(constructionAreaId, accessToken);
    }

    @Override
    public List<BuildingModuleResponse> cancelDeconstructionOfBuildingModule(UUID deconstructionId, AccessToken accessToken) {
        log.info("{} wants to cancel of deconstruction {} of buildingModule", accessToken.getUserId(), deconstructionId);

        UUID constructionAreaId = cancelDeconstructionFacade.cancelDeconstructionOfBuildingModule(accessToken.getUserId(), deconstructionId);

        return getBuildingModules(constructionAreaId, accessToken);
    }
}
