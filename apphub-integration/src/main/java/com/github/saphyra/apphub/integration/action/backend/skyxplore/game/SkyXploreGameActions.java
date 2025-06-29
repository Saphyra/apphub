package com.github.saphyra.apphub.integration.action.backend.skyxplore.game;

import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.framework.endpoints.skyxplore.SkyXploreGameEndpoints;
import com.github.saphyra.apphub.integration.structure.api.OneParamRequest;
import io.restassured.response.Response;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class SkyXploreGameActions {
    public static void setPaused(int serverPort, UUID accessTokenId, boolean isPaused) {
        Response response = getPauseGameResponse(serverPort, accessTokenId, isPaused);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getPauseGameResponse(int serverPort, UUID accessTokenId, boolean isPaused) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(new OneParamRequest<>(isPaused))
            .post(UrlFactory.create(serverPort, SkyXploreGameEndpoints.SKYXPLORE_GAME_PAUSE));
    }

    public static boolean isHost(int serverPort, UUID accessTokenId) {
        Response response = getIsHostResponse(serverPort, accessTokenId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return response.getBody()
            .jsonPath()
            .getBoolean("value");
    }

    public static Response getIsHostResponse(int serverPort, UUID accessTokenId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .get(UrlFactory.create(serverPort, SkyXploreGameEndpoints.SKYXPLORE_GAME_IS_HOST));
    }

    public static void saveGame(int serverPort, UUID accessTokenId) {
        Response response = getSaveGameResponse(serverPort, accessTokenId);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getSaveGameResponse(int serverPort, UUID accessTokenId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .post(UrlFactory.create(serverPort, SkyXploreGameEndpoints.SKYXPLORE_GAME_SAVE));
    }

    public static void exit(int serverPort, UUID accessTokenId) {
        Response response = getExitResponse(serverPort, accessTokenId);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getExitResponse(int serverPort, UUID accessTokenId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .delete(UrlFactory.create(serverPort, SkyXploreGameEndpoints.SKYXPLORE_EXIT_GAME));
    }

    public static Response getIsUserInGameResponse(int serverPort, UUID accessTokenId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .get(UrlFactory.create(serverPort, SkyXploreGameEndpoints.SKYXPLORE_GET_GAME_ID_OF_USER));
    }

    public static Response getProcessTickResponse(int serverPort, UUID accessTokenId){
        return  RequestFactory.createAuthorizedRequest(accessTokenId)
            .post(UrlFactory.create(serverPort, SkyXploreGameEndpoints.SKYXPLORE_PROCESS_TICK));
    }
}
