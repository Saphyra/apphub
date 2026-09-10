package com.github.saphyra.apphub.integration.backend.skyxplore.lobby;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreLobbyActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.CommonUtils;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.db.dynamodb.UserDynamoDbRepository;
import com.github.saphyra.apphub.integration.structure.api.authorization.TokenResponse;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.AiPlayer;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.SkyXploreGameSettings;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.UUID;

public class SkyXploreLobbyRoleProtectionTest extends BackEndTest {
    @Test(dataProvider = "roleProvider", groups = {"be", "skyxplore", "role-protection"})
    public void lobbyRoleProtection(String role) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(getServerPort(), userData.toRegistrationRequest());
        UserDynamoDbRepository.removeRoleByEmail(userData.getEmail(), role);
        TokenResponse tokenResponse = IndexPageActions.login(getServerPort(), userData.toLoginRequest());
        String accessToken = tokenResponse.getAccessToken()
            .getJwt();

        //Platform
        CommonUtils.verifyMissingRole(() -> SkyXploreLobbyActions.getIsUserInLobbyResponse(getServerPort(), accessToken));
        CommonUtils.verifyMissingRole(() -> SkyXploreLobbyActions.getCreateLobbyResponse(getServerPort(), accessToken, ""));
        CommonUtils.verifyMissingRole(() -> SkyXploreLobbyActions.getLobbyViewForPageResponse(getServerPort(), accessToken));
        CommonUtils.verifyMissingRole(() -> SkyXploreLobbyActions.getExitFromLobbyResponse(getServerPort(), accessToken));
        CommonUtils.verifyMissingRole(() -> SkyXploreLobbyActions.getInviteToLobbyResponse(getServerPort(), accessToken, UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> SkyXploreLobbyActions.getAcceptInvitationResponse(getServerPort(), accessToken, UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> SkyXploreLobbyActions.getLobbyPlayersResponse(getServerPort(), accessToken));
        CommonUtils.verifyMissingRole(() -> SkyXploreLobbyActions.getStartGameResponse(getServerPort(), accessToken));
        CommonUtils.verifyMissingRole(() -> SkyXploreLobbyActions.getActiveFriendsResponse(getServerPort(), accessToken));
        CommonUtils.verifyMissingRole(() -> SkyXploreLobbyActions.getLoadGameResponse(getServerPort(), accessToken, UUID.randomUUID()));

        //Settings
        CommonUtils.verifyMissingRole(() -> SkyXploreLobbyActions.getEditSettingsResponse(getServerPort(), accessToken, new SkyXploreGameSettings()));
        CommonUtils.verifyMissingRole(() -> SkyXploreLobbyActions.getGameSettingsResponse(getServerPort(), accessToken));
        CommonUtils.verifyMissingRole(() -> SkyXploreLobbyActions.getCreateOrModifyAiResponse(getServerPort(), accessToken, new AiPlayer()));
        CommonUtils.verifyMissingRole(() -> SkyXploreLobbyActions.getRemoveAiResponse(getServerPort(), accessToken, UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> SkyXploreLobbyActions.getAisResponse(getServerPort(), accessToken));
        CommonUtils.verifyMissingRole(() -> SkyXploreLobbyActions.getAlliancesResponse(getServerPort(), accessToken));
        CommonUtils.verifyMissingRole(() -> SkyXploreLobbyActions.getChangeAllianceOfPlayerResponse(getServerPort(), accessToken, UUID.randomUUID(), ""));
        CommonUtils.verifyMissingRole(() -> SkyXploreLobbyActions.getChangeAllianceOfAiResponse(getServerPort(), accessToken, UUID.randomUUID(), ""));
    }

    @DataProvider(parallel = true)
    public Object[][] roleProvider() {
        return new Object[][]{
            new Object[]{Constants.ROLE_SKYXPLORE},
            new Object[]{Constants.ROLE_ACCESS}
        };
    }
}
