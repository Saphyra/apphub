package com.github.saphyra.apphub.integration.action.backend.skyxplore;

import com.github.saphyra.apphub.integration.framework.Endpoints;
import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.localization.Language;
import com.github.saphyra.apphub.integration.structure.OneParamRequest;
import io.restassured.response.Response;

import java.util.UUID;

public class SkyXploreGameActions {
    public static void setPaused(Language language, UUID accessTokenId, boolean isPaused) {
        Response response = RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .body(new OneParamRequest<>(isPaused))
            .post(UrlFactory.create(Endpoints.SKYXPLORE_GAME_PAUSE));
    }
}
