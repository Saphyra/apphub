package com.github.saphyra.apphub.integraton.backend.skyxplore.lobby;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreFriendActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreLobbyActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.ResponseValidator;
import com.github.saphyra.apphub.integration.localization.Language;
import com.github.saphyra.apphub.integration.structure.skyxplore.AiPlayer;
import com.github.saphyra.apphub.integration.structure.skyxplore.AllianceCreatedResponse;
import com.github.saphyra.apphub.integration.structure.skyxplore.LobbyMemberResponse;
import com.github.saphyra.apphub.integration.structure.skyxplore.SkyXploreCharacterModel;
import com.github.saphyra.apphub.integration.structure.user.RegistrationParameters;
import com.github.saphyra.apphub.integration.ws.ApphubWsClient;
import com.github.saphyra.apphub.integration.ws.model.WebSocketEventName;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class AllianceSettingsTest extends BackEndTest {
    private static final String GAME_NAME = "game-name";
    private static final String AI_NAME = "ai-name";

    @Test(groups = "skyxplore")
    void allianceCrud() {
        Language language = Language.ENGLISH;
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

        SkyXploreFriendActions.setUpFriendship(language, accessTokenId1, accessTokenId2, userId2);
        SkyXploreLobbyActions.inviteToLobby(language, accessTokenId1, userId2);
        SkyXploreLobbyActions.acceptInvitation(language, accessTokenId2, userId1);

        //Set alliance of player - not host
        Response response = SkyXploreLobbyActions.getChangeAllianceOfPlayerResponse(language, accessTokenId2, userId1, Constants.NEW_ALLIANCE_VALUE);

        ResponseValidator.verifyForbiddenOperation(response);

        //Set alliance of player - New alliance
        ApphubWsClient wsClient = ApphubWsClient.createSkyXploreLobby(language, accessTokenId1);

        SkyXploreLobbyActions.changeAllianceOfPlayer(language, accessTokenId1, userId1, Constants.NEW_ALLIANCE_VALUE);

        AllianceCreatedResponse allianceCreatedResponse = wsClient.awaitForEvent(WebSocketEventName.SKYXPLORE_LOBBY_ALLIANCE_CREATED)
            .orElseThrow()
            .getPayloadAs(AllianceCreatedResponse.class);
        assertThat(allianceCreatedResponse.getAlliance().getAllianceName()).isEqualTo("1");
        UUID allianceId1 = allianceCreatedResponse.getAlliance().getAllianceId();
        assertThat(allianceCreatedResponse.getMember().getAllianceId()).isEqualTo(allianceId1);
        assertThat(allianceCreatedResponse.getMember().getUserId()).isEqualTo(userId1);
        assertThat(allianceCreatedResponse.getAi()).isNull();

        //Set alliance of player - No alliance
        SkyXploreLobbyActions.changeAllianceOfPlayer(language, accessTokenId1, userId1, Constants.NO_ALLIANCE_VALUE);

        LobbyMemberResponse lobbyMemberResponse = wsClient.awaitForEvent(WebSocketEventName.SKYXPLORE_LOBBY_PLAYER_MODIFIED)
            .orElseThrow()
            .getPayloadAs(LobbyMemberResponse.class);
        assertThat(lobbyMemberResponse.getUserId()).isEqualTo(userId1);
        assertThat(lobbyMemberResponse.getAllianceId()).isNull();

        //Set alliance of player - Existing alliance
        wsClient.clearMessages();

        SkyXploreLobbyActions.changeAllianceOfPlayer(language, accessTokenId1, userId2, allianceId1);

        lobbyMemberResponse = wsClient.awaitForEvent(WebSocketEventName.SKYXPLORE_LOBBY_PLAYER_MODIFIED)
            .orElseThrow()
            .getPayloadAs(LobbyMemberResponse.class);
        assertThat(lobbyMemberResponse.getUserId()).isEqualTo(userId2);
        assertThat(lobbyMemberResponse.getAllianceId()).isEqualTo(allianceId1);

        //Set alliance of AI - not host
        SkyXploreLobbyActions.createOrModifyAi(language, accessTokenId1, AiPlayer.builder().name(AI_NAME).build());
        UUID aiId = wsClient.awaitForEvent(WebSocketEventName.SKYXPLORE_LOBBY_AI_MODIFIED)
            .orElseThrow()
            .getPayloadAs(AiPlayer.class)
            .getUserId();

        response = SkyXploreLobbyActions.getChangeAllianceOfAiResponse(language, accessTokenId2, aiId, Constants.NEW_ALLIANCE_VALUE);

        ResponseValidator.verifyForbiddenOperation(response);

        //Set alliance of AI - New alliance
        SkyXploreLobbyActions.changeAllianceOfAI(language, accessTokenId1, aiId, Constants.NEW_ALLIANCE_VALUE);

        allianceCreatedResponse = wsClient.awaitForEvent(WebSocketEventName.SKYXPLORE_LOBBY_ALLIANCE_CREATED)
            .orElseThrow()
            .getPayloadAs(AllianceCreatedResponse.class);
        assertThat(allianceCreatedResponse.getAlliance().getAllianceName()).isEqualTo("2");
        UUID allianceId2 = allianceCreatedResponse.getAlliance().getAllianceId();
        assertThat(allianceCreatedResponse.getMember()).isNull();
        assertThat(allianceCreatedResponse.getAi().getUserId()).isEqualTo(aiId);
        assertThat(allianceCreatedResponse.getAi().getAllianceId()).isEqualTo(allianceId2);

        //Set alliance of AI - No alliance
        wsClient.clearMessages();

        SkyXploreLobbyActions.changeAllianceOfAI(language, accessTokenId1, aiId, Constants.NO_ALLIANCE_VALUE);

        AiPlayer aiPlayer = wsClient.awaitForEvent(WebSocketEventName.SKYXPLORE_LOBBY_AI_MODIFIED)
            .orElseThrow()
            .getPayloadAs(AiPlayer.class);
        assertThat(aiPlayer.getUserId()).isEqualTo(aiId);
        assertThat(aiPlayer.getAllianceId()).isNull();

        //Set alliance of AI - Existing alliance
        wsClient.clearMessages();

        SkyXploreLobbyActions.changeAllianceOfAI(language, accessTokenId1, aiId, allianceId1);

        aiPlayer = wsClient.awaitForEvent(WebSocketEventName.SKYXPLORE_LOBBY_AI_MODIFIED)
            .orElseThrow()
            .getPayloadAs(AiPlayer.class);
        assertThat(aiPlayer.getUserId()).isEqualTo(aiId);
        assertThat(aiPlayer.getAllianceId()).isEqualTo(allianceId1);
    }
}
