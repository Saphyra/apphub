package com.github.saphyra.apphub.integration.backend.skyxplore.data;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreSavedGameActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.CommonUtils;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.db.dynamodb.UserDynamoDbRepository;
import com.github.saphyra.apphub.integration.structure.api.authorization.TokenResponse;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.UUID;

public class SkyXploreSavedGamesRoleProtectionTest extends BackEndTest {
    @Test(dataProvider = "roleProvider", groups = {"be", "skyxplore", "role-protection"})
    public void savedGamesRoleProtection(String role) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(getServerPort(), userData.toRegistrationRequest());
        UserDynamoDbRepository.removeRoleByEmail(userData.getEmail(), role);
        TokenResponse tokenResponse = IndexPageActions.login(getServerPort(), userData.toLoginRequest());
        String accessToken = tokenResponse.getAccessToken()
            .getJwt();

        CommonUtils.verifyMissingRole(() -> SkyXploreSavedGameActions.getSavedGamesResponse(getServerPort(), accessToken));
        CommonUtils.verifyMissingRole(() -> SkyXploreSavedGameActions.getDeleteGameResponse(getServerPort(), accessToken, UUID.randomUUID()));
    }

    @DataProvider(parallel = true)
    public Object[][] roleProvider() {
        return new Object[][]{
            new Object[]{Constants.ROLE_SKYXPLORE},
            new Object[]{Constants.ROLE_ACCESS}
        };
    }
}
