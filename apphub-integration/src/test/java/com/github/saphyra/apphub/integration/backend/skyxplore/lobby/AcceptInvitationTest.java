package com.github.saphyra.apphub.integration.backend.skyxplore.lobby;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreFriendActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreLobbyActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
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
        UUID accessTokenId1 = IndexPageActions.registerAndLogin(userData1);
        SkyXploreCharacterActions.createOrUpdateCharacter(accessTokenId1, characterModel1);
        UUID userId1 = DatabaseUtil.getUserIdByEmail(userData1.getEmail());

        RegistrationParameters userData2 = RegistrationParameters.validParameters();
        SkyXploreCharacterModel characterModel2 = SkyXploreCharacterModel.valid();
        UUID accessTokenId2 = IndexPageActions.registerAndLogin(userData2);
        SkyXploreCharacterActions.createOrUpdateCharacter(accessTokenId2, characterModel2);
        UUID userId2 = DatabaseUtil.getUserIdByEmail(userData2.getEmail());

        RegistrationParameters userData3 = RegistrationParameters.validParameters();
        SkyXploreCharacterModel characterModel3 = SkyXploreCharacterModel.valid();
        UUID accessTokenId3 = IndexPageActions.registerAndLogin(userData3);
        SkyXploreCharacterActions.createOrUpdateCharacter(accessTokenId3, characterModel3);

        SkyXploreFriendActions.setUpFriendship(accessTokenId1, accessTokenId2, userId2);
        SkyXploreLobbyActions.createLobby(accessTokenId1, GAME_NAME);
        SkyXploreLobbyActions.inviteToLobby(accessTokenId1, userId2);

        forbiddenOperation(userId1, accessTokenId3);
        acceptInvitation(userId1, accessTokenId2, userId2);
    }

    private static void forbiddenOperation(UUID userId1, UUID accessTokenId3) {
        Response forbiddenOperationResponse = SkyXploreLobbyActions.getAcceptInvitationResponse(accessTokenId3, userId1);
        verifyForbiddenOperation(forbiddenOperationResponse);
    }

    private static void acceptInvitation(UUID userId1, UUID accessTokenId2, UUID userId2) {
        SkyXploreLobbyActions.acceptInvitation(accessTokenId2, userId1);
        List<UUID> lobbyMembers = SkyXploreLobbyActions.getLobbyPlayers(accessTokenId2)
            .stream()
            .map(LobbyPlayerResponse::getUserId)
            .collect(Collectors.toList());
        assertThat(lobbyMembers).containsExactlyInAnyOrder(userId1, userId2);
    }
}
