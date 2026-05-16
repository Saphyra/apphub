package com.github.saphyra.apphub.integration.backend.skyxplore.lobby;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreFriendActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreLobbyActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.DynamoDbUtil;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.LobbyPlayerResponse;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.SkyXploreCharacterModel;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.github.saphyra.apphub.integration.framework.ResponseValidator.verifyForbiddenOperation;
import static org.assertj.core.api.Assertions.assertThat;

public class AcceptInvitationTest extends BackEndTest {
    private static final String GAME_NAME = "game-name";

    @Test(groups = {"be", "skyxplore"})
    public void acceptInvitation() {
        RegistrationParameters userData1 = RegistrationParameters.validParameters();
        SkyXploreCharacterModel characterModel1 = SkyXploreCharacterModel.valid();
        String accessToken1 = IndexPageActions.registerAndLogin(getServerPort(), userData1);
        SkyXploreCharacterActions.createOrUpdateCharacter(getServerPort(), accessToken1, characterModel1);
        UUID userId1 = DynamoDbUtil.getUserIdByEmail(userData1.getEmail());

        RegistrationParameters userData2 = RegistrationParameters.validParameters();
        SkyXploreCharacterModel characterModel2 = SkyXploreCharacterModel.valid();
        String accessToken2 = IndexPageActions.registerAndLogin(getServerPort(), userData2);
        SkyXploreCharacterActions.createOrUpdateCharacter(getServerPort(), accessToken2, characterModel2);
        UUID userId2 = DynamoDbUtil.getUserIdByEmail(userData2.getEmail());

        RegistrationParameters userData3 = RegistrationParameters.validParameters();
        SkyXploreCharacterModel characterModel3 = SkyXploreCharacterModel.valid();
        String accessToken3 = IndexPageActions.registerAndLogin(getServerPort(), userData3);
        SkyXploreCharacterActions.createOrUpdateCharacter(getServerPort(), accessToken3, characterModel3);

        SkyXploreFriendActions.setUpFriendship(getServerPort(), accessToken1, accessToken2, userId2);
        SkyXploreLobbyActions.createLobby(getServerPort(), accessToken1, GAME_NAME);
        SkyXploreLobbyActions.inviteToLobby(getServerPort(), accessToken1, userId2);

        forbiddenOperation(userId1, accessToken3);
        acceptInvitation(userId1, accessToken2, userId2);
    }

    private static void forbiddenOperation(UUID userId1, String accessToken3) {
        Response forbiddenOperationResponse = SkyXploreLobbyActions.getAcceptInvitationResponse(getServerPort(), accessToken3, userId1);
        verifyForbiddenOperation(forbiddenOperationResponse);
    }

    private static void acceptInvitation(UUID userId1, String accessToken2, UUID userId2) {
        SkyXploreLobbyActions.acceptInvitation(getServerPort(), accessToken2, userId1);
        List<UUID> lobbyMembers = SkyXploreLobbyActions.getLobbyPlayers(getServerPort(), accessToken2)
            .stream()
            .map(LobbyPlayerResponse::getUserId)
            .collect(Collectors.toList());
        assertThat(lobbyMembers).containsExactlyInAnyOrder(userId1, userId2);
    }
}
