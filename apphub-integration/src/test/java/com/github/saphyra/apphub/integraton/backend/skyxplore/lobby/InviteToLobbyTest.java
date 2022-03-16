package com.github.saphyra.apphub.integraton.backend.skyxplore.lobby;

import com.github.saphyra.apphub.integration.BackEndTest;
import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreFriendActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreLobbyActions;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.ErrorCode;
import com.github.saphyra.apphub.integration.localization.Language;
import com.github.saphyra.apphub.integration.localization.LocalizationKey;
import com.github.saphyra.apphub.integration.localization.LocalizationProperties;
import com.github.saphyra.apphub.integration.structure.ErrorResponse;
import com.github.saphyra.apphub.integration.structure.skyxplore.LobbyInvitationWsMessage;
import com.github.saphyra.apphub.integration.structure.skyxplore.SkyXploreCharacterModel;
import com.github.saphyra.apphub.integration.structure.user.RegistrationParameters;
import com.github.saphyra.apphub.integration.ws.ApphubWsClient;
import com.github.saphyra.apphub.integration.ws.model.WebSocketEvent;
import com.github.saphyra.apphub.integration.ws.model.WebSocketEventName;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class InviteToLobbyTest extends BackEndTest {
    private static final String GAME_NAME = "game-name";

    @Test(dataProvider = "languageDataProvider", groups = "skyxplore")
    public void inviteToLobby(Language language) {
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

        SkyXploreLobbyActions.createLobby(language, accessTokenId1, GAME_NAME);

        SkyXploreFriendActions.setUpFriendship(language, accessTokenId1, accessTokenId3, userId3);
        SkyXploreFriendActions.setUpFriendship(language, accessTokenId2, accessTokenId3, userId3);
        SkyXploreLobbyActions.inviteToLobby(language, accessTokenId1, userId3);
        SkyXploreLobbyActions.acceptInvitation(language, accessTokenId3, userId1);

        //Not friends
        Response notFriendsResponse = SkyXploreLobbyActions.getInviteToLobbyResponse(language, accessTokenId1, userId2);
        assertThat(notFriendsResponse.getStatusCode()).isEqualTo(412);

        //Invitation arrived
        SkyXploreFriendActions.setUpFriendship(language, accessTokenId1, accessTokenId2, userId2);
        ApphubWsClient wsClient = ApphubWsClient.createSkyXploreMainMenu(language, accessTokenId2);

        SkyXploreLobbyActions.inviteToLobby(language, accessTokenId1, userId2);
        WebSocketEvent event = wsClient.awaitForEvent(WebSocketEventName.SKYXPLORE_MAIN_MENU_INVITATION)
            .orElseThrow(() -> new RuntimeException("Invitation not arrived"));
        LobbyInvitationWsMessage payload = event.getPayloadAs(LobbyInvitationWsMessage.class);
        assertThat(payload.getSenderId()).isEqualTo(userId1);
        assertThat(payload.getSenderName()).isEqualTo(characterModel1.getName());

        //Flooding
        Response floodingResponse = SkyXploreLobbyActions.getInviteToLobbyResponse(language, accessTokenId1, userId2);
        assertThat(floodingResponse.getStatusCode()).isEqualTo(429);
        ErrorResponse errorResponse = floodingResponse.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.TOO_FREQUENT_INVITATIONS.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, LocalizationKey.TOO_FREQUENT_INVITATIONS));

        //Invite by different player
        wsClient.clearMessages();
        SkyXploreLobbyActions.inviteToLobby(language, accessTokenId3, userId2);
        event = wsClient.awaitForEvent(WebSocketEventName.SKYXPLORE_MAIN_MENU_INVITATION)
            .orElseThrow(() -> new RuntimeException("Invitation not arrived"));
        payload = event.getPayloadAs(LobbyInvitationWsMessage.class);
        assertThat(payload.getSenderId()).isEqualTo(userId3);
        assertThat(payload.getSenderName()).isEqualTo(characterModel3.getName());

        ApphubWsClient.cleanUpConnections();
    }
}