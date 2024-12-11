package com.github.saphyra.apphub.integration.action.backend.skyxplore.game;

import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.framework.endpoints.skyxplore.SkyXploreGameEndpoints;
import com.github.saphyra.apphub.integration.structure.api.OneParamRequest;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.game.SurfaceConstructionAreaResponse;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.game.SurfaceResponse;
import io.restassured.response.Response;

import java.util.Map;
import java.util.UUID;

import static java.util.Objects.isNull;
import static org.assertj.core.api.Assertions.assertThat;

public class SkyXploreConstructionAreaActions {
    public static UUID constructConstructionArea(int serverPort, UUID accessTokenId, UUID planetId, UUID surfaceId, String dataId) {
        SkyXploreConstructionAreaActions.constructConstructionArea(serverPort, accessTokenId, surfaceId, dataId);

        AwaitilityWrapper.awaitAssert(
            () -> SkyXplorePlanetActions.findSurfaceBySurfaceId(serverPort, accessTokenId, planetId, surfaceId),
            surfaceResponse -> assertThat(surfaceResponse)
                .extracting(SurfaceResponse::getConstructionArea)
                .returns(Constants.CONSTRUCTION_AREA_EXTRACTOR, SurfaceConstructionAreaResponse::getDataId)
                .extracting(SurfaceConstructionAreaResponse::getConstruction)
                .isNotNull()
        );

        SkyXploreGameActions.setPaused(serverPort, accessTokenId, false);

        AwaitilityWrapper.create(120, 1)
            .until(() -> isNull(SkyXplorePlanetActions.findSurfaceBySurfaceId(serverPort, accessTokenId, planetId, surfaceId).getConstructionArea().getConstruction()))
            .assertTrue("ConstructionArea is not finished");

        SkyXploreGameActions.setPaused(serverPort, accessTokenId, true);

        return SkyXplorePlanetActions.findSurfaceBySurfaceId(serverPort, accessTokenId, planetId, surfaceId)
            .getConstructionArea()
            .getConstructionAreaId();
    }

    public static void constructConstructionArea(int serverPort, UUID accessTokenId, UUID surfaceId, String dataId) {
        Response response = getConstructConstructionAreaResponse(serverPort, accessTokenId, surfaceId, dataId);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getConstructConstructionAreaResponse(int serverPort, UUID accessToken, UUID surfaceId, String dataId) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .body(new OneParamRequest<>(dataId))
            .put(UrlFactory.create(serverPort, SkyXploreGameEndpoints.SKYXPLORE_PLANET_SURFACE_CONSTRUCT_CONSTRUCTION_AREA, "surfaceId", surfaceId));
    }

    public static void deconstructConstructionArea(int serverPort, UUID accessTokenId, UUID constructionAreaId) {
        Response response = getDeconstructConstructionAreaResponse(serverPort, accessTokenId, constructionAreaId);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getDeconstructConstructionAreaResponse(int serverPort, UUID accessTokenId, UUID constructionAreaId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .delete(UrlFactory.create(serverPort, SkyXploreGameEndpoints.SKYXPLORE_PLANET_SURFACE_DECONSTRUCT_CONSTRUCTION_AREA, "constructionAreaId", constructionAreaId));
    }

    public static void cancelConstructionAreaConstruction(int serverPort, UUID accessTokenId, UUID constructionId) {
        Response response = getCancelConstructionAreaConstructionResponse(serverPort, accessTokenId, constructionId);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getCancelConstructionAreaConstructionResponse(int serverPort, UUID accessTokenId, UUID constructionId) {
        return getCancelDeconstructionOfConstructionAreaResponse(serverPort, accessTokenId, constructionId);
    }

    public static Response getCancelDeconstructionOfConstructionAreaResponse(int serverPort, UUID accessTokenId, UUID constructionId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .delete(UrlFactory.create(serverPort, SkyXploreGameEndpoints.SKYXPLORE_PLANET_SURFACE_CANCEL_CONSTRUCTION_AREA_CONSTRUCTION, "constructionId", constructionId));
    }

    public static void cancelDeconstructionOfConstructionArea(int serverPort, UUID accessTokenId, UUID deconstructionId) {
        Response response = RequestFactory.createAuthorizedRequest(accessTokenId)
            .delete(UrlFactory.create(serverPort, SkyXploreGameEndpoints.SKYXPLORE_PLANET_SURFACE_CANCEL_DECONSTRUCT_CONSTRUCTION_AREA, "deconstructionId", deconstructionId));

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getAvailableBuildingsResponse(int serverPort, UUID accessTokenId, UUID constructionAreaId, String buildingModuleCategory) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .get(UrlFactory.create(
                serverPort,
                SkyXploreGameEndpoints.SKYXPLORE_PLANET_SURFACE_CONSTRUCTION_AREA_AVAILABLE_BUILDING_MODULES,
                Map.of(
                    "constructionAreaId", constructionAreaId,
                    "buildingModuleCategory", buildingModuleCategory
                )
            ));
    }
}
