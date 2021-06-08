package com.github.saphyra.integration.backend.skyxplore.lobby;

import com.github.saphyra.apphub.integration.backend.BackEndTest;
import com.github.saphyra.apphub.integration.backend.actions.skyxplore.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.backend.actions.skyxplore.SkyXploreFriendActions;
import com.github.saphyra.apphub.integration.backend.actions.skyxplore.SkyXploreLobbyActions;
import com.github.saphyra.apphub.integration.backend.model.skyxplore.LobbyMemberResponse;
import com.github.saphyra.apphub.integration.backend.model.skyxplore.LobbyMembersResponse;
import com.github.saphyra.apphub.integration.backend.model.skyxplore.SkyXploreCharacterModel;
import com.github.saphyra.apphub.integration.common.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.common.framework.ErrorCode;
import com.github.saphyra.apphub.integration.backend.actions.IndexPageActions;
import com.github.saphyra.apphub.integration.common.framework.localization.Language;
import com.github.saphyra.apphub.integration.common.framework.localization.LocalizationKey;
import com.github.saphyra.apphub.integration.common.framework.localization.LocalizationProperties;
import com.github.saphyra.apphub.integration.common.model.ErrorResponse;
import com.github.saphyra.apphub.integration.common.model.RegistrationParameters;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class AcceptInvitationTest extends BackEndTest {
    private static final String GAME_NAME = "game-name";

    @Test(dataProvider = "languageDataProvider", groups = "skyxplore")
    public void forbiddenOperation(Language language) {
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

        SkyXploreFriendActions.setUpFriendship(language, accessTokenId1, accessTokenId2, userId2);
        SkyXploreLobbyActions.createLobby(language, accessTokenId1, GAME_NAME);
        SkyXploreLobbyActions.inviteToLobby(language, accessTokenId1, userId2);

        //Forbidden operation
        Response forbiddenOperationResponse = SkyXploreLobbyActions.getAcceptInvitationResponse(language, accessTokenId3, userId1);
        verifyForbiddenOperation(language, forbiddenOperationResponse);

        //Accept invitation
        SkyXploreLobbyActions.acceptInvitation(language, accessTokenId2, userId1);
        LobbyMembersResponse lobbyMembers = SkyXploreLobbyActions.getLobbyMembers(language, accessTokenId2);
        assertThat(lobbyMembers.getHost()).isEqualTo(LobbyMemberResponse.builder().userId(userId1).characterName(characterModel1.getName()).build());
        assertThat(lobbyMembers.getMembers()).containsExactly(LobbyMemberResponse.builder().userId(userId2).characterName(characterModel2.getName()).build());
    }

    private void verifyForbiddenOperation(Language language, Response response) {
        assertThat(response.getStatusCode()).isEqualTo(403);
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.FORBIDDEN_OPERATION.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, LocalizationKey.FORBIDDEN_OPERATION));
    }
}
