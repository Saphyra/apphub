package com.github.saphyra.apphub.integraton.backend.skyxplore.lobby;

import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreFriendActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreLobbyActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreSavedGameActions;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.ErrorCode;
import com.github.saphyra.apphub.integration.framework.ResponseValidator;
import com.github.saphyra.apphub.integration.localization.Language;
import com.github.saphyra.apphub.integration.localization.LocalizationKey;
import com.github.saphyra.apphub.integration.localization.LocalizationProperties;
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

    @Test(dataProvider = "languageDataProvider", groups = {"be", "skyxplore"})
    public void createAndDeleteGame(Language language) {
        RegistrationParameters userData1 = RegistrationParameters.validParameters();
        SkyXploreCharacterModel characterModel1 = SkyXploreCharacterModel.valid();
        UUID accessTokenId1 = IndexPageActions.registerAndLogin(language, userData1);
        SkyXploreCharacterActions.createOrUpdateCharacter(language, accessTokenId1, characterModel1);
        UUID userId1 = DatabaseUtil.getUserIdByEmail(userData1.getEmail());

        RegistrationParameters userData2 = RegistrationParameters.validParameters();
        SkyXploreCharacterModel characterModel2 = SkyXploreCharacterModel.valid();
        UUID accessTokenId2 = IndexPageActions.registerAndLogin(language, userData2);
        SkyXploreCharacterActions.createOrUpdateCharacter(language, accessTokenId2, characterModel2);
        UUID userId2 = DatabaseUtil.getUserIdByEmail(userData2.getEmail());

        SkyXploreFriendActions.setUpFriendship(language, accessTokenId1, accessTokenId2, userId2);

        SkyXploreLobbyActions.createLobby(language, accessTokenId1, GAME_NAME);

        SkyXploreLobbyActions.inviteToLobby(language, accessTokenId1, userId2);
        SkyXploreLobbyActions.acceptInvitation(language, accessTokenId2, userId1);

        createGame_notHost(language, accessTokenId2);
        createGame_lobbyMemberNotReady(language, accessTokenId1);
        createGame(language, accessTokenId1, accessTokenId2);
        List<SavedGameResponse> savedGames = verifyingSavedGame(language, accessTokenId1, characterModel2);
        deleteGame_gameNotFound(language, accessTokenId1);
        deleteGame_notHost(language, accessTokenId2, savedGames);
        deleteGame(language, accessTokenId1, savedGames);
    }

    private static void createGame_notHost(Language language, UUID accessTokenId2) {
        Response forbiddenResponse = SkyXploreLobbyActions.getStartGameResponse(language, accessTokenId2);
        assertThat(forbiddenResponse.getStatusCode()).isEqualTo(403);
        ErrorResponse forbiddenErrorResponse = forbiddenResponse.getBody().as(ErrorResponse.class);
        assertThat(forbiddenErrorResponse.getErrorCode()).isEqualTo(ErrorCode.FORBIDDEN_OPERATION.name());
        assertThat(forbiddenErrorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, LocalizationKey.FORBIDDEN_OPERATION));
    }

    private static void createGame_lobbyMemberNotReady(Language language, UUID accessTokenId1) {
        Response notReadyResponse = SkyXploreLobbyActions.getStartGameResponse(language, accessTokenId1);
        assertThat(notReadyResponse.getStatusCode()).isEqualTo(412);
        ErrorResponse notReadyErrorResponse = notReadyResponse.getBody().as(ErrorResponse.class);
        assertThat(notReadyErrorResponse.getErrorCode()).isEqualTo(ErrorCode.LOBBY_MEMBER_NOT_READY.name());
        assertThat(notReadyErrorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, LocalizationKey.LOBBY_MEMBER_NOT_READY));
    }

    private static void createGame(Language language, UUID accessTokenId1, UUID accessTokenId2) {
        ApphubWsClient hostLobbyWsClient = ApphubWsClient.createSkyXploreLobby(language, accessTokenId1);
        ApphubWsClient memberLobbyWsClient = ApphubWsClient.createSkyXploreLobby(language, accessTokenId2);

        WebSocketEvent readyEvent = WebSocketEvent.builder()
            .eventName(WebSocketEventName.SKYXPLORE_LOBBY_SET_READINESS)
            .payload(true)
            .build();
        hostLobbyWsClient.send(readyEvent);
        memberLobbyWsClient.send(readyEvent);

        SkyXploreLobbyActions.startGame(language, accessTokenId1);

        hostLobbyWsClient.awaitForEvent(WebSocketEventName.SKYXPLORE_LOBBY_GAME_CREATION_INITIATED)
            .orElseThrow(() -> new RuntimeException("Lobby creation initiated event did not arrive."));

        hostLobbyWsClient.awaitForEvent(WebSocketEventName.SKYXPLORE_LOBBY_GAME_LOADED)
            .orElseThrow(() -> new RuntimeException("Game not loaded."));
    }

    private static List<SavedGameResponse> verifyingSavedGame(Language language, UUID accessTokenId1, SkyXploreCharacterModel characterModel2) {
        List<SavedGameResponse> savedGames = SkyXploreSavedGameActions.getSavedGames(language, accessTokenId1);
        assertThat(savedGames).hasSize(1);
        assertThat(savedGames.get(0).getGameName()).isEqualTo(GAME_NAME);
        assertThat(savedGames.get(0).getPlayers()).isEqualTo(characterModel2.getName());
        return savedGames;
    }

    private static void deleteGame_gameNotFound(Language language, UUID accessTokenId1) {
        Response deleteGame_gameNotFoundResponse = SkyXploreSavedGameActions.getDeleteGameResponse(language, accessTokenId1, UUID.randomUUID());
        ResponseValidator.verifyErrorResponse(language, deleteGame_gameNotFoundResponse, 404, ErrorCode.GAME_NOT_FOUND);
    }

    private static void deleteGame_notHost(Language language, UUID accessTokenId2, List<SavedGameResponse> savedGames) {
        Response deleteGame_notHostResponse = SkyXploreSavedGameActions.getDeleteGameResponse(language, accessTokenId2, savedGames.get(0).getGameId());
        ResponseValidator.verifyForbiddenOperation(language, deleteGame_notHostResponse);
    }

    private static void deleteGame(Language language, UUID accessTokenId1, List<SavedGameResponse> savedGames) {
        Response deleteGameResponse = SkyXploreSavedGameActions.getDeleteGameResponse(language, accessTokenId1, savedGames.get(0).getGameId());
        assertThat(deleteGameResponse.getStatusCode()).isEqualTo(200);

        savedGames = SkyXploreSavedGameActions.getSavedGames(language, accessTokenId1);
        assertThat(savedGames).isEmpty();

        ApphubWsClient.cleanUpConnections();
    }
}
