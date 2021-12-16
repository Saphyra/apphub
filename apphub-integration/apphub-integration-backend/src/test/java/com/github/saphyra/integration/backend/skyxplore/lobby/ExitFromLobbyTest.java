package com.github.saphyra.integration.backend.skyxplore.lobby;

import com.github.saphyra.apphub.integration.backend.BackEndTest;
import com.github.saphyra.apphub.integration.backend.actions.IndexPageActions;
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

    @Test(groups = "skyxplore")
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

        RegistrationParameters userData3 = RegistrationParameters.validParameters();
        SkyXploreCharacterModel characterModel3 = SkyXploreCharacterModel.valid();
        UUID accessTokenId3 = IndexPageActions.registerAndLogin(language, userData3);
        SkyXploreCharacterActions.createOrUpdateCharacter(language, accessTokenId3, characterModel3);
        UUID userId3 = DatabaseUtil.getUserIdByEmail(userData3.getEmail());

        RegistrationParameters userData4 = RegistrationParameters.validParameters();
        SkyXploreCharacterModel characterModel4 = SkyXploreCharacterModel.valid();
        UUID accessTokenId4 = IndexPageActions.registerAndLogin(language, userData4);
        SkyXploreCharacterActions.createOrUpdateCharacter(language, accessTokenId4, characterModel4);
        UUID userId4 = DatabaseUtil.getUserIdByEmail(userData4.getEmail());

        SkyXploreFriendActions.setUpFriendship(language, accessTokenId1, accessTokenId2, userId2);
        SkyXploreFriendActions.setUpFriendship(language, accessTokenId1, accessTokenId3, userId3);
        SkyXploreFriendActions.setUpFriendship(language, accessTokenId3, accessTokenId4, userId4);
        SkyXploreFriendActions.setUpFriendship(language, accessTokenId2, accessTokenId4, userId4);

        SkyXploreLobbyActions.createLobby(language, accessTokenId1, GAME_NAME);

        SkyXploreLobbyActions.inviteToLobby(language, accessTokenId1, userId2);
        SkyXploreLobbyActions.inviteToLobby(language, accessTokenId1, userId3);
        SkyXploreLobbyActions.acceptInvitation(language, accessTokenId2, userId1);
        SkyXploreLobbyActions.acceptInvitation(language, accessTokenId3, userId1);

        ApphubWsClient wsClient = ApphubWsClient.createSkyXploreLobby(language, accessTokenId2);

        //Member left
        SkyXploreLobbyActions.inviteToLobby(language, accessTokenId3, userId4);
        ApphubWsClient mainMenuClient = ApphubWsClient.createSkyXploreMainMenu(language, accessTokenId4);

        SkyXploreLobbyActions.exitFromLobby(language, accessTokenId3);
        WebSocketEvent memberLeftEvent = wsClient.awaitForEvent(WebSocketEventName.SKYXPLORE_LOBBY_EXIT_FROM_LOBBY)
            .orElseThrow(() -> new RuntimeException("WebSocket event did not arrive"));
        ExitFromLobbyWsMessage memberLeftMessage = memberLeftEvent.getPayloadAs(ExitFromLobbyWsMessage.class);
        assertThat(memberLeftMessage.getCharacterName()).isEqualTo(characterModel3.getName());
        assertThat(memberLeftMessage.getUserId()).isEqualTo(userId3);
        assertThat(memberLeftMessage.isHost()).isFalse();

        WebSocketEvent rejectInvitationEvent = mainMenuClient.awaitForEvent(WebSocketEventName.SKYXPLORE_MAIN_MENU_CANCEL_INVITATION)
            .orElseThrow(() -> new RuntimeException("RejectInvitation event did not arrive."));
        assertThat(rejectInvitationEvent.getPayload()).isEqualTo(userId3.toString());

        wsClient.clearMessages();
        mainMenuClient.clearMessages();

        //Host left
        SkyXploreLobbyActions.inviteToLobby(language, accessTokenId2, userId4);

        SkyXploreLobbyActions.exitFromLobby(language, accessTokenId1);
        WebSocketEvent hostLeftEvent = wsClient.awaitForEvent(WebSocketEventName.SKYXPLORE_LOBBY_EXIT_FROM_LOBBY)
            .orElseThrow(() -> new RuntimeException("WebSocket event did not arrive"));
        ExitFromLobbyWsMessage hostLeftMessage = hostLeftEvent.getPayloadAs(ExitFromLobbyWsMessage.class);
        assertThat(hostLeftMessage.getCharacterName()).isEqualTo(characterModel1.getName());
        assertThat(hostLeftMessage.getUserId()).isEqualTo(userId1);
        assertThat(hostLeftMessage.isHost()).isTrue();
        Response response = SkyXploreLobbyActions.getLobbyMembersResponse(language, accessTokenId2);
        assertThat(response.getStatusCode()).isEqualTo(404);
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.LOBBY_NOT_FOUND.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, LocalizationKey.LOBBY_NOT_FOUND));

        rejectInvitationEvent = mainMenuClient.awaitForEvent(WebSocketEventName.SKYXPLORE_MAIN_MENU_CANCEL_INVITATION)
            .orElseThrow(() -> new RuntimeException("RejectInvitation event did not arrive."));
        assertThat(rejectInvitationEvent.getPayload()).isEqualTo(userId2.toString());

        ApphubWsClient.cleanUpConnections();
    }
}
