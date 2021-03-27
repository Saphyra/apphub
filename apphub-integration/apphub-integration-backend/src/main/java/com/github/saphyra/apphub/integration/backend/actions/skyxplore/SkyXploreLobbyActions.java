package com.github.saphyra.apphub.integration.backend.actions.skyxplore;

import com.github.saphyra.apphub.integration.common.framework.Endpoints;
import com.github.saphyra.apphub.integration.common.framework.RequestFactory;
import com.github.saphyra.apphub.integration.common.framework.UrlFactory;
import com.github.saphyra.apphub.integration.common.framework.localization.Language;
import com.github.saphyra.apphub.integration.common.model.OneParamRequest;
import io.restassured.response.Response;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class SkyXploreLobbyActions {
    public static void createLobby(Language language, UUID accessTokenId, String gameName) {
        Response response = RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .body(new OneParamRequest<>(gameName))
            .put(UrlFactory.create(Endpoints.SKYXPLORE_CREATE_LOBBY));

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static void inviteToLobby(Language language, UUID accessTokenId, UUID friendId) {
        Response response = RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .post(UrlFactory.create(Endpoints.SKYXPLORE_INVITE_TO_LOBBY, "friendId", friendId));

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static void acceptInvitation(Language language, UUID accessTokenId, UUID senderId) {
        Response response = RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .post(UrlFactory.create(Endpoints.SKYXPLORE_LOBBY_ACCEPT_INVITATION, "invitorId", senderId));

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static void startGame(Language language, UUID accessTokenId) {
        Response response = RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .post(UrlFactory.create(Endpoints.SKYXPLORE_START_GAME));

        assertThat(response.getStatusCode()).isEqualTo(200);
    }
}
