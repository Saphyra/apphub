package com.github.saphyra.apphub.integration.action.backend.skyxplore;

import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.framework.endpoints.skyxplore.SkyXploreDataEndpoints;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.SavedGameResponse;
import io.restassured.response.Response;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class SkyXploreSavedGameActions {
    public static List<SavedGameResponse> getSavedGames(int serverPort, String accessToken) {
        Response response = getSavedGamesResponse(serverPort, accessToken);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(SavedGameResponse[].class));
    }

    public static Response getSavedGamesResponse(int serverPort, String accessToken) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .get(UrlFactory.create(serverPort, SkyXploreDataEndpoints.SKYXPLORE_GET_GAMES));
    }

    public static void deleteGame(int serverPort, String accessToken, UUID gameId) {
        Response response = getDeleteGameResponse(serverPort, accessToken, gameId);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getDeleteGameResponse(int serverPort, String accessToken, UUID gameId) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .delete(UrlFactory.create(serverPort, SkyXploreDataEndpoints.SKYXPLORE_DELETE_GAME, "gameId", gameId));
    }
}
