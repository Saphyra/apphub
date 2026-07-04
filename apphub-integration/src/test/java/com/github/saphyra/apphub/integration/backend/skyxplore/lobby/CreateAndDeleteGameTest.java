package com.github.saphyra.apphub.integration.backend.skyxplore.lobby;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreFriendActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreLobbyActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreSavedGameActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.ErrorCode;
import com.github.saphyra.apphub.integration.framework.ResponseValidator;
import com.github.saphyra.apphub.integration.framework.db.dynamodb.UserDynamoDbRepository;
import com.github.saphyra.apphub.integration.structure.api.ErrorResponse;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.SavedGameResponse;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.SkyXploreCharacterModel;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import com.github.saphyra.apphub.integration.ws.ApphubWsClient;
import com.github.saphyra.apphub.integration.ws.model.WebSocketEvent;
import com.github.saphyra.apphub.integration.ws.model.WebSocketEventName;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class CreateAndDeleteGameTest extends BackEndTest {
    private static final String GAME_NAME = "game-name";

    @Test(groups = {"be", "skyxplore"})
    public void createAndDeleteGame() {
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

        SkyXploreFriendActions.setUpFriendship(getServerPort(), accessToken1, accessToken2, userId2);

        SkyXploreLobbyActions.createLobby(getServerPort(), accessToken1, GAME_NAME);

        SkyXploreLobbyActions.inviteToLobby(getServerPort(), accessToken1, userId2);
        SkyXploreLobbyActions.acceptInvitation(getServerPort(), accessToken2, userId1);

        createGame_notHost(accessToken2);
        createGame_lobbyMemberNotReady(accessToken1);
        createGame(accessToken1, accessToken2);
        List<SavedGameResponse> savedGames = verifyingSavedGame(accessToken1, characterModel2);
        deleteGame_gameNotFound(accessToken1);
        deleteGame_notHost(accessToken2, savedGames);
        deleteGame(accessToken1, savedGames);
    }

    private static void createGame_notHost(String accessToken2) {
        Response forbiddenResponse = SkyXploreLobbyActions.getStartGameResponse(getServerPort(), accessToken2);
        assertThat(forbiddenResponse.getStatusCode()).isEqualTo(403);
        ErrorResponse forbiddenErrorResponse = forbiddenResponse.getBody().as(ErrorResponse.class);
        assertThat(forbiddenErrorResponse.getErrorCode()).isEqualTo(ErrorCode.FORBIDDEN_OPERATION.name());
    }

    private static void createGame_lobbyMemberNotReady(String accessToken1) {
        Response notReadyResponse = SkyXploreLobbyActions.getStartGameResponse(getServerPort(), accessToken1);
        assertThat(notReadyResponse.getStatusCode()).isEqualTo(412);
        ErrorResponse notReadyErrorResponse = notReadyResponse.getBody().as(ErrorResponse.class);
        assertThat(notReadyErrorResponse.getErrorCode()).isEqualTo(ErrorCode.LOBBY_PLAYER_NOT_READY.name());
    }

    private static void createGame(String accessToken1, String accessToken2) {
        ApphubWsClient hostLobbyWsClient = ApphubWsClient.createSkyXploreLobby(getServerPort(), accessToken1, "host");
        ApphubWsClient memberLobbyWsClient = ApphubWsClient.createSkyXploreLobby(getServerPort(), accessToken2, "member");

        WebSocketEvent readyEvent = WebSocketEvent.builder()
            .eventName(WebSocketEventName.SKYXPLORE_LOBBY_SET_READINESS)
            .payload(true)
            .build();
        hostLobbyWsClient.send(readyEvent);
        memberLobbyWsClient.send(readyEvent);

        SkyXploreLobbyActions.startGame(getServerPort(), accessToken1);

        hostLobbyWsClient.awaitForEvent(WebSocketEventName.SKYXPLORE_LOBBY_GAME_CREATION_INITIATED)
            .orElseThrow(() -> new RuntimeException("Lobby creation initiated event did not arrive."));

        hostLobbyWsClient.awaitForEvent(WebSocketEventName.SKYXPLORE_LOBBY_GAME_LOADED)
            .orElseThrow(() -> new RuntimeException("Game not loaded."));
    }

    private static List<SavedGameResponse> verifyingSavedGame(String accessToken1, SkyXploreCharacterModel characterModel2) {
        List<SavedGameResponse> savedGames = SkyXploreSavedGameActions.getSavedGames(getServerPort(), accessToken1);
        assertThat(savedGames).hasSize(1);
        assertThat(savedGames.get(0).getGameName()).isEqualTo(GAME_NAME);
        assertThat(savedGames.get(0).getPlayers()).isEqualTo(characterModel2.getName());
        return savedGames;
    }

    private static void deleteGame_gameNotFound(String accessToken1) {
        Response deleteGame_gameNotFoundResponse = SkyXploreSavedGameActions.getDeleteGameResponse(getServerPort(), accessToken1, UUID.randomUUID());
        ResponseValidator.verifyErrorResponse(deleteGame_gameNotFoundResponse, 404, ErrorCode.GAME_NOT_FOUND);
    }

    private static void deleteGame_notHost(String accessToken2, List<SavedGameResponse> savedGames) {
        Response deleteGame_notHostResponse = SkyXploreSavedGameActions.getDeleteGameResponse(getServerPort(), accessToken2, savedGames.get(0).getGameId());
        ResponseValidator.verifyForbiddenOperation(deleteGame_notHostResponse);
    }

    private static void deleteGame(String accessToken1, List<SavedGameResponse> savedGames) {
        Response deleteGameResponse = SkyXploreSavedGameActions.getDeleteGameResponse(getServerPort(), accessToken1, savedGames.get(0).getGameId());
        assertThat(deleteGameResponse.getStatusCode()).isEqualTo(200);

        savedGames = SkyXploreSavedGameActions.getSavedGames(getServerPort(), accessToken1);
        assertThat(savedGames).isEmpty();

        ApphubWsClient.cleanUpConnections();
    }
}
