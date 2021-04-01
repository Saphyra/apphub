package com.github.saphyra.apphub.integration.backend.actions.skyxplore;

import com.github.saphyra.apphub.integration.backend.model.skyxplore.SolarSystemResponse;
import com.github.saphyra.apphub.integration.common.framework.Endpoints;
import com.github.saphyra.apphub.integration.common.framework.RequestFactory;
import com.github.saphyra.apphub.integration.common.framework.UrlFactory;
import com.github.saphyra.apphub.integration.common.framework.localization.Language;
import io.restassured.response.Response;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class SkyXploreSolarSystemActions {
    public static SolarSystemResponse getSolarSystem(Language language, UUID accessTokenId, UUID solarSystemId) {
        Response response = RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .get(UrlFactory.create(Endpoints.SKYXPLORE_GET_SOLAR_SYSTEM, "solarSystemId", solarSystemId));

        assertThat(response.getStatusCode()).isEqualTo(200);

        return response.getBody().as(SolarSystemResponse.class);
    }
}
