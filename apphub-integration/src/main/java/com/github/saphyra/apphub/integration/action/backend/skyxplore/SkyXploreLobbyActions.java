package com.github.saphyra.apphub.integration.action.backend.skyxplore;

import com.github.saphyra.apphub.integration.framework.Endpoints;
import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.localization.Language;
import com.github.saphyra.apphub.integration.structure.api.OneParamRequest;
import com.github.saphyra.apphub.integration.structure.api.OneParamResponse;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.AiPlayer;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.LobbyMemberResponse;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.SkyXploreGameSettings;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.Response;

import java.util.Arrays;
import java.util.List;
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

    public static List<LobbyMemberResponse> getLobbyMembers(Language language, UUID accessTokenId) {
        Response response = getLobbyMembersResponse(language, accessTokenId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(LobbyMemberResponse[].class));
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

    public static SkyXploreGameSettings getGameSettings(Language language, UUID accessTokenId) {
        Response response = RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .get(UrlFactory.create(Endpoints.SKYXPLORE_LOBBY_GET_GAME_SETTINGS));

        assertThat(response.getStatusCode()).isEqualTo(200);
        return response.getBody().as(SkyXploreGameSettings.class);
    }

    public static Response getLoadGameResponse(Language language, UUID accessTokenId, UUID gameId) {
        return RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .post(UrlFactory.create(Endpoints.SKYXPLORE_LOBBY_LOAD_GAME, "gameId", gameId));
    }

    public static void loadGame(Language language, UUID accessTokenId, UUID gameId) {
        Response response = getLoadGameResponse(language, accessTokenId, gameId);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static boolean isUserInLobby(Language language, UUID accessTokenId) {
        Response response = RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .get(UrlFactory.create(Endpoints.SKYXPLORE_LOBBY_IS_IN_LOBBY));

        assertThat(response.getStatusCode()).isEqualTo(200);

        TypeRef<OneParamResponse<Boolean>> typeRef = new TypeRef<OneParamResponse<Boolean>>() {
        };

        return response.getBody().as(typeRef)
            .getValue();
    }

    public static void createOrModifyAi(Language language, UUID accessTokenId, AiPlayer aiPlayer) {
        Response response = getCreateOrModifyAiResponse(language, accessTokenId, aiPlayer);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getCreateOrModifyAiResponse(Language language, UUID accessTokenId, AiPlayer aiPlayer) {
        return RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .body(aiPlayer)
            .put(UrlFactory.create(Endpoints.SKYXPLORE_LOBBY_CREATE_OR_MODIFY_AI));
    }

    public static List<AiPlayer> getAis(Language language, UUID accessTokenId) {
        Response response = RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .get(UrlFactory.create(Endpoints.SKYXPLORE_LOBBY_GET_AIS));

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(AiPlayer[].class));
    }

    public static void removeAi(Language language, UUID accessTokenId1, UUID aiId) {
        Response response = RequestFactory.createAuthorizedRequest(language, accessTokenId1)
            .delete(UrlFactory.create(Endpoints.SKYXPLORE_LOBBY_REMOVE_AI, "userId", aiId));

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static void changeAllianceOfPlayer(Language language, UUID accessTokenId, UUID userId, Object alliance) {
        Response response = getChangeAllianceOfPlayerResponse(language, accessTokenId, userId, alliance);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getChangeAllianceOfPlayerResponse(Language language, UUID accessTokenId, UUID userId, Object alliance) {
        return RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .body(new OneParamRequest<>(alliance))
            .post(UrlFactory.create(Endpoints.SKYXPLORE_LOBBY_CHANGE_ALLIANCE_OF_PLAYER, "userId", userId));
    }

    public static void changeAllianceOfAI(Language language, UUID accessTokenId, UUID aiId, Object alliance) {
        Response response = getChangeAllianceOfAiResponse(language, accessTokenId, aiId, alliance);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getChangeAllianceOfAiResponse(Language language, UUID accessTokenId, UUID aiId, Object alliance) {
        return RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .body(new OneParamRequest<>(alliance))
            .post(UrlFactory.create(Endpoints.SKYXPLORE_LOBBY_CHANGE_ALLIANCE_OF_AI, "userId", aiId));
    }

    public static Response getEditSettingsResponse(Language language, UUID accessTokenId, SkyXploreGameSettings settings) {
        return RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .body(settings)
            .post(UrlFactory.create(Endpoints.SKYXPLORE_LOBBY_EDIT_SETTINGS));
    }
}
