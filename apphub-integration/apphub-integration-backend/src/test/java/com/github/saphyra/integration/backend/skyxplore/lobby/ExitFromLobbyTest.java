package com.github.saphyra.integration.backend.skyxplore.lobby;

import com.github.saphyra.apphub.integration.backend.BackEndTest;
import com.github.saphyra.apphub.integration.backend.actions.skyxplore.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.backend.actions.skyxplore.SkyXploreFriendActions;
import com.github.saphyra.apphub.integration.backend.actions.skyxplore.SkyXploreLobbyActions;
import com.github.saphyra.apphub.integration.backend.model.skyxplore.ExitFromLobbyWsMessage;
import com.github.saphyra.apphub.integration.backend.model.skyxplore.SkyXploreCharacterModel;
import com.github.saphyra.apphub.integration.backend.ws.ApphubWsClient;
import com.github.saphyra.apphub.integration.backend.ws.model.WebSocketEvent;
import com.github.saphyra.apphub.integration.backend.ws.model.WebSocketEventName;
import com.github.saphyra.apphub.integration.common.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.common.framework.ErrorCode;
import com.github.saphyra.apphub.integration.common.framework.IndexPageActions;
import com.github.saphyra.apphub.integration.common.framework.localization.Language;
import com.github.saphyra.apphub.integration.common.framework.localization.LocalizationKey;
import com.github.saphyra.apphub.integration.common.framework.localization.LocalizationProperties;
import com.github.saphyra.apphub.integration.common.model.ErrorResponse;
import com.github.saphyra.apphub.integration.common.model.RegistrationParameters;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class ExitFromLobbyTest extends BackEndTest {
    private static final String GAME_NAME = "game-name";

    @Test
    public void memberLeftLobby() {
        Language language = Language.HUNGARIAN;
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

        ApphubWsClient wsClient = ApphubWsClient.createSkyXploreLobby(language, accessTokenId1);

        SkyXploreLobbyActions.exitFromLobby(language, accessTokenId2);

        WebSocketEvent event = wsClient.awaitForEvent(WebSocketEventName.SKYXPLORE_LOBBY_EXIT_FROM_LOBBY)
            .orElseThrow(() -> new RuntimeException("WebSocket event did not arrive"));

        ExitFromLobbyWsMessage message = event.getPayloadAs(ExitFromLobbyWsMessage.class);
        assertThat(message.getCharacterName()).isEqualTo(characterModel2.getName());
        assertThat(message.getUserId()).isEqualTo(userId2);
        assertThat(message.isHost()).isFalse();
    }

    @Test
    public void hostLeftTheLobby() {
        Language language = Language.HUNGARIAN;
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

        ApphubWsClient wsClient = ApphubWsClient.createSkyXploreLobby(language, accessTokenId2);

        SkyXploreLobbyActions.exitFromLobby(language, accessTokenId1);

        WebSocketEvent event = wsClient.awaitForEvent(WebSocketEventName.SKYXPLORE_LOBBY_EXIT_FROM_LOBBY)
            .orElseThrow(() -> new RuntimeException("WebSocket event did not arrive"));

        ExitFromLobbyWsMessage message = event.getPayloadAs(ExitFromLobbyWsMessage.class);
        assertThat(message.getCharacterName()).isEqualTo(characterModel1.getName());
        assertThat(message.getUserId()).isEqualTo(userId1);
        assertThat(message.isHost()).isTrue();

        Response response = SkyXploreLobbyActions.getLobbyMembersResponse(language, accessTokenId2);
        assertThat(response.getStatusCode()).isEqualTo(404);
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.LOBBY_NOT_FOUND.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, LocalizationKey.LOBBY_NOT_FOUND));
    }
}
