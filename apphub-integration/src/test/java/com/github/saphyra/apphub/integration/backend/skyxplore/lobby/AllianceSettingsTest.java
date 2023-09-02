package com.github.saphyra.apphub.integration.backend.skyxplore.lobby;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreFriendActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreLobbyActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.ErrorCode;
import com.github.saphyra.apphub.integration.framework.ResponseValidator;
import com.github.saphyra.apphub.integration.localization.Language;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.AiPlayer;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.AllianceCreatedResponse;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.LobbyMemberResponse;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.SkyXploreCharacterModel;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import com.github.saphyra.apphub.integration.ws.ApphubWsClient;
import com.github.saphyra.apphub.integration.ws.model.WebSocketEvent;
import com.github.saphyra.apphub.integration.ws.model.WebSocketEventName;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class AllianceSettingsTest extends BackEndTest {
    private static final String GAME_NAME = "game-name";
    private static final String AI_NAME = "ai-name";

    @Test(groups = {"be", "skyxplore"})
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

        ApphubWsClient wsClient = ApphubWsClient.createSkyXploreLobby(language, accessTokenId1);

        setAllianceOfPlayer_notHost(language, userId1, accessTokenId2);
        UUID allianceId1 = setAllianceOfPlayer_newAlliance(language, accessTokenId1, userId1, wsClient);
        setAllianceOfPlayer_noAlliance(language, accessTokenId1, userId1, wsClient);
        setAllianceOfPlayer_existingAlliance(language, accessTokenId1, userId2, wsClient, allianceId1);
        UUID aiId = setAllianceOfAi_notHost(language, accessTokenId1, accessTokenId2, wsClient);
        setAllianceOfAi_newAlliance(language, accessTokenId1, wsClient, aiId);
        setAllianceOfAi_noAlliance(language, accessTokenId1, wsClient, aiId);
        setAllianceOfAi_existingAlliance(language, accessTokenId1, wsClient, allianceId1, aiId);
    }

    private static void setAllianceOfPlayer_notHost(Language language, UUID userId1, UUID accessTokenId2) {
        Response response = SkyXploreLobbyActions.getChangeAllianceOfPlayerResponse(language, accessTokenId2, userId1, Constants.NEW_ALLIANCE_VALUE);

        ResponseValidator.verifyForbiddenOperation(response);
    }

    private static UUID setAllianceOfPlayer_newAlliance(Language language, UUID accessTokenId1, UUID userId1, ApphubWsClient wsClient) {
        SkyXploreLobbyActions.changeAllianceOfPlayer(language, accessTokenId1, userId1, Constants.NEW_ALLIANCE_VALUE);

        AllianceCreatedResponse allianceCreatedResponse = wsClient.awaitForEvent(WebSocketEventName.SKYXPLORE_LOBBY_ALLIANCE_CREATED)
            .orElseThrow()
            .getPayloadAs(AllianceCreatedResponse.class);
        assertThat(allianceCreatedResponse.getAlliance().getAllianceName()).isEqualTo("1");
        UUID allianceId1 = allianceCreatedResponse.getAlliance().getAllianceId();
        assertThat(allianceCreatedResponse.getMember().getAllianceId()).isEqualTo(allianceId1);
        assertThat(allianceCreatedResponse.getMember().getUserId()).isEqualTo(userId1);
        assertThat(allianceCreatedResponse.getAi()).isNull();
        return allianceId1;
    }

    private static void setAllianceOfPlayer_noAlliance(Language language, UUID accessTokenId1, UUID userId1, ApphubWsClient wsClient) {
        SkyXploreLobbyActions.changeAllianceOfPlayer(language, accessTokenId1, userId1, Constants.NO_ALLIANCE_VALUE);

        LobbyMemberResponse lobbyMemberResponse = wsClient.awaitForEvent(WebSocketEventName.SKYXPLORE_LOBBY_PLAYER_MODIFIED)
            .orElseThrow()
            .getPayloadAs(LobbyMemberResponse.class);
        assertThat(lobbyMemberResponse.getUserId()).isEqualTo(userId1);
        assertThat(lobbyMemberResponse.getAllianceId()).isNull();
    }

    private static void setAllianceOfPlayer_existingAlliance(Language language, UUID accessTokenId1, UUID userId2, ApphubWsClient wsClient, UUID allianceId1) {
        LobbyMemberResponse lobbyMemberResponse;
        wsClient.clearMessages();

        SkyXploreLobbyActions.changeAllianceOfPlayer(language, accessTokenId1, userId2, allianceId1);

        lobbyMemberResponse = wsClient.awaitForEvent(WebSocketEventName.SKYXPLORE_LOBBY_PLAYER_MODIFIED)
            .orElseThrow()
            .getPayloadAs(LobbyMemberResponse.class);
        assertThat(lobbyMemberResponse.getUserId()).isEqualTo(userId2);
        assertThat(lobbyMemberResponse.getAllianceId()).isEqualTo(allianceId1);
    }

    private static UUID setAllianceOfAi_notHost(Language language, UUID accessTokenId1, UUID accessTokenId2, ApphubWsClient wsClient) {
        Response response;
        SkyXploreLobbyActions.createOrModifyAi(language, accessTokenId1, AiPlayer.builder().name(AI_NAME).build());
        UUID aiId = wsClient.awaitForEvent(WebSocketEventName.SKYXPLORE_LOBBY_AI_MODIFIED)
            .orElseThrow()
            .getPayloadAs(AiPlayer.class)
            .getUserId();

        response = SkyXploreLobbyActions.getChangeAllianceOfAiResponse(language, accessTokenId2, aiId, Constants.NEW_ALLIANCE_VALUE);

        ResponseValidator.verifyForbiddenOperation(response);
        return aiId;
    }

    private static void setAllianceOfAi_newAlliance(Language language, UUID accessTokenId1, ApphubWsClient wsClient, UUID aiId) {
        AllianceCreatedResponse allianceCreatedResponse;
        SkyXploreLobbyActions.changeAllianceOfAI(language, accessTokenId1, aiId, Constants.NEW_ALLIANCE_VALUE);

        allianceCreatedResponse = wsClient.awaitForEvent(WebSocketEventName.SKYXPLORE_LOBBY_ALLIANCE_CREATED)
            .orElseThrow()
            .getPayloadAs(AllianceCreatedResponse.class);
        assertThat(allianceCreatedResponse.getAlliance().getAllianceName()).isEqualTo("2");
        UUID allianceId2 = allianceCreatedResponse.getAlliance().getAllianceId();
        assertThat(allianceCreatedResponse.getMember()).isNull();
        assertThat(allianceCreatedResponse.getAi().getUserId()).isEqualTo(aiId);
        assertThat(allianceCreatedResponse.getAi().getAllianceId()).isEqualTo(allianceId2);
    }

    private static void setAllianceOfAi_noAlliance(Language language, UUID accessTokenId1, ApphubWsClient wsClient, UUID aiId) {
        wsClient.clearMessages();

        SkyXploreLobbyActions.changeAllianceOfAI(language, accessTokenId1, aiId, Constants.NO_ALLIANCE_VALUE);

        AiPlayer aiPlayer = wsClient.awaitForEvent(WebSocketEventName.SKYXPLORE_LOBBY_AI_MODIFIED)
            .orElseThrow()
            .getPayloadAs(AiPlayer.class);
        assertThat(aiPlayer.getUserId()).isEqualTo(aiId);
        assertThat(aiPlayer.getAllianceId()).isNull();
    }

    private static void setAllianceOfAi_existingAlliance(Language language, UUID accessTokenId1, ApphubWsClient wsClient, UUID allianceId1, UUID aiId) {
        AiPlayer aiPlayer;
        wsClient.clearMessages();

        SkyXploreLobbyActions.changeAllianceOfAI(language, accessTokenId1, aiId, allianceId1);

        aiPlayer = wsClient.awaitForEvent(WebSocketEventName.SKYXPLORE_LOBBY_AI_MODIFIED)
            .orElseThrow()
            .getPayloadAs(AiPlayer.class);
        assertThat(aiPlayer.getUserId()).isEqualTo(aiId);
        assertThat(aiPlayer.getAllianceId()).isEqualTo(allianceId1);
    }

    @Test(groups = {"be", "skyxplore"})
    void gameDoesNotStartWithOneAlliance() {
        Language language = Language.ENGLISH;
        RegistrationParameters userData1 = RegistrationParameters.validParameters();
        SkyXploreCharacterModel characterModel1 = SkyXploreCharacterModel.valid();
        UUID accessTokenId1 = IndexPageActions.registerAndLogin(language, userData1);
        SkyXploreCharacterActions.createOrUpdateCharacter(language, accessTokenId1, characterModel1);
        UUID userId1 = DatabaseUtil.getUserIdByEmail(userData1.getEmail());

        SkyXploreLobbyActions.createLobby(language, accessTokenId1, GAME_NAME);

        ApphubWsClient wsClient = ApphubWsClient.createSkyXploreLobby(language, accessTokenId1);

        SkyXploreLobbyActions.createOrModifyAi(language, accessTokenId1, AiPlayer.builder().name(AI_NAME).build());

        UUID aiId = wsClient.awaitForEvent(WebSocketEventName.SKYXPLORE_LOBBY_AI_MODIFIED)
            .orElseThrow()
            .getPayloadAs(AiPlayer.class)
            .getUserId();

        SkyXploreLobbyActions.changeAllianceOfPlayer(language, accessTokenId1, userId1, Constants.NEW_ALLIANCE_VALUE);

        UUID allianceId = wsClient.awaitForEvent(WebSocketEventName.SKYXPLORE_LOBBY_ALLIANCE_CREATED)
            .orElseThrow()
            .getPayloadAs(AllianceCreatedResponse.class)
            .getAlliance()
            .getAllianceId();

        SkyXploreLobbyActions.changeAllianceOfAI(language, accessTokenId1, aiId, allianceId);

        wsClient.clearMessages();

        WebSocketEvent readyEvent = WebSocketEvent.builder()
            .eventName(WebSocketEventName.SKYXPLORE_LOBBY_SET_READINESS)
            .payload(true)
            .build();

        wsClient.send(readyEvent);

        wsClient.awaitForEvent(WebSocketEventName.SKYXPLORE_LOBBY_PLAYER_MODIFIED)
            .orElseThrow();

        Response response = SkyXploreLobbyActions.getStartGameResponse(language, accessTokenId1);

        ResponseValidator.verifyErrorResponse(response, 412, ErrorCode.NOT_ENOUGH_ALLIANCES);
    }
}
