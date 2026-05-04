package com.github.saphyra.apphub.integration.action.backend.skyxplore.game;

import com.github.saphyra.apphub.integration.framework.BiWrapper;
import com.github.saphyra.apphub.integration.framework.CollectionUtils;
import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.framework.endpoints.skyxplore.SkyXploreGameEndpoints;
import com.github.saphyra.apphub.integration.structure.api.OneParamRequest;
import io.restassured.response.Response;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class SkyXploreSurfaceActions {
    public static void terraform(int serverPort, String accessToken, UUID planetId, UUID surfaceUd, String surfaceType) {
        Response response = getTerraformResponse(serverPort, accessToken, planetId, surfaceUd, surfaceType);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getTerraformResponse(int serverPort, String accessToken, UUID planetId, UUID surfaceId, String surfaceType) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .body(new OneParamRequest<>(surfaceType))
            .post(UrlFactory.create(serverPort, SkyXploreGameEndpoints.SKYXPLORE_GAME_TERRAFORM_SURFACE, CollectionUtils.toMap(new BiWrapper<>("planetId", planetId), new BiWrapper<>("surfaceId", surfaceId))));
    }

    public static void cancelTerraformation(int serverPort, String accessToken, UUID planetId, UUID surfaceId) {
        Response response = getCancelTerraformationResponse(serverPort, accessToken, planetId, surfaceId);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getCancelTerraformationResponse(int serverPort, String accessToken, UUID planetId, UUID surfaceId) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .delete(UrlFactory.create(serverPort, SkyXploreGameEndpoints.SKYXPLORE_GAME_CANCEL_TERRAFORMATION, CollectionUtils.toMap(new BiWrapper<>("planetId", planetId), new BiWrapper<>("surfaceId", surfaceId))));
    }
}
