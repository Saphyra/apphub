package com.github.saphyra.apphub.integration.backend.actions.skyxplore;

import com.github.saphyra.apphub.integration.backend.model.skyxplore.SurfaceResponse;
import com.github.saphyra.apphub.integration.common.framework.Endpoints;
import com.github.saphyra.apphub.integration.common.framework.RequestFactory;
import com.github.saphyra.apphub.integration.common.framework.UrlFactory;
import com.github.saphyra.apphub.integration.common.framework.localization.Language;
import com.github.saphyra.apphub.integration.common.model.OneParamRequest;
import io.restassured.response.Response;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class SkyXplorePlanetActions {
    public static Response getRenamePlanetResponse(Language language, UUID accessTokenId, UUID planetId, String planetName) {
        return RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .body(new OneParamRequest<>(planetName))
            .post(UrlFactory.create(Endpoints.SKYXPLORE_PLANET_RENAME, "planetId", planetId));
    }

    public static void renamePlanet(Language language, UUID accessTokenId, UUID planetId, String planetName) {
        Response response = getRenamePlanetResponse(language, accessTokenId, planetId, planetName);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static List<SurfaceResponse> getSurfaces(Language language, UUID accessTokenId, UUID planetId) {
        Response response = RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .get(UrlFactory.create(Endpoints.SKYXPLORE_PLANET_GET_SURFACE, "planetId", planetId));

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.stream(response.getBody().as(SurfaceResponse[].class))
            .collect(Collectors.toList());
    }
}
