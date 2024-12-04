package com.github.saphyra.apphub.integration.action.backend.skyxplore.game;

import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.framework.endpoints.skyxplore.SkyXploreGameEndpoints;
import com.github.saphyra.apphub.integration.structure.api.OneParamRequest;
import io.restassured.response.Response;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class SkyXploreConstructionAreaActions {
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
        Response response = RequestFactory.createAuthorizedRequest(accessTokenId)
            .delete(UrlFactory.create(serverPort, SkyXploreGameEndpoints.SKYXPLORE_PLANET_SURFACE_CANCEL_CONSTRUCTION_AREA_CONSTRUCTION, "constructionId", constructionId));

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static void cancelDeconstructionOfConstructionArea(int serverPort, UUID accessTokenId, UUID deconstructionId) {
        Response response = RequestFactory.createAuthorizedRequest(accessTokenId)
            .delete(UrlFactory.create(serverPort, SkyXploreGameEndpoints.SKYXPLORE_PLANET_SURFACE_CANCEL_DECONSTRUCT_CONSTRUCTION_AREA, "deconstructionId", deconstructionId));

        assertThat(response.getStatusCode()).isEqualTo(200);
    }
}
