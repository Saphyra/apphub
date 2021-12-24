package com.github.saphyra.apphub.integration.backend.actions.skyxplore;

import com.github.saphyra.apphub.integration.backend.model.skyxplore.PlanetStorageResponse;
import com.github.saphyra.apphub.integration.common.framework.Endpoints;
import com.github.saphyra.apphub.integration.common.framework.RequestFactory;
import com.github.saphyra.apphub.integration.common.framework.UrlFactory;
import com.github.saphyra.apphub.integration.common.framework.localization.Language;
import io.restassured.response.Response;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class SkyXplorePlanetStorageActions {
    public static PlanetStorageResponse getStorageOverview(Language language, UUID accessTokenId, UUID planetId) {
        Response response = RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .get(UrlFactory.create(Endpoints.SKYXPLORE_PLANET_GET_STORAGE, "planetId", planetId));

        assertThat(response.getStatusCode()).isEqualTo(200);

        return response.getBody().as(PlanetStorageResponse.class);
    }
}
