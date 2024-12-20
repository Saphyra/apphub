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
    public static void terraform(int serverPort, UUID accessTokenId, UUID planetId, UUID surfaceUd, String surfaceType) {
        Response response = getTerraformResponse(serverPort, accessTokenId, planetId, surfaceUd, surfaceType);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getTerraformResponse(int serverPort, UUID accessTokenId, UUID planetId, UUID surfaceId, String surfaceType) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(new OneParamRequest<>(surfaceType))
            .post(UrlFactory.create(serverPort, SkyXploreGameEndpoints.SKYXPLORE_GAME_TERRAFORM_SURFACE, CollectionUtils.toMap(new BiWrapper<>("planetId", planetId), new BiWrapper<>("surfaceId", surfaceId))));
    }

    public static void cancelTerraformation(int serverPort, UUID accessTokenId, UUID planetId, UUID surfaceId) {
        Response response = getCancelTerraformationResponse(serverPort, accessTokenId, planetId, surfaceId);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getCancelTerraformationResponse(int serverPort, UUID accessTokenId, UUID planetId, UUID surfaceId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .delete(UrlFactory.create(serverPort, SkyXploreGameEndpoints.SKYXPLORE_GAME_CANCEL_TERRAFORMATION, CollectionUtils.toMap(new BiWrapper<>("planetId", planetId), new BiWrapper<>("surfaceId", surfaceId))));
    }
}
