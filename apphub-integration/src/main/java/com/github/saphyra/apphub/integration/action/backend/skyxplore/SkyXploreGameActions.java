package com.github.saphyra.apphub.integration.action.backend.skyxplore;

import com.github.saphyra.apphub.integration.framework.Endpoints;
import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.localization.Language;
import com.github.saphyra.apphub.integration.structure.api.OneParamRequest;
import io.restassured.response.Response;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class SkyXploreGameActions {
    public static void setPaused(Language language, UUID accessTokenId, boolean isPaused) {
        Response response = RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .body(new OneParamRequest<>(isPaused))
            .post(UrlFactory.create(Endpoints.SKYXPLORE_GAME_PAUSE));

        assertThat(response.getStatusCode()).isEqualTo(200);
    }
}
