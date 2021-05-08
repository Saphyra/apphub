package com.github.saphyra.apphub.integration.backend.actions.skyxplore;

import com.github.saphyra.apphub.integration.backend.model.skyxplore.GameSettingsResponse;
import com.github.saphyra.apphub.integration.backend.model.skyxplore.LobbyMembersResponse;
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
        Response response = getCreateLobbyResponse(language, accessTokenId, gameName);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getCreateLobbyResponse(Language language, UUID accessTokenId, String gameName) {
        return RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .body(new OneParamRequest<>(gameName))
            .put(UrlFactory.create(Endpoints.SKYXPLORE_CREATE_LOBBY));
    }

    public static void inviteToLobby(Language language, UUID accessTokenId, UUID friendId) {
        Response response = getInviteToLobbyResponse(language, accessTokenId, friendId);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getInviteToLobbyResponse(Language language, UUID accessTokenId, UUID friendId) {
        return RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .post(UrlFactory.create(Endpoints.SKYXPLORE_INVITE_TO_LOBBY, "friendId", friendId));
    }

    public static void acceptInvitation(Language language, UUID accessTokenId, UUID senderId) {
        Response response = getAcceptInvitationResponse(language, accessTokenId, senderId);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getAcceptInvitationResponse(Language language, UUID accessTokenId, UUID senderId) {
        return RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .post(UrlFactory.create(Endpoints.SKYXPLORE_LOBBY_ACCEPT_INVITATION, "invitorId", senderId));
    }

    public static void startGame(Language language, UUID accessTokenId) {
        Response response = getStartGameResponse(language, accessTokenId);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getStartGameResponse(Language language, UUID accessTokenId) {
        return RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .post(UrlFactory.create(Endpoints.SKYXPLORE_START_GAME));
    }

    public static LobbyMembersResponse getLobbyMembers(Language language, UUID accessTokenId) {
        Response response = getLobbyMembersResponse(language, accessTokenId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return response.getBody().as(LobbyMembersResponse.class);
    }

    public static Response getLobbyMembersResponse(Language language, UUID accessTokenId) {
        return RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .get(UrlFactory.create(Endpoints.SKYXPLORE_LOBBY_GET_MEMBERS));
    }

    public static void exitFromLobby(Language language, UUID accessTokenId) {
        Response response = RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .delete(UrlFactory.create(Endpoints.SKYXPLORE_EXIT_FROM_LOBBY));

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static GameSettingsResponse getGameSettings(Language language, UUID accessTokenId) {
        Response response = RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .get(UrlFactory.create(Endpoints.SKYXPLORE_LOBBY_GET_GAME_SETTINGS));

        assertThat(response.getStatusCode()).isEqualTo(200);
        return response.getBody().as(GameSettingsResponse.class);
    }
}
