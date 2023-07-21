package com.github.saphyra.apphub.integraton.backend.skyxplore.lobby;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreFriendActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreLobbyActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.ErrorCode;
import com.github.saphyra.apphub.integration.framework.ResponseValidator;
import com.github.saphyra.apphub.integration.localization.Language;
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

    @Test(groups = "skyxplore")
    void aiCrud() {
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

        //Create - null name
        AiPlayer aiPlayer = AiPlayer.builder()
            .name(null)
            .build();
        Response response = SkyXploreLobbyActions.getCreateOrModifyAiResponse(language, accessTokenId1, aiPlayer);

        ResponseValidator.verifyInvalidParam(response, "name", "must not be null");

        //Create - too short name
        aiPlayer = AiPlayer.builder()
            .name("aa")
            .build();
        response = SkyXploreLobbyActions.getCreateOrModifyAiResponse(language, accessTokenId1, aiPlayer);

        ResponseValidator.verifyInvalidParam(response, "name", "too short");

        //Create - too long name
        aiPlayer = AiPlayer.builder()
            .name(Stream.generate(() -> "a").limit(31).collect(Collectors.joining()))
            .build();
        response = SkyXploreLobbyActions.getCreateOrModifyAiResponse(language, accessTokenId1, aiPlayer);

        ResponseValidator.verifyInvalidParam(response, "name", "too long");

        //Create - alliance does not exist
        aiPlayer = AiPlayer.builder()
            .name(AI_NAME)
            .allianceId(UUID.randomUUID())
            .build();
        response = SkyXploreLobbyActions.getCreateOrModifyAiResponse(language, accessTokenId1, aiPlayer);

        ResponseValidator.verifyInvalidParam(response, "allianceId", "does not exist");

        //Create by not host
        response = SkyXploreLobbyActions.getCreateOrModifyAiResponse(language, accessTokenId2, AI_PLAYER);

        ResponseValidator.verifyForbiddenOperation(response);

        ApphubWsClient wsClient = ApphubWsClient.createSkyXploreLobby(language, accessTokenId1);

        //Create
        Stream.generate(() -> AI_PLAYER)
            .limit(10)
            .forEach(ai -> SkyXploreLobbyActions.createOrModifyAi(language, accessTokenId1, ai));

        assertThat(wsClient.awaitForEvent(WebSocketEventName.SKYXPLORE_LOBBY_AI_MODIFIED)).isNotEmpty();

        //Create - exceed limit
        response = SkyXploreLobbyActions.getCreateOrModifyAiResponse(language, accessTokenId1, AI_PLAYER);

        ResponseValidator.verifyErrorResponse(response, 400, ErrorCode.TOO_MANY_AIS);

        //Rename
        wsClient.clearMessages();
        UUID aiId = findRandomAi(language, accessTokenId1);
        aiPlayer = AiPlayer.builder()
            .userId(aiId)
            .name(NEW_AI_NAME)
            .build();
        SkyXploreLobbyActions.createOrModifyAi(language, accessTokenId1, aiPlayer);

        assertThat(findAiById(language, accessTokenId1, aiId).getName()).isEqualTo(NEW_AI_NAME);

        aiPlayer = wsClient.awaitForEvent(WebSocketEventName.SKYXPLORE_LOBBY_AI_MODIFIED)
            .orElseThrow()
            .getPayloadAs(AiPlayer.class);
        assertThat(aiPlayer.getName()).isEqualTo(NEW_AI_NAME);

        //Remove
        SkyXploreLobbyActions.removeAi(language, accessTokenId1, aiId);

        assertThat(SkyXploreLobbyActions.getAis(language, accessTokenId1)).hasSize(9);

        UUID removedPlayerId = wsClient.awaitForEvent(WebSocketEventName.SKYXPLORE_LOBBY_AI_REMOVED)
            .orElseThrow()
            .getPayloadAs(UUID.class);
        assertThat(removedPlayerId).isEqualTo(aiId);
    }

    private UUID findRandomAi(Language language, UUID accessTokenId) {
        return SkyXploreLobbyActions.getAis(language, accessTokenId)
            .stream()
            .findAny()
            .map(AiPlayer::getUserId)
            .orElseThrow(() -> new RuntimeException("No Ai found"));
    }

    private AiPlayer findAiById(Language language, UUID accessTokenId1, UUID aiId) {
        return SkyXploreLobbyActions.getAis(language, accessTokenId1)
            .stream()
            .filter(aiPlayer -> aiPlayer.getUserId().equals(aiId))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Ai not found with id " + aiId));
    }
}
