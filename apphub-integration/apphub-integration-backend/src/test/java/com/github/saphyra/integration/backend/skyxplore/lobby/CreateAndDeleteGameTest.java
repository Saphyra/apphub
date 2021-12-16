package com.github.saphyra.integration.backend.skyxplore.lobby;

import com.github.saphyra.apphub.integration.backend.BackEndTest;
import com.github.saphyra.apphub.integration.backend.ResponseValidator;
import com.github.saphyra.apphub.integration.backend.actions.IndexPageActions;
import com.github.saphyra.apphub.integration.backend.actions.skyxplore.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.backend.actions.skyxplore.SkyXploreFriendActions;
import com.github.saphyra.apphub.integration.backend.actions.skyxplore.SkyXploreLobbyActions;
import com.github.saphyra.apphub.integration.backend.actions.skyxplore.SkyXploreSavedGameActions;
import com.github.saphyra.apphub.integration.backend.model.skyxplore.SavedGameResponse;
import com.github.saphyra.apphub.integration.backend.model.skyxplore.SkyXploreCharacterModel;
import com.github.saphyra.apphub.integration.backend.ws.ApphubWsClient;
import com.github.saphyra.apphub.integration.backend.ws.model.WebSocketEvent;
import com.github.saphyra.apphub.integration.backend.ws.model.WebSocketEventName;
import com.github.saphyra.apphub.integration.common.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.common.framework.ErrorCode;
import com.github.saphyra.apphub.integration.common.framework.localization.Language;
import com.github.saphyra.apphub.integration.common.framework.localization.LocalizationKey;
import com.github.saphyra.apphub.integration.common.framework.localization.LocalizationProperties;
import com.github.saphyra.apphub.integration.common.model.ErrorResponse;
import com.github.saphyra.apphub.integration.common.model.RegistrationParameters;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class CreateAndDeleteGameTest extends BackEndTest {
    private static final String GAME_NAME = "game-name";

    @Test(dataProvider = "languageDataProvider", groups = "skyxplore")
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

        //Create game - Not host
        Response forbiddenResponse = SkyXploreLobbyActions.getStartGameResponse(language, accessTokenId2);
        assertThat(forbiddenResponse.getStatusCode()).isEqualTo(403);
        ErrorResponse forbiddenErrorResponse = forbiddenResponse.getBody().as(ErrorResponse.class);
        assertThat(forbiddenErrorResponse.getErrorCode()).isEqualTo(ErrorCode.FORBIDDEN_OPERATION.name());
        assertThat(forbiddenErrorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, LocalizationKey.FORBIDDEN_OPERATION));

        //Create game - Lobby member not ready
        Response notReadyResponse = SkyXploreLobbyActions.getStartGameResponse(language, accessTokenId1);
        assertThat(notReadyResponse.getStatusCode()).isEqualTo(412);
        ErrorResponse notReadyErrorResponse = notReadyResponse.getBody().as(ErrorResponse.class);
        assertThat(notReadyErrorResponse.getErrorCode()).isEqualTo(ErrorCode.LOBBY_MEMBER_NOT_READY.name());
        assertThat(notReadyErrorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, LocalizationKey.LOBBY_MEMBER_NOT_READY));

        //Create game
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

        //Verifying saved game
        List<SavedGameResponse> savedGames = SkyXploreSavedGameActions.getSavedGames(language, accessTokenId1);
        assertThat(savedGames).hasSize(1);
        assertThat(savedGames.get(0).getGameName()).isEqualTo(GAME_NAME);
        assertThat(savedGames.get(0).getPlayers()).isEqualTo(characterModel2.getName());

        //Delete game - Game not found
        Response deleteGame_gameNotFoundResponse = SkyXploreSavedGameActions.getDeleteGameResponse(language, accessTokenId1, UUID.randomUUID());
        ResponseValidator.verifyErrorResponse(language, deleteGame_gameNotFoundResponse, 404, ErrorCode.GAME_NOT_FOUND);

        //Delete game - Not host
        Response deleteGame_notHostResponse = SkyXploreSavedGameActions.getDeleteGameResponse(language, accessTokenId2, savedGames.get(0).getGameId());
        ResponseValidator.verifyForbiddenOperation(language, deleteGame_notHostResponse);

        //Delete game
        Response deleteGameResponse = SkyXploreSavedGameActions.getDeleteGameResponse(language, accessTokenId1, savedGames.get(0).getGameId());
        assertThat(deleteGameResponse.getStatusCode()).isEqualTo(200);

        savedGames = SkyXploreSavedGameActions.getSavedGames(language, accessTokenId1);
        assertThat(savedGames).isEmpty();

        ApphubWsClient.cleanUpConnections();
    }
}
