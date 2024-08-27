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
    public static void createLobby(int serverPort, UUID accessTokenId, String gameName) {
        Response response = getCreateLobbyResponse(serverPort, accessTokenId, gameName);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getCreateLobbyResponse(int serverPort, UUID accessTokenId, String gameName) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(new OneParamRequest<>(gameName))
            .put(UrlFactory.create(serverPort, Endpoints.SKYXPLORE_CREATE_LOBBY));
    }

    public static void inviteToLobby(int serverPort, UUID accessTokenId, UUID friendId) {
        Response response = getInviteToLobbyResponse(serverPort, accessTokenId, friendId);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getInviteToLobbyResponse(int serverPort, UUID accessTokenId, UUID friendId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .post(UrlFactory.create(serverPort, Endpoints.SKYXPLORE_INVITE_TO_LOBBY, "friendId", friendId));
    }

    public static void acceptInvitation(int serverPort, UUID accessTokenId, UUID senderId) {
        Response response = getAcceptInvitationResponse(serverPort, accessTokenId, senderId);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getAcceptInvitationResponse(int serverPort, UUID accessTokenId, UUID senderId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .post(UrlFactory.create(serverPort, Endpoints.SKYXPLORE_LOBBY_ACCEPT_INVITATION, "invitorId", senderId));
    }

    public static void startGame(int serverPort, UUID accessTokenId) {
        Response response = getStartGameResponse(serverPort, accessTokenId);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getStartGameResponse(int serverPort, UUID accessTokenId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .post(UrlFactory.create(serverPort, Endpoints.SKYXPLORE_START_GAME));
    }

    public static List<LobbyPlayerResponse> getLobbyPlayers(int serverPort, UUID accessTokenId) {
        Response response = getLobbyPlayersResponse(serverPort, accessTokenId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(LobbyPlayerResponse[].class));
    }

    public static Response getLobbyPlayersResponse(int serverPort, UUID accessTokenId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .get(UrlFactory.create(serverPort, Endpoints.SKYXPLORE_LOBBY_GET_PLAYERS));
    }

    public static void exitFromLobby(int serverPort, UUID accessTokenId) {
        Response response = getExitFromLobbyResponse(serverPort, accessTokenId);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getExitFromLobbyResponse(int serverPort, UUID accessTokenId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .delete(UrlFactory.create(serverPort, Endpoints.SKYXPLORE_EXIT_FROM_LOBBY));
    }

    public static SkyXploreGameSettings getGameSettings(int serverPort, UUID accessTokenId) {
        Response response = getGameSettingsResponse(serverPort, accessTokenId);

        assertThat(response.getStatusCode()).isEqualTo(200);
        return response.getBody().as(SkyXploreGameSettings.class);
    }

    public static Response getGameSettingsResponse(int serverPort, UUID accessTokenId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .get(UrlFactory.create(serverPort, Endpoints.SKYXPLORE_LOBBY_GET_GAME_SETTINGS));
    }

    public static Response getLoadGameResponse(int serverPort, UUID accessTokenId, UUID gameId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .post(UrlFactory.create(serverPort, Endpoints.SKYXPLORE_LOBBY_LOAD_GAME, "gameId", gameId));
    }

    public static void loadGame(int serverPort, UUID accessTokenId, UUID gameId) {
        Response response = getLoadGameResponse(serverPort, accessTokenId, gameId);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static boolean isUserInLobby(int serverPort, UUID accessTokenId) {
        Response response = getIsUserInLobbyResponse(serverPort, accessTokenId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        TypeRef<OneParamResponse<Boolean>> typeRef = new TypeRef<>() {
        };

        return response.getBody().as(typeRef)
            .getValue();
    }

    public static Response getIsUserInLobbyResponse(int serverPort, UUID accessTokenId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .get(UrlFactory.create(serverPort, Endpoints.SKYXPLORE_LOBBY_IS_IN_LOBBY));
    }

    public static void createOrModifyAi(int serverPort, UUID accessTokenId, AiPlayer aiPlayer) {
        Response response = getCreateOrModifyAiResponse(serverPort, accessTokenId, aiPlayer);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getCreateOrModifyAiResponse(int serverPort, UUID accessTokenId, AiPlayer aiPlayer) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(aiPlayer)
            .put(UrlFactory.create(serverPort, Endpoints.SKYXPLORE_LOBBY_CREATE_OR_MODIFY_AI));
    }

    public static List<AiPlayer> getAis(int serverPort, UUID accessTokenId) {
        Response response = getAisResponse(serverPort, accessTokenId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(AiPlayer[].class));
    }

    public static Response getAisResponse(int serverPort, UUID accessTokenId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .get(UrlFactory.create(serverPort, Endpoints.SKYXPLORE_LOBBY_GET_AIS));
    }

    public static void removeAi(int serverPort, UUID accessTokenId1, UUID aiId) {
        Response response = getRemoveAiResponse(serverPort, accessTokenId1, aiId);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getRemoveAiResponse(int serverPort, UUID accessTokenId1, UUID aiId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId1)
            .delete(UrlFactory.create(serverPort, Endpoints.SKYXPLORE_LOBBY_REMOVE_AI, "userId", aiId));
    }

    public static void changeAllianceOfPlayer(int serverPort, UUID accessTokenId, UUID userId, Object alliance) {
        Response response = getChangeAllianceOfPlayerResponse(serverPort, accessTokenId, userId, alliance);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getChangeAllianceOfPlayerResponse(int serverPort, UUID accessTokenId, UUID userId, Object alliance) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(new OneParamRequest<>(alliance))
            .post(UrlFactory.create(serverPort, Endpoints.SKYXPLORE_LOBBY_CHANGE_ALLIANCE_OF_PLAYER, "userId", userId));
    }

    public static void changeAllianceOfAI(int serverPort, UUID accessTokenId, UUID aiId, Object alliance) {
        Response response = getChangeAllianceOfAiResponse(serverPort, accessTokenId, aiId, alliance);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getChangeAllianceOfAiResponse(int serverPort, UUID accessTokenId, UUID aiId, Object alliance) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(new OneParamRequest<>(alliance))
            .post(UrlFactory.create(serverPort, Endpoints.SKYXPLORE_LOBBY_CHANGE_ALLIANCE_OF_AI, "userId", aiId));
    }

    public static Response getEditSettingsResponse(int serverPort, UUID accessTokenId, SkyXploreGameSettings settings) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(settings)
            .post(UrlFactory.create(serverPort, Endpoints.SKYXPLORE_LOBBY_EDIT_SETTINGS));
    }

    public static Response getLobbyViewForPageResponse(int serverPort, UUID accessTokenId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .get(UrlFactory.create(serverPort, Endpoints.SKYXPLORE_LOBBY_VIEW_FOR_PAGE));
    }

    public static Response getActiveFriendsResponse(int serverPort, UUID accessTokenId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .get(UrlFactory.create(serverPort, Endpoints.SKYXPLORE_LOBBY_GET_ACTIVE_FRIENDS));
    }

    public static Response getAlliancesResponse(int serverPort, UUID accessTokenId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .get(UrlFactory.create(serverPort, Endpoints.SKYXPLORE_LOBBY_GET_ALLIANCES));
    }
}
