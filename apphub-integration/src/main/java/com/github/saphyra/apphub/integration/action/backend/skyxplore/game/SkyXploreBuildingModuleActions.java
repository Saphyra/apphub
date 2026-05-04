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
    public static void constructBuildingModules(int serverPort, String accessToken, UUID constructionAreaId, String... dataIds) {
        Arrays.stream(dataIds)
            .forEach(dataId -> constructBuildingModule(serverPort, accessToken, constructionAreaId, dataId));

        SkyXploreGameActions.setPaused(serverPort, accessToken, false);

        AwaitilityWrapper.create(180, 5)
            .until(() -> getBuildingModules(serverPort, accessToken, constructionAreaId).stream().allMatch(buildingModuleResponse -> isNull(buildingModuleResponse.getConstruction())))
            .assertTrue("BuildingModule construction is not finished.");

        SkyXploreGameActions.setPaused(serverPort, accessToken, true);
    }

    public static List<BuildingModuleResponse> constructBuildingModule(int serverPort, String accessToken, UUID constructionAreaId, String dataId) {
        Response response = getConstructBuildingModuleResponse(serverPort, accessToken, constructionAreaId, dataId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(BuildingModuleResponse[].class));
    }

    public static Response getConstructBuildingModuleResponse(int serverPort, String accessToken, UUID constructionAreaId, String dataId) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .body(new OneParamRequest<>(dataId))
            .put(UrlFactory.create(serverPort, SkyXploreGameEndpoints.SKYXPLORE_PLANET_SURFACE_CONSTRUCTION_AREA_CONSTRUCT_BUILDING_MODULE, "constructionAreaId", constructionAreaId));
    }

    public static List<BuildingModuleResponse> deconstructBuildingModule(int serverPort, String accessToken, UUID buildingModuleId) {
        Response response = getDeconstructBuildingModuleResponse(serverPort, accessToken, buildingModuleId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(BuildingModuleResponse[].class));
    }

    public static Response getDeconstructBuildingModuleResponse(int serverPort, String accessToken, UUID buildingModuleId) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .delete(UrlFactory.create(serverPort, SkyXploreGameEndpoints.SKYXPLORE_PLANET_SURFACE_CONSTRUCTION_AREA_DECONSTRUCT_BUILDING_MODULE, "buildingModuleId", buildingModuleId));
    }

    public static List<BuildingModuleResponse> cancelConstruction(int serverPort, String accessToken, UUID constructionId) {
        Response response = getCancelConstructionResponse(serverPort, accessToken, constructionId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(BuildingModuleResponse[].class));
    }

    public static Response getCancelConstructionResponse(int serverPort, String accessToken, UUID constructionId) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .delete(UrlFactory.create(serverPort, SkyXploreGameEndpoints.SKYXPLORE_PLANET_SURFACE_CONSTRUCTION_AREA_CANCEL_CONSTRUCTION_OF_BUILDING_MODULE, "constructionId", constructionId));
    }

    public static List<BuildingModuleResponse> getBuildingModules(int serverPort, String accessToken, UUID constructionAreaId) {
        Response response = getBuildingModulesResponse(serverPort, accessToken, constructionAreaId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(BuildingModuleResponse[].class));
    }

    public static Response getBuildingModulesResponse(int serverPort, String accessToken, UUID constructionAreaId) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .get(UrlFactory.create(serverPort, SkyXploreGameEndpoints.SKYXPLORE_PLANET_SURFACE_CONSTRUCTION_AREA_GET_BUILDING_MODULES, "constructionAreaId", constructionAreaId));
    }

    public static List<BuildingModuleResponse> cancelDeconstruction(int serverPort, String accessToken, UUID deconstructionId) {
        Response response = getCancelDeconstructionResponse(serverPort, accessToken, deconstructionId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(BuildingModuleResponse[].class));
    }

    public static Response getCancelDeconstructionResponse(int serverPort, String accessToken, UUID deconstructionId) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .delete(UrlFactory.create(serverPort, SkyXploreGameEndpoints.SKYXPLORE_PLANET_SURFACE_CONSTRUCTION_AREA_CANCEL_DECONSTRUCTION_OF_BUILDING_MODULE, "deconstructionId", deconstructionId));
    }
}
