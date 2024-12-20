package com.github.saphyra.apphub.integration.action.backend.skyxplore.game;

import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.framework.endpoints.skyxplore.SkyXploreGameEndpoints;
import com.github.saphyra.apphub.integration.structure.api.OneParamRequest;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.game.building.BuildingModuleResponse;
import io.restassured.response.Response;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static java.util.Objects.isNull;
import static org.assertj.core.api.Assertions.assertThat;

public class SkyXploreBuildingModuleActions {
    public static void constructBuildingModules(int serverPort, UUID accessTokenId, UUID constructionAreaId, String... dataIds) {
        Arrays.stream(dataIds)
            .forEach(dataId -> constructBuildingModule(serverPort, accessTokenId, constructionAreaId, dataId));

        SkyXploreGameActions.setPaused(serverPort, accessTokenId, false);

        AwaitilityWrapper.create(180, 5)
            .until(() -> getBuildingModules(serverPort, accessTokenId, constructionAreaId).stream().allMatch(buildingModuleResponse -> isNull(buildingModuleResponse.getConstruction())))
            .assertTrue("BuildingModule construction is not finished.");

        SkyXploreGameActions.setPaused(serverPort, accessTokenId, true);
    }

    public static List<BuildingModuleResponse> constructBuildingModule(int serverPort, UUID accessTokenId, UUID constructionAreaId, String dataId) {
        Response response = getConstructBuildingModuleResponse(serverPort, accessTokenId, constructionAreaId, dataId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(BuildingModuleResponse[].class));
    }

    public static Response getConstructBuildingModuleResponse(int serverPort, UUID accessTokenId, UUID constructionAreaId, String dataId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(new OneParamRequest<>(dataId))
            .put(UrlFactory.create(serverPort, SkyXploreGameEndpoints.SKYXPLORE_PLANET_SURFACE_CONSTRUCTION_AREA_CONSTRUCT_BUILDING_MODULE, "constructionAreaId", constructionAreaId));
    }

    public static List<BuildingModuleResponse> deconstructBuildingModule(int serverPort, UUID accessTokenId, UUID buildingModuleId) {
        Response response = getDeconstructBuildingModuleResponse(serverPort, accessTokenId, buildingModuleId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(BuildingModuleResponse[].class));
    }

    public static Response getDeconstructBuildingModuleResponse(int serverPort, UUID accessTokenId, UUID buildingModuleId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .delete(UrlFactory.create(serverPort, SkyXploreGameEndpoints.SKYXPLORE_PLANET_SURFACE_CONSTRUCTION_AREA_DECONSTRUCT_BUILDING_MODULE, "buildingModuleId", buildingModuleId));
    }

    public static List<BuildingModuleResponse> cancelConstruction(int serverPort, UUID accessTokenId, UUID constructionId) {
        Response response = getCancelConstructionResponse(serverPort, accessTokenId, constructionId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(BuildingModuleResponse[].class));
    }

    public static Response getCancelConstructionResponse(int serverPort, UUID accessTokenId, UUID constructionId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .delete(UrlFactory.create(serverPort, SkyXploreGameEndpoints.SKYXPLORE_PLANET_SURFACE_CONSTRUCTION_AREA_CANCEL_CONSTRUCTION_OF_BUILDING_MODULE, "constructionId", constructionId));
    }

    public static List<BuildingModuleResponse> getBuildingModules(int serverPort, UUID accessTokenId, UUID constructionAreaId) {
        Response response = getBuildingModulesResponse(serverPort, accessTokenId, constructionAreaId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(BuildingModuleResponse[].class));
    }

    public static Response getBuildingModulesResponse(int serverPort, UUID accessTokenId, UUID constructionAreaId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .get(UrlFactory.create(serverPort, SkyXploreGameEndpoints.SKYXPLORE_PLANET_SURFACE_CONSTRUCTION_AREA_GET_BUILDING_MODULES, "constructionAreaId", constructionAreaId));
    }

    public static List<BuildingModuleResponse> cancelDeconstruction(int serverPort, UUID accessTokenId, UUID deconstructionId) {
        Response response = getCancelDeconstructionResponse(serverPort, accessTokenId, deconstructionId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(BuildingModuleResponse[].class));
    }

    public static Response getCancelDeconstructionResponse(int serverPort, UUID accessTokenId, UUID deconstructionId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .delete(UrlFactory.create(serverPort, SkyXploreGameEndpoints.SKYXPLORE_PLANET_SURFACE_CONSTRUCTION_AREA_CANCEL_DECONSTRUCTION_OF_BUILDING_MODULE, "deconstructionId", deconstructionId));
    }
}
