package com.github.saphyra.apphub.integration.action.backend.skyxplore;

import com.github.saphyra.apphub.integration.framework.Endpoints;
import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.SavedGameResponse;
import io.restassured.response.Response;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class SkyXploreSavedGameActions {
    public static List<SavedGameResponse> getSavedGames(UUID accessTokenId) {
        Response response = RequestFactory.createAuthorizedRequest(accessTokenId)
            .get(UrlFactory.create(Endpoints.SKYXPLORE_GET_GAMES));

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(SavedGameResponse[].class));
    }

    public static void deleteGame(UUID accessTokenId, UUID gameId) {
        Response response = getDeleteGameResponse(accessTokenId, gameId);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getDeleteGameResponse(UUID accessTokenId, UUID gameId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .delete(UrlFactory.create(Endpoints.SKYXPLORE_DELETE_GAME, "gameId", gameId));
    }
}
