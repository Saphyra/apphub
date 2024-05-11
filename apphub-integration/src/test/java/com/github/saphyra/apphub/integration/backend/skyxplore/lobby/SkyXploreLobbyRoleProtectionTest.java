package com.github.saphyra.apphub.integration.backend.skyxplore.lobby;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreLobbyActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.CommonUtils;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.SleepUtil;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.AiPlayer;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.SkyXploreGameSettings;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.UUID;

public class SkyXploreLobbyRoleProtectionTest extends BackEndTest {
    @Test(dataProvider = "roleProvider", groups = {"be", "skyxplore"})
    public void lobbyRoleProtection(String role) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(userData);

        DatabaseUtil.removeRoleByEmail(userData.getEmail(), role);

        SleepUtil.sleep(3000);

        //Platform
        CommonUtils.verifyMissingRole(() -> SkyXploreLobbyActions.getIsUserInLobbyResponse(accessTokenId));
        CommonUtils.verifyMissingRole(() -> SkyXploreLobbyActions.getCreateLobbyResponse(accessTokenId, ""));
        CommonUtils.verifyMissingRole(() -> SkyXploreLobbyActions.getLobbyViewForPageResponse(accessTokenId));
        CommonUtils.verifyMissingRole(() -> SkyXploreLobbyActions.getExitFromLobbyResponse(accessTokenId));
        CommonUtils.verifyMissingRole(() -> SkyXploreLobbyActions.getInviteToLobbyResponse(accessTokenId, UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> SkyXploreLobbyActions.getAcceptInvitationResponse(accessTokenId, UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> SkyXploreLobbyActions.getLobbyPlayersResponse(accessTokenId));
        CommonUtils.verifyMissingRole(() -> SkyXploreLobbyActions.getStartGameResponse(accessTokenId));
        CommonUtils.verifyMissingRole(() -> SkyXploreLobbyActions.getActiveFriendsResponse(accessTokenId));
        CommonUtils.verifyMissingRole(() -> SkyXploreLobbyActions.getLoadGameResponse(accessTokenId, UUID.randomUUID()));

        //Settings
        CommonUtils.verifyMissingRole(() -> SkyXploreLobbyActions.getEditSettingsResponse(accessTokenId, new SkyXploreGameSettings()));
        CommonUtils.verifyMissingRole(() -> SkyXploreLobbyActions.getGameSettingsResponse(accessTokenId));
        CommonUtils.verifyMissingRole(() -> SkyXploreLobbyActions.getCreateOrModifyAiResponse(accessTokenId, new AiPlayer()));
        CommonUtils.verifyMissingRole(() -> SkyXploreLobbyActions.getRemoveAiResponse(accessTokenId, UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> SkyXploreLobbyActions.getAisResponse(accessTokenId));
        CommonUtils.verifyMissingRole(() -> SkyXploreLobbyActions.getAlliancesResponse(accessTokenId));
        CommonUtils.verifyMissingRole(() -> SkyXploreLobbyActions.getChangeAllianceOfPlayerResponse(accessTokenId, UUID.randomUUID(), ""));
        CommonUtils.verifyMissingRole(() -> SkyXploreLobbyActions.getChangeAllianceOfAiResponse(accessTokenId, UUID.randomUUID(), ""));
    }

    @DataProvider(parallel = true)
    public Object[][] roleProvider() {
        return new Object[][]{
            new Object[]{Constants.ROLE_SKYXPLORE},
            new Object[]{Constants.ROLE_ACCESS}
        };
    }
}
