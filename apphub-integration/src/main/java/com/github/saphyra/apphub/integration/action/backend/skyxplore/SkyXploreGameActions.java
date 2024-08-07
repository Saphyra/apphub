package com.github.saphyra.apphub.integration.action.backend.skyxplore;

import com.github.saphyra.apphub.integration.framework.Endpoints;
import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.structure.api.OneParamRequest;
import io.restassured.response.Response;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class SkyXploreGameActions {
    public static void setPaused(UUID accessTokenId, boolean isPaused) {
        Response response = getPauseGameResponse(accessTokenId, isPaused);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getPauseGameResponse(UUID accessTokenId, boolean isPaused) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(new OneParamRequest<>(isPaused))
            .post(UrlFactory.create(Endpoints.SKYXPLORE_GAME_PAUSE));
    }

    public static boolean isHost(UUID accessTokenId) {
        Response response = getIsHostResponse(accessTokenId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return response.getBody()
            .jsonPath()
            .getBoolean("value");
    }

    public static Response getIsHostResponse(UUID accessTokenId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .get(UrlFactory.create(Endpoints.SKYXPLORE_GAME_IS_HOST));
    }

    public static void saveGame(UUID accessTokenId) {
        Response response = getSaveGameResponse(accessTokenId);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getSaveGameResponse(UUID accessTokenId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .post(UrlFactory.create(Endpoints.SKYXPLORE_GAME_SAVE));
    }

    public static void exit(UUID accessTokenId) {
        Response response = getExitResponse(accessTokenId);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getExitResponse(UUID accessTokenId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .delete(UrlFactory.create(Endpoints.SKYXPLORE_EXIT_GAME));
    }

    public static Response getIsUserInGameResponse(UUID accessTokenId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .get(UrlFactory.create(Endpoints.SKYXPLORE_IS_USER_IN_GAME));
    }
}
