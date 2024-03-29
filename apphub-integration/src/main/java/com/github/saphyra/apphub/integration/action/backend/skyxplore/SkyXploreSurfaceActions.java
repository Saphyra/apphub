package com.github.saphyra.apphub.integration.action.backend.skyxplore;

import com.github.saphyra.apphub.integration.framework.BiWrapper;
import com.github.saphyra.apphub.integration.framework.CollectionUtils;
import com.github.saphyra.apphub.integration.framework.Endpoints;
import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.structure.api.OneParamRequest;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.SurfaceResponse;
import io.restassured.response.Response;

import java.util.UUID;

import static java.util.Objects.isNull;
import static org.assertj.core.api.Assertions.assertThat;

public class SkyXploreSurfaceActions {
    public static void terraform(UUID accessTokenId, UUID planetId, UUID surfaceUd, String surfaceType) {
        Response response = getTerraformResponse(accessTokenId, planetId, surfaceUd, surfaceType);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getTerraformResponse(UUID accessTokenId, UUID planetId, UUID surfaceId, String surfaceType) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(new OneParamRequest<>(surfaceType))
            .post(UrlFactory.create(Endpoints.SKYXPLORE_GAME_TERRAFORM_SURFACE, CollectionUtils.toMap(new BiWrapper<>("planetId", planetId), new BiWrapper<>("surfaceId", surfaceId))));
    }

    public static void cancelTerraformation(UUID accessTokenId, UUID planetId, UUID surfaceId) {
        Response response = getCancelTerraformationResponse(accessTokenId, planetId, surfaceId);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getCancelTerraformationResponse(UUID accessTokenId, UUID planetId, UUID surfaceId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .delete(UrlFactory.create(Endpoints.SKYXPLORE_GAME_CANCEL_TERRAFORMATION, CollectionUtils.toMap(new BiWrapper<>("planetId", planetId), new BiWrapper<>("surfaceId", surfaceId))));
    }

    public static UUID findEmptySurfaceId(UUID accessTokenId, UUID planetId, String surfaceType) {
        return SkyXplorePlanetActions.getSurfaces(accessTokenId, planetId)
            .stream()
            .filter(surfaceResponse -> isNull(surfaceResponse.getBuilding()))
            .filter(surfaceResponse -> surfaceResponse.getSurfaceType().equals(surfaceType))
            .findFirst()
            .map(SurfaceResponse::getSurfaceId)
            .orElseThrow(() -> new RuntimeException("Empty Desert not found on planet " + planetId));
    }
}
