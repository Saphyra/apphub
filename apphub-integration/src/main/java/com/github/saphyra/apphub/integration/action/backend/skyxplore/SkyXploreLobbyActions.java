package com.github.saphyra.apphub.integration.action.backend.skyxplore;

import com.github.saphyra.apphub.integration.framework.Endpoints;
import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.structure.api.OneParamRequest;
import com.github.saphyra.apphub.integration.structure.api.OneParamResponse;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.AiPlayer;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.LobbyPlayerResponse;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.SkyXploreGameSettings;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.Response;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class SkyXploreLobbyActions {
    public static void createLobby(UUID accessTokenId, String gameName) {
        Response response = getCreateLobbyResponse(accessTokenId, gameName);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getCreateLobbyResponse(UUID accessTokenId, String gameName) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(new OneParamRequest<>(gameName))
            .put(UrlFactory.create(Endpoints.SKYXPLORE_CREATE_LOBBY));
    }

    public static void inviteToLobby(UUID accessTokenId, UUID friendId) {
        Response response = getInviteToLobbyResponse(accessTokenId, friendId);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getInviteToLobbyResponse(UUID accessTokenId, UUID friendId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .post(UrlFactory.create(Endpoints.SKYXPLORE_INVITE_TO_LOBBY, "friendId", friendId));
    }

    public static void acceptInvitation(UUID accessTokenId, UUID senderId) {
        Response response = getAcceptInvitationResponse(accessTokenId, senderId);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getAcceptInvitationResponse(UUID accessTokenId, UUID senderId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .post(UrlFactory.create(Endpoints.SKYXPLORE_LOBBY_ACCEPT_INVITATION, "invitorId", senderId));
    }

    public static void startGame(UUID accessTokenId) {
        Response response = getStartGameResponse(accessTokenId);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getStartGameResponse(UUID accessTokenId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .post(UrlFactory.create(Endpoints.SKYXPLORE_START_GAME));
    }

    public static List<LobbyPlayerResponse> getLobbyPlayers(UUID accessTokenId) {
        Response response = getLobbyPlayersResponse(accessTokenId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(LobbyPlayerResponse[].class));
    }

    public static Response getLobbyPlayersResponse(UUID accessTokenId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .get(UrlFactory.create(Endpoints.SKYXPLORE_LOBBY_GET_PLAYERS));
    }

    public static void exitFromLobby(UUID accessTokenId) {
        Response response = RequestFactory.createAuthorizedRequest(accessTokenId)
            .delete(UrlFactory.create(Endpoints.SKYXPLORE_EXIT_FROM_LOBBY));

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static SkyXploreGameSettings getGameSettings(UUID accessTokenId) {
        Response response = RequestFactory.createAuthorizedRequest(accessTokenId)
            .get(UrlFactory.create(Endpoints.SKYXPLORE_LOBBY_GET_GAME_SETTINGS));

        assertThat(response.getStatusCode()).isEqualTo(200);
        return response.getBody().as(SkyXploreGameSettings.class);
    }

    public static Response getLoadGameResponse(UUID accessTokenId, UUID gameId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .post(UrlFactory.create(Endpoints.SKYXPLORE_LOBBY_LOAD_GAME, "gameId", gameId));
    }

    public static void loadGame(UUID accessTokenId, UUID gameId) {
        Response response = getLoadGameResponse(accessTokenId, gameId);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static boolean isUserInLobby(UUID accessTokenId) {
        Response response = RequestFactory.createAuthorizedRequest(accessTokenId)
            .get(UrlFactory.create(Endpoints.SKYXPLORE_LOBBY_IS_IN_LOBBY));

        assertThat(response.getStatusCode()).isEqualTo(200);

        TypeRef<OneParamResponse<Boolean>> typeRef = new TypeRef<OneParamResponse<Boolean>>() {
        };

        return response.getBody().as(typeRef)
            .getValue();
    }

    public static void createOrModifyAi(UUID accessTokenId, AiPlayer aiPlayer) {
        Response response = getCreateOrModifyAiResponse(accessTokenId, aiPlayer);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getCreateOrModifyAiResponse(UUID accessTokenId, AiPlayer aiPlayer) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(aiPlayer)
            .put(UrlFactory.create(Endpoints.SKYXPLORE_LOBBY_CREATE_OR_MODIFY_AI));
    }

    public static List<AiPlayer> getAis(UUID accessTokenId) {
        Response response = RequestFactory.createAuthorizedRequest(accessTokenId)
            .get(UrlFactory.create(Endpoints.SKYXPLORE_LOBBY_GET_AIS));

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(AiPlayer[].class));
    }

    public static void removeAi(UUID accessTokenId1, UUID aiId) {
        Response response = RequestFactory.createAuthorizedRequest(accessTokenId1)
            .delete(UrlFactory.create(Endpoints.SKYXPLORE_LOBBY_REMOVE_AI, "userId", aiId));

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static void changeAllianceOfPlayer(UUID accessTokenId, UUID userId, Object alliance) {
        Response response = getChangeAllianceOfPlayerResponse(accessTokenId, userId, alliance);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getChangeAllianceOfPlayerResponse(UUID accessTokenId, UUID userId, Object alliance) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(new OneParamRequest<>(alliance))
            .post(UrlFactory.create(Endpoints.SKYXPLORE_LOBBY_CHANGE_ALLIANCE_OF_PLAYER, "userId", userId));
    }

    public static void changeAllianceOfAI(UUID accessTokenId, UUID aiId, Object alliance) {
        Response response = getChangeAllianceOfAiResponse(accessTokenId, aiId, alliance);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getChangeAllianceOfAiResponse(UUID accessTokenId, UUID aiId, Object alliance) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(new OneParamRequest<>(alliance))
            .post(UrlFactory.create(Endpoints.SKYXPLORE_LOBBY_CHANGE_ALLIANCE_OF_AI, "userId", aiId));
    }

    public static Response getEditSettingsResponse(UUID accessTokenId, SkyXploreGameSettings settings) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(settings)
            .post(UrlFactory.create(Endpoints.SKYXPLORE_LOBBY_EDIT_SETTINGS));
    }
}
