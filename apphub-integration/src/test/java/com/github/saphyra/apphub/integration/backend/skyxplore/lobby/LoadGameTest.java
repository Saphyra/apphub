package com.github.saphyra.apphub.integration.backend.skyxplore.lobby;


import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreFlow;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreFriendActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreLobbyActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreSavedGameActions;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.ErrorCode;
import com.github.saphyra.apphub.integration.framework.ResponseValidator;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.InvitationMessage;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.Player;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.SkyXploreCharacterModel;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import com.github.saphyra.apphub.integration.ws.ApphubWsClient;
import com.github.saphyra.apphub.integration.ws.model.WebSocketEvent;
import com.github.saphyra.apphub.integration.ws.model.WebSocketEventName;
import io.restassured.response.Response;
import org.java_websocket.client.WebSocketClient;
import org.testng.annotations.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class LoadGameTest extends BackEndTest {
    private static final String GAME_NAME = "game-name";

    @Test(groups = {"be", "skyxplore"})
    public void loadGame() {
        RegistrationParameters userData1 = RegistrationParameters.validParameters();
        SkyXploreCharacterModel characterModel1 = SkyXploreCharacterModel.valid();
        UUID accessTokenId1 = IndexPageActions.registerAndLogin(getServerPort(), userData1);
        SkyXploreCharacterActions.createOrUpdateCharacter(getServerPort(), accessTokenId1, characterModel1);
        UUID userId1 = DatabaseUtil.getUserIdByEmail(userData1.getEmail());

        RegistrationParameters userData2 = RegistrationParameters.validParameters();
        SkyXploreCharacterModel characterModel2 = SkyXploreCharacterModel.valid();
        UUID accessTokenId2 = IndexPageActions.registerAndLogin(getServerPort(), userData2);
        SkyXploreCharacterActions.createOrUpdateCharacter(getServerPort(), accessTokenId2, characterModel2);
        UUID userId2 = DatabaseUtil.getUserIdByEmail(userData2.getEmail());

        RegistrationParameters userData3 = RegistrationParameters.validParameters();
        SkyXploreCharacterModel characterModel3 = SkyXploreCharacterModel.valid();
        UUID accessTokenId3 = IndexPageActions.registerAndLogin(getServerPort(), userData3);
        SkyXploreCharacterActions.createOrUpdateCharacter(getServerPort(), accessTokenId3, characterModel3);
        UUID userId3 = DatabaseUtil.getUserIdByEmail(userData3.getEmail());

        SkyXploreFlow.startGame(getServerPort(), GAME_NAME, new Player(accessTokenId1, userId1), new Player(accessTokenId2, userId2))
            .values()
            .forEach(WebSocketClient::close);

        createLobby_gameNotFound(accessTokenId1);
        UUID gameId = createLobby_notHost(accessTokenId1, accessTokenId2);
        ApphubWsClient invitationClient = createLobby(characterModel1, accessTokenId1, userId1, accessTokenId2, gameId);
        startGane_notAllReady(accessTokenId1);
        ApphubWsClient hostLobbyClient = startGame_notHost(accessTokenId1, accessTokenId2);
        inviteAgainMember(accessTokenId1, accessTokenId2, userId2, invitationClient);
        inviteNotMember(accessTokenId1, accessTokenId3, userId3);
        loadGame(accessTokenId1, hostLobbyClient);
        checkGameLoadingProcess(hostLobbyClient);
        createLobby_gameMarkedForDeletion(accessTokenId1, gameId);
    }

    private static void createLobby_gameNotFound(UUID accessTokenId1) {
        Response createLobby_gameNotFoundResponse = SkyXploreLobbyActions.getLoadGameResponse(getServerPort(), accessTokenId1, UUID.randomUUID());
        ResponseValidator.verifyErrorResponse(createLobby_gameNotFoundResponse, 404, ErrorCode.GAME_NOT_FOUND);
    }

    private static UUID createLobby_notHost(UUID accessTokenId1, UUID accessTokenId2) {
        UUID gameId = SkyXploreSavedGameActions.getSavedGames(getServerPort(), accessTokenId1)
            .get(0)
            .getGameId();

        Response createLobby_notHostResponse = SkyXploreLobbyActions.getLoadGameResponse(getServerPort(), accessTokenId2, gameId);
        ResponseValidator.verifyForbiddenOperation(createLobby_notHostResponse);
        return gameId;
    }

    private static ApphubWsClient createLobby(SkyXploreCharacterModel characterModel1, UUID accessTokenId1, UUID userId1, UUID accessTokenId2, UUID gameId) {
        ApphubWsClient invitationClient = ApphubWsClient.createSkyXploreLobbyInvitation(getServerPort(), accessTokenId2, accessTokenId2);
        SkyXploreLobbyActions.loadGame(getServerPort(), accessTokenId1, gameId);

        InvitationMessage invitationMessage = invitationClient.awaitForEvent(WebSocketEventName.SKYXPLORE_MAIN_MENU_INVITATION)
            .orElseThrow(() -> new RuntimeException("Invitation not arrived"))
            .getPayloadAs(InvitationMessage.class);
        assertThat(invitationMessage.getSenderId()).isEqualTo(userId1);
        assertThat(invitationMessage.getSenderName()).isEqualTo(characterModel1.getName());
        SkyXploreLobbyActions.acceptInvitation(getServerPort(), accessTokenId2, userId1);
        return invitationClient;
    }

    private static void startGane_notAllReady(UUID accessTokenId1) {
        Response startGame_notAllReadyResponse = SkyXploreLobbyActions.getStartGameResponse(getServerPort(), accessTokenId1);
        ResponseValidator.verifyErrorResponse(startGame_notAllReadyResponse, 412, ErrorCode.LOBBY_PLAYER_NOT_READY);
    }

    private static ApphubWsClient startGame_notHost(UUID accessTokenId1, UUID accessTokenId2) {
        ApphubWsClient hostLobbyClient = ApphubWsClient.createSkyXploreLobby(getServerPort(), accessTokenId1, accessTokenId1);
        ApphubWsClient memberLobbyClient = ApphubWsClient.createSkyXploreLobby(getServerPort(), accessTokenId2, accessTokenId2);
        WebSocketEvent readyEvent = WebSocketEvent.builder()
            .eventName(WebSocketEventName.SKYXPLORE_LOBBY_SET_READINESS)
            .payload(true)
            .build();
        hostLobbyClient.send(readyEvent);
        memberLobbyClient.send(readyEvent);
        Response startGame_notHostResponse = SkyXploreLobbyActions.getStartGameResponse(getServerPort(), accessTokenId2);
        ResponseValidator.verifyForbiddenOperation(startGame_notHostResponse);
        return hostLobbyClient;
    }

    private static void inviteAgainMember(UUID accessTokenId1, UUID accessTokenId2, UUID userId2, ApphubWsClient mainMenuClient) {
        SkyXploreLobbyActions.exitFromLobby(getServerPort(), accessTokenId2);
        SkyXploreLobbyActions.inviteToLobby(getServerPort(), accessTokenId1, userId2);
        mainMenuClient.awaitForEvent(WebSocketEventName.SKYXPLORE_MAIN_MENU_INVITATION)
            .orElseThrow(() -> new RuntimeException("Invitation did not arrive."));
    }

    private static void inviteNotMember(UUID accessTokenId1, UUID accessTokenId3, UUID userId3) {
        SkyXploreFriendActions.setUpFriendship(getServerPort(), accessTokenId1, accessTokenId3, userId3);
        Response inviteNotMemberResponse = SkyXploreLobbyActions.getInviteToLobbyResponse(getServerPort(), accessTokenId1, userId3);
        ResponseValidator.verifyForbiddenOperation(inviteNotMemberResponse);
    }

    private static void loadGame(UUID accessTokenId1, ApphubWsClient hostLobbyClient) {
        SkyXploreLobbyActions.startGame(getServerPort(), accessTokenId1);
        hostLobbyClient.awaitForEvent(WebSocketEventName.SKYXPLORE_LOBBY_GAME_CREATION_INITIATED)
            .orElseThrow(() -> new RuntimeException("Game creation not started."));
    }

    private static void checkGameLoadingProcess(ApphubWsClient hostLobbyClient) {
        hostLobbyClient.awaitForEvent(WebSocketEventName.SKYXPLORE_LOBBY_GAME_LOADED, 120)
            .orElseThrow(() -> new RuntimeException("Game not loaded."));
    }

    private static void createLobby_gameMarkedForDeletion(UUID accessTokenId1, UUID gameId) {
        SkyXploreSavedGameActions.deleteGame(getServerPort(), accessTokenId1, gameId);
        Response loadGameResponse = SkyXploreLobbyActions.getLoadGameResponse(getServerPort(), accessTokenId1, gameId);

        ResponseValidator.verifyErrorResponse(loadGameResponse, 423, ErrorCode.GAME_DELETED);
    }
}
