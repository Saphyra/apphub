package com.github.saphyra.integration.backend.skyxplore.lobby;

import com.github.saphyra.apphub.integration.backend.BackEndTest;
import com.github.saphyra.apphub.integration.backend.actions.skyxplore.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.backend.actions.skyxplore.SkyXploreFriendActions;
import com.github.saphyra.apphub.integration.backend.actions.skyxplore.SkyXploreLobbyActions;
import com.github.saphyra.apphub.integration.backend.model.skyxplore.LobbyInvitationWsMessage;
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

public class InviteToLobbyTest extends BackEndTest {
    private static final String GAME_NAME = "game-name";

    @Test(dataProvider = "localeDataProvider")
    public void invite(Language language) {
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

        SkyXploreLobbyActions.createLobby(language, accessTokenId1, GAME_NAME);

        Response notFriendsResponse = SkyXploreLobbyActions.getInviteToLobbyResponse(language, accessTokenId1, userId2);
        assertThat(notFriendsResponse.getStatusCode()).isEqualTo(412);

        SkyXploreFriendActions.setUpFriendship(language, accessTokenId1, accessTokenId2, userId2);

        ApphubWsClient wsClient = ApphubWsClient.createSkyXploreMainMenu(language, accessTokenId2);

        SkyXploreLobbyActions.inviteToLobby(language, accessTokenId1, userId2);

        WebSocketEvent event = wsClient.awaitForEvent(WebSocketEventName.SKYXPLORE_MAIN_MENU_INVITATION)
            .orElseThrow(() -> new RuntimeException("Invitation not arrived"));
        LobbyInvitationWsMessage payload = event.getPayloadAs(LobbyInvitationWsMessage.class);
        assertThat(payload.getSenderId()).isEqualTo(userId1);
        assertThat(payload.getSenderName()).isEqualTo(characterModel1.getName());

        Response floodingResponse = SkyXploreLobbyActions.getInviteToLobbyResponse(language, accessTokenId1, userId2);
        assertThat(floodingResponse.getStatusCode()).isEqualTo(429);
        ErrorResponse errorResponse = floodingResponse.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.TOO_FREQUENT_INVITATIONS.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, LocalizationKey.TOO_FREQUENT_INVITATIONS));

    }
}
