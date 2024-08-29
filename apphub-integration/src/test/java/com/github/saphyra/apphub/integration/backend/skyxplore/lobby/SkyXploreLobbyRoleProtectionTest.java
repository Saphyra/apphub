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
        UUID accessTokenId = IndexPageActions.registerAndLogin(getServerPort(), userData);

        DatabaseUtil.removeRoleByEmail(userData.getEmail(), role);

        SleepUtil.sleep(3000);

        //Platform
        CommonUtils.verifyMissingRole(() -> SkyXploreLobbyActions.getIsUserInLobbyResponse(getServerPort(), accessTokenId));
        CommonUtils.verifyMissingRole(() -> SkyXploreLobbyActions.getCreateLobbyResponse(getServerPort(), accessTokenId, ""));
        CommonUtils.verifyMissingRole(() -> SkyXploreLobbyActions.getLobbyViewForPageResponse(getServerPort(), accessTokenId));
        CommonUtils.verifyMissingRole(() -> SkyXploreLobbyActions.getExitFromLobbyResponse(getServerPort(), accessTokenId));
        CommonUtils.verifyMissingRole(() -> SkyXploreLobbyActions.getInviteToLobbyResponse(getServerPort(), accessTokenId, UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> SkyXploreLobbyActions.getAcceptInvitationResponse(getServerPort(), accessTokenId, UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> SkyXploreLobbyActions.getLobbyPlayersResponse(getServerPort(), accessTokenId));
        CommonUtils.verifyMissingRole(() -> SkyXploreLobbyActions.getStartGameResponse(getServerPort(), accessTokenId));
        CommonUtils.verifyMissingRole(() -> SkyXploreLobbyActions.getActiveFriendsResponse(getServerPort(), accessTokenId));
        CommonUtils.verifyMissingRole(() -> SkyXploreLobbyActions.getLoadGameResponse(getServerPort(), accessTokenId, UUID.randomUUID()));

        //Settings
        CommonUtils.verifyMissingRole(() -> SkyXploreLobbyActions.getEditSettingsResponse(getServerPort(), accessTokenId, new SkyXploreGameSettings()));
        CommonUtils.verifyMissingRole(() -> SkyXploreLobbyActions.getGameSettingsResponse(getServerPort(), accessTokenId));
        CommonUtils.verifyMissingRole(() -> SkyXploreLobbyActions.getCreateOrModifyAiResponse(getServerPort(), accessTokenId, new AiPlayer()));
        CommonUtils.verifyMissingRole(() -> SkyXploreLobbyActions.getRemoveAiResponse(getServerPort(), accessTokenId, UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> SkyXploreLobbyActions.getAisResponse(getServerPort(), accessTokenId));
        CommonUtils.verifyMissingRole(() -> SkyXploreLobbyActions.getAlliancesResponse(getServerPort(), accessTokenId));
        CommonUtils.verifyMissingRole(() -> SkyXploreLobbyActions.getChangeAllianceOfPlayerResponse(getServerPort(), accessTokenId, UUID.randomUUID(), ""));
        CommonUtils.verifyMissingRole(() -> SkyXploreLobbyActions.getChangeAllianceOfAiResponse(getServerPort(), accessTokenId, UUID.randomUUID(), ""));
    }

    @DataProvider(parallel = true)
    public Object[][] roleProvider() {
        return new Object[][]{
            new Object[]{Constants.ROLE_SKYXPLORE},
            new Object[]{Constants.ROLE_ACCESS}
        };
    }
}
