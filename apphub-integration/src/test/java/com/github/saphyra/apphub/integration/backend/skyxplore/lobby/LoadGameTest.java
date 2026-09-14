package com.github.saphyra.apphub.integration.backend.skyxplore.lobby;


import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreFlow;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreFriendActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreLobbyActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreSavedGameActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.ErrorCode;
import com.github.saphyra.apphub.integration.framework.ResponseValidator;
import com.github.saphyra.apphub.integration.framework.db.dynamodb.UserDynamoDbRepository;
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
        String accessToken1 = IndexPageActions.registerAndLogin(getServerPort(), userData1);
        SkyXploreCharacterActions.createOrUpdateCharacter(getServerPort(), accessToken1, characterModel1);
        UUID userId1 = UserDynamoDbRepository.getUserIdByEmail(userData1.getEmail());

        RegistrationParameters userData2 = RegistrationParameters.validParameters();
        SkyXploreCharacterModel characterModel2 = SkyXploreCharacterModel.valid();
        String accessToken2 = IndexPageActions.registerAndLogin(getServerPort(), userData2);
        SkyXploreCharacterActions.createOrUpdateCharacter(getServerPort(), accessToken2, characterModel2);
        UUID userId2 = UserDynamoDbRepository.getUserIdByEmail(userData2.getEmail());

        RegistrationParameters userData3 = RegistrationParameters.validParameters();
        SkyXploreCharacterModel characterModel3 = SkyXploreCharacterModel.valid();
        String accessToken3 = IndexPageActions.registerAndLogin(getServerPort(), userData3);
        SkyXploreCharacterActions.createOrUpdateCharacter(getServerPort(), accessToken3, characterModel3);
        UUID userId3 = UserDynamoDbRepository.getUserIdByEmail(userData3.getEmail());

        SkyXploreFlow.startGame(getServerPort(), GAME_NAME, new Player(accessToken1, userId1), new Player(accessToken2, userId2))
            .values()
            .forEach(WebSocketClient::close);

        createLobby_gameNotFound(accessToken1);
        UUID gameId = createLobby_notHost(accessToken1, accessToken2);
        ApphubWsClient invitationClient = createLobby(characterModel1, accessToken1, userId1, accessToken2, gameId);
        startGane_notAllReady(accessToken1);
        ApphubWsClient hostLobbyClient = startGame_notHost(accessToken1, accessToken2);
        inviteAgainMember(accessToken1, accessToken2, userId2, invitationClient);
        inviteNotMember(accessToken1, accessToken3, userId3);
        loadGame(accessToken1, hostLobbyClient);
        checkGameLoadingProcess(hostLobbyClient);
        createLobby_gameMarkedForDeletion(accessToken1, gameId);
    }

    private static void createLobby_gameNotFound(String accessToken1) {
        Response createLobby_gameNotFoundResponse = SkyXploreLobbyActions.getLoadGameResponse(getServerPort(), accessToken1, UUID.randomUUID());
        ResponseValidator.verifyErrorResponse(createLobby_gameNotFoundResponse, 404, ErrorCode.GAME_NOT_FOUND);
    }

    private static UUID createLobby_notHost(String accessToken1, String accessToken2) {
        UUID gameId = SkyXploreSavedGameActions.getSavedGames(getServerPort(), accessToken1)
            .get(0)
            .getGameId();

        Response createLobby_notHostResponse = SkyXploreLobbyActions.getLoadGameResponse(getServerPort(), accessToken2, gameId);
        ResponseValidator.verifyForbiddenOperation(createLobby_notHostResponse);
        return gameId;
    }

    private static ApphubWsClient createLobby(SkyXploreCharacterModel characterModel1, String accessToken1, UUID userId1, String accessToken2, UUID gameId) {
        ApphubWsClient invitationClient = ApphubWsClient.createSkyXploreLobbyInvitation(getServerPort(), accessToken2, accessToken2);
        SkyXploreLobbyActions.loadGame(getServerPort(), accessToken1, gameId);

        InvitationMessage invitationMessage = invitationClient.awaitForEvent(WebSocketEventName.SKYXPLORE_MAIN_MENU_INVITATION)
            .orElseThrow(() -> new RuntimeException("Invitation not arrived"))
            .getPayloadAs(InvitationMessage.class);
        assertThat(invitationMessage.getSenderId()).isEqualTo(userId1);
        assertThat(invitationMessage.getSenderName()).isEqualTo(characterModel1.getName());
        SkyXploreLobbyActions.acceptInvitation(getServerPort(), accessToken2, userId1);
        return invitationClient;
    }

    private static void startGane_notAllReady(String accessToken1) {
        Response startGame_notAllReadyResponse = SkyXploreLobbyActions.getStartGameResponse(getServerPort(), accessToken1);
        ResponseValidator.verifyErrorResponse(startGame_notAllReadyResponse, 412, ErrorCode.LOBBY_PLAYER_NOT_READY);
    }

    private static ApphubWsClient startGame_notHost(String accessToken1, String accessToken2) {
        ApphubWsClient hostLobbyClient = ApphubWsClient.createSkyXploreLobby(getServerPort(), accessToken1, accessToken1);
        ApphubWsClient memberLobbyClient = ApphubWsClient.createSkyXploreLobby(getServerPort(), accessToken2, accessToken2);
        WebSocketEvent readyEvent = WebSocketEvent.builder()
            .eventName(WebSocketEventName.SKYXPLORE_LOBBY_SET_READINESS)
            .payload(true)
            .build();
        hostLobbyClient.send(readyEvent);
        memberLobbyClient.send(readyEvent);
        Response startGame_notHostResponse = SkyXploreLobbyActions.getStartGameResponse(getServerPort(), accessToken2);
        ResponseValidator.verifyForbiddenOperation(startGame_notHostResponse);
        return hostLobbyClient;
    }

    private static void inviteAgainMember(String accessToken1, String accessToken2, UUID userId2, ApphubWsClient mainMenuClient) {
        SkyXploreLobbyActions.exitFromLobby(getServerPort(), accessToken2);
        SkyXploreLobbyActions.inviteToLobby(getServerPort(), accessToken1, userId2);
        mainMenuClient.awaitForEvent(WebSocketEventName.SKYXPLORE_MAIN_MENU_INVITATION)
            .orElseThrow(() -> new RuntimeException("Invitation did not arrive."));
    }

    private static void inviteNotMember(String accessToken1, String accessToken3, UUID userId3) {
        SkyXploreFriendActions.setUpFriendship(getServerPort(), accessToken1, accessToken3, userId3);
        Response inviteNotMemberResponse = SkyXploreLobbyActions.getInviteToLobbyResponse(getServerPort(), accessToken1, userId3);
        ResponseValidator.verifyForbiddenOperation(inviteNotMemberResponse);
    }

    private static void loadGame(String accessToken1, ApphubWsClient hostLobbyClient) {
        SkyXploreLobbyActions.startGame(getServerPort(), accessToken1);
        hostLobbyClient.awaitForEvent(WebSocketEventName.SKYXPLORE_LOBBY_GAME_CREATION_INITIATED)
            .orElseThrow(() -> new RuntimeException("Game creation not started."));
    }

    private static void checkGameLoadingProcess(ApphubWsClient hostLobbyClient) {
        hostLobbyClient.awaitForEvent(WebSocketEventName.SKYXPLORE_LOBBY_GAME_LOADED, 120)
            .orElseThrow(() -> new RuntimeException("Game not loaded."));
    }

    private static void createLobby_gameMarkedForDeletion(String accessToken1, UUID gameId) {
        SkyXploreSavedGameActions.deleteGame(getServerPort(), accessToken1, gameId);
        Response loadGameResponse = SkyXploreLobbyActions.getLoadGameResponse(getServerPort(), accessToken1, gameId);

        ResponseValidator.verifyErrorResponse(loadGameResponse, 423, ErrorCode.GAME_DELETED);
    }
}
