package com.github.saphyra.apphub.integration.action.backend.skyxplore;

import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.framework.endpoints.skyxplore.SkyXploreLobbyEndpoints;
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
    public static void createLobby(int serverPort, String accessToken, String gameName) {
        Response response = getCreateLobbyResponse(serverPort, accessToken, gameName);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getCreateLobbyResponse(int serverPort, String accessToken, String gameName) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .body(new OneParamRequest<>(gameName))
            .put(UrlFactory.create(serverPort, SkyXploreLobbyEndpoints.SKYXPLORE_CREATE_LOBBY));
    }

    public static void inviteToLobby(int serverPort, String accessToken, UUID friendId) {
        Response response = getInviteToLobbyResponse(serverPort, accessToken, friendId);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getInviteToLobbyResponse(int serverPort, String accessToken, UUID friendId) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .post(UrlFactory.create(serverPort, SkyXploreLobbyEndpoints.SKYXPLORE_INVITE_TO_LOBBY, "friendId", friendId));
    }

    public static void acceptInvitation(int serverPort, String accessToken, UUID senderId) {
        Response response = getAcceptInvitationResponse(serverPort, accessToken, senderId);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getAcceptInvitationResponse(int serverPort, String accessToken, UUID senderId) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .post(UrlFactory.create(serverPort, SkyXploreLobbyEndpoints.SKYXPLORE_LOBBY_ACCEPT_INVITATION, "invitorId", senderId));
    }

    public static void startGame(int serverPort, String accessToken) {
        Response response = getStartGameResponse(serverPort, accessToken);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getStartGameResponse(int serverPort, String accessToken) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .post(UrlFactory.create(serverPort, SkyXploreLobbyEndpoints.SKYXPLORE_LOBBY_START_GAME));
    }

    public static List<LobbyPlayerResponse> getLobbyPlayers(int serverPort, String accessToken) {
        Response response = getLobbyPlayersResponse(serverPort, accessToken);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(LobbyPlayerResponse[].class));
    }

    public static Response getLobbyPlayersResponse(int serverPort, String accessToken) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .get(UrlFactory.create(serverPort, SkyXploreLobbyEndpoints.SKYXPLORE_LOBBY_GET_PLAYERS));
    }

    public static void exitFromLobby(int serverPort, String accessToken) {
        Response response = getExitFromLobbyResponse(serverPort, accessToken);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getExitFromLobbyResponse(int serverPort, String accessToken) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .delete(UrlFactory.create(serverPort, SkyXploreLobbyEndpoints.SKYXPLORE_EXIT_FROM_LOBBY));
    }

    public static SkyXploreGameSettings getGameSettings(int serverPort, String accessToken) {
        Response response = getGameSettingsResponse(serverPort, accessToken);

        assertThat(response.getStatusCode()).isEqualTo(200);
        return response.getBody().as(SkyXploreGameSettings.class);
    }

    public static Response getGameSettingsResponse(int serverPort, String accessToken) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .get(UrlFactory.create(serverPort, SkyXploreLobbyEndpoints.SKYXPLORE_LOBBY_GET_SETTINGS));
    }

    public static Response getLoadGameResponse(int serverPort, String accessToken, UUID gameId) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .post(UrlFactory.create(serverPort, SkyXploreLobbyEndpoints.SKYXPLORE_LOBBY_LOAD_GAME, "gameId", gameId));
    }

    public static void loadGame(int serverPort, String accessToken, UUID gameId) {
        Response response = getLoadGameResponse(serverPort, accessToken, gameId);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static boolean isUserInLobby(int serverPort, String accessToken) {
        Response response = getIsUserInLobbyResponse(serverPort, accessToken);

        assertThat(response.getStatusCode()).isEqualTo(200);

        TypeRef<OneParamResponse<Boolean>> typeRef = new TypeRef<>() {
        };

        return response.getBody().as(typeRef)
            .getValue();
    }

    public static Response getIsUserInLobbyResponse(int serverPort, String accessToken) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .get(UrlFactory.create(serverPort, SkyXploreLobbyEndpoints.SKYXPLORE_LOBBY_IS_IN_LOBBY));
    }

    public static void createOrModifyAi(int serverPort, String accessToken, AiPlayer aiPlayer) {
        Response response = getCreateOrModifyAiResponse(serverPort, accessToken, aiPlayer);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getCreateOrModifyAiResponse(int serverPort, String accessToken, AiPlayer aiPlayer) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .body(aiPlayer)
            .put(UrlFactory.create(serverPort, SkyXploreLobbyEndpoints.SKYXPLORE_LOBBY_CREATE_OR_MODIFY_AI));
    }

    public static List<AiPlayer> getAis(int serverPort, String accessToken) {
        Response response = getAisResponse(serverPort, accessToken);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(AiPlayer[].class));
    }

    public static Response getAisResponse(int serverPort, String accessToken) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .get(UrlFactory.create(serverPort, SkyXploreLobbyEndpoints.SKYXPLORE_LOBBY_GET_AIS));
    }

    public static void removeAi(int serverPort, String accessToken1, UUID aiId) {
        Response response = getRemoveAiResponse(serverPort, accessToken1, aiId);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getRemoveAiResponse(int serverPort, String accessToken1, UUID aiId) {
        return RequestFactory.createAuthorizedRequest(accessToken1)
            .delete(UrlFactory.create(serverPort, SkyXploreLobbyEndpoints.SKYXPLORE_LOBBY_REMOVE_AI, "userId", aiId));
    }

    public static void changeAllianceOfPlayer(int serverPort, String accessToken, UUID userId, Object alliance) {
        Response response = getChangeAllianceOfPlayerResponse(serverPort, accessToken, userId, alliance);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getChangeAllianceOfPlayerResponse(int serverPort, String accessToken, UUID userId, Object alliance) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .body(new OneParamRequest<>(alliance))
            .post(UrlFactory.create(serverPort, SkyXploreLobbyEndpoints.SKYXPLORE_LOBBY_CHANGE_ALLIANCE_OF_PLAYER, "userId", userId));
    }

    public static void changeAllianceOfAI(int serverPort, String accessToken, UUID aiId, Object alliance) {
        Response response = getChangeAllianceOfAiResponse(serverPort, accessToken, aiId, alliance);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getChangeAllianceOfAiResponse(int serverPort, String accessToken, UUID aiId, Object alliance) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .body(new OneParamRequest<>(alliance))
            .post(UrlFactory.create(serverPort, SkyXploreLobbyEndpoints.SKYXPLORE_LOBBY_CHANGE_ALLIANCE_OF_AI, "userId", aiId));
    }

    public static Response getEditSettingsResponse(int serverPort, String accessToken, SkyXploreGameSettings settings) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .body(settings)
            .post(UrlFactory.create(serverPort, SkyXploreLobbyEndpoints.SKYXPLORE_LOBBY_EDIT_SETTINGS));
    }

    public static Response getLobbyViewForPageResponse(int serverPort, String accessToken) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .get(UrlFactory.create(serverPort, SkyXploreLobbyEndpoints.SKYXPLORE_LOBBY_VIEW_FOR_PAGE));
    }

    public static Response getActiveFriendsResponse(int serverPort, String accessToken) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .get(UrlFactory.create(serverPort, SkyXploreLobbyEndpoints.SKYXPLORE_LOBBY_GET_ACTIVE_FRIENDS));
    }

    public static Response getAlliancesResponse(int serverPort, String accessToken) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .get(UrlFactory.create(serverPort, SkyXploreLobbyEndpoints.SKYXPLORE_LOBBY_GET_ALLIANCES));
    }
}
