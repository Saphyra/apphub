package com.github.saphyra.apphub.integration.backend.skyxplore.lobby;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreFriendActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreLobbyActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.ErrorCode;
import com.github.saphyra.apphub.integration.framework.ResponseValidator;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.AiPlayer;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.SkyXploreCharacterModel;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import com.github.saphyra.apphub.integration.ws.ApphubWsClient;
import com.github.saphyra.apphub.integration.ws.model.WebSocketEventName;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class AiSettingsTest extends BackEndTest {
    private static final String GAME_NAME = "game-name";
    private static final String AI_NAME = "ai-name";
    private static final AiPlayer AI_PLAYER = AiPlayer.builder()
        .name(AI_NAME)
        .build();
    private static final String NEW_AI_NAME = "new-ai-name";

    @Test(groups = {"be", "skyxplore"})
    void aiCrud() {
        RegistrationParameters userData1 = RegistrationParameters.validParameters();
        SkyXploreCharacterModel characterModel1 = SkyXploreCharacterModel.valid();
        UUID accessTokenId1 = IndexPageActions.registerAndLogin(getServerPort(), userData1);
        SkyXploreCharacterActions.createOrUpdateCharacter(getServerPort(), accessTokenId1, characterModel1);
        UUID userId1 = DatabaseUtil.getUserIdByEmail(userData1.getEmail());

        RegistrationParameters userData2 = RegistrationParameters.validParameters();
        SkyXploreCharacterModel characterModel2 = SkyXploreCharacterModel.valid();
        UUID accessTokenId2 = IndexPageActions.registerAndLogin(getServerPort(), userData2);
        SkyXploreCharacterActions.createOrUpdateCharacter(getServerPort(), accessTokenId2, characterModel2);
        UUID userId2 = DatabaseUtil.getUserIdByEmail(userData2.getEmail());

        SkyXploreLobbyActions.createLobby(getServerPort(), accessTokenId1, GAME_NAME);

        SkyXploreFriendActions.setUpFriendship(getServerPort(), accessTokenId1, accessTokenId2, userId2);
        SkyXploreLobbyActions.inviteToLobby(getServerPort(), accessTokenId1, userId2);
        SkyXploreLobbyActions.acceptInvitation(getServerPort(), accessTokenId2, userId1);

        create_nullName(accessTokenId1);
        create_tooShortName(accessTokenId1);
        create_tooLongName(accessTokenId1);
        create_allianceDoesNotExist(accessTokenId1);
        ApphubWsClient wsClient = createByNotHost(accessTokenId1, accessTokenId2);
        create(accessTokenId1, wsClient);
        create_exceedLimit(accessTokenId1);
        UUID aiId = rename(accessTokenId1, wsClient);
        remove(accessTokenId1, wsClient, aiId);
    }

    private static void create_nullName(UUID accessTokenId1) {
        AiPlayer aiPlayer = AiPlayer.builder()
            .name(null)
            .build();
        Response response = SkyXploreLobbyActions.getCreateOrModifyAiResponse(getServerPort(), accessTokenId1, aiPlayer);

        ResponseValidator.verifyInvalidParam(response, "name", "must not be null");
    }

    private static void create_tooShortName(UUID accessTokenId1) {
        Response response;
        AiPlayer aiPlayer;
        aiPlayer = AiPlayer.builder()
            .name("aa")
            .build();
        response = SkyXploreLobbyActions.getCreateOrModifyAiResponse(getServerPort(), accessTokenId1, aiPlayer);

        ResponseValidator.verifyInvalidParam(response, "name", "too short");
    }

    private static void create_tooLongName(UUID accessTokenId1) {
        Response response;
        AiPlayer aiPlayer;
        aiPlayer = AiPlayer.builder()
            .name(Stream.generate(() -> "a").limit(31).collect(Collectors.joining()))
            .build();
        response = SkyXploreLobbyActions.getCreateOrModifyAiResponse(getServerPort(), accessTokenId1, aiPlayer);

        ResponseValidator.verifyInvalidParam(response, "name", "too long");
    }

    private static void create_allianceDoesNotExist(UUID accessTokenId1) {
        Response response;
        AiPlayer aiPlayer;
        aiPlayer = AiPlayer.builder()
            .name(AI_NAME)
            .allianceId(UUID.randomUUID())
            .build();
        response = SkyXploreLobbyActions.getCreateOrModifyAiResponse(getServerPort(), accessTokenId1, aiPlayer);

        ResponseValidator.verifyInvalidParam(response, "allianceId", "does not exist");
    }

    private static ApphubWsClient createByNotHost(UUID accessTokenId1, UUID accessTokenId2) {
        Response response;
        response = SkyXploreLobbyActions.getCreateOrModifyAiResponse(getServerPort(), accessTokenId2, AI_PLAYER);

        ResponseValidator.verifyForbiddenOperation(response);

        return ApphubWsClient.createSkyXploreLobby(getServerPort(),
            accessTokenId1, accessTokenId1);
    }

    private static void create(UUID accessTokenId1, ApphubWsClient wsClient) {
        Stream.generate(() -> AI_PLAYER)
            .limit(10)
            .forEach(ai -> SkyXploreLobbyActions.createOrModifyAi(getServerPort(), accessTokenId1, ai));

        assertThat(wsClient.awaitForEvent(WebSocketEventName.SKYXPLORE_LOBBY_AI_MODIFIED)).isNotEmpty();
    }

    private static void create_exceedLimit(UUID accessTokenId1) {
        Response response;
        response = SkyXploreLobbyActions.getCreateOrModifyAiResponse(getServerPort(), accessTokenId1, AI_PLAYER);

        ResponseValidator.verifyErrorResponse(response, 400, ErrorCode.TOO_MANY_AIS);
    }

    private UUID rename(UUID accessTokenId1, ApphubWsClient wsClient) {
        AiPlayer aiPlayer;
        wsClient.clearMessages();
        UUID aiId = findRandomAi(accessTokenId1);
        aiPlayer = AiPlayer.builder()
            .userId(aiId)
            .name(NEW_AI_NAME)
            .build();
        SkyXploreLobbyActions.createOrModifyAi(getServerPort(), accessTokenId1, aiPlayer);

        assertThat(findAiById(accessTokenId1, aiId).getName()).isEqualTo(NEW_AI_NAME);

        aiPlayer = wsClient.awaitForEvent(WebSocketEventName.SKYXPLORE_LOBBY_AI_MODIFIED)
            .orElseThrow()
            .getPayloadAs(AiPlayer.class);
        assertThat(aiPlayer.getName()).isEqualTo(NEW_AI_NAME);
        return aiId;
    }

    private static void remove(UUID accessTokenId1, ApphubWsClient wsClient, UUID aiId) {
        SkyXploreLobbyActions.removeAi(getServerPort(), accessTokenId1, aiId);

        assertThat(SkyXploreLobbyActions.getAis(getServerPort(), accessTokenId1)).hasSize(9);

        UUID removedPlayerId = wsClient.awaitForEvent(WebSocketEventName.SKYXPLORE_LOBBY_AI_REMOVED)
            .orElseThrow()
            .getPayloadAs(UUID.class);
        assertThat(removedPlayerId).isEqualTo(aiId);
    }

    private UUID findRandomAi(UUID accessTokenId) {
        return SkyXploreLobbyActions.getAis(getServerPort(), accessTokenId)
            .stream()
            .findAny()
            .map(AiPlayer::getUserId)
            .orElseThrow(() -> new RuntimeException("No Ai found"));
    }

    private AiPlayer findAiById(UUID accessTokenId1, UUID aiId) {
        return SkyXploreLobbyActions.getAis(getServerPort(), accessTokenId1)
            .stream()
            .filter(aiPlayer -> aiPlayer.getUserId().equals(aiId))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Ai not found with id " + aiId));
    }
}
