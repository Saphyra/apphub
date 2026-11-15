package com.github.saphyra.apphub.integration.backend.skyxplore.admin;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreAdminActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.CommonUtils;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.db.dynamodb.UserDynamoDbRepository;
import com.github.saphyra.apphub.integration.structure.api.authorization.TokenResponse;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.GameItemType;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.UUID;

public class SkyXploreAdminRoleProtectionTest extends BackEndTest {
    /**
     * Tests that an admin user who has the {@code ROLE_ADMIN} privilege but lacks one of the required roles ({@code ROLE_ADMIN}, {@code ROLE_SKYXPLORE}, or {@code ROLE_ACCESS}).
     * The test verifies that such a user is prevented from accessing SkyXplore admin endpoints.
     *
     * <ul>
     *   <li>validate endpoint {@code /skyxplore/admin/getByType} returns 403 when called without {@code ROLE_ADMIN}</li>
     *   <li>validate endpoint {@code /skyxplore/admin/getItem} returns 403 when called without {@code ROLE_SKYXPLORE}</li>
     *   <li>validate endpoint {@code /skyxplore/admin/getItem} returns 403 when called without {@code ROLE_ACCESS}</li>
     * </ul>
     */
    @Test(dataProvider = "adminRoleProvider", groups = {"be", "skyxplore", "role-protection"})
    public void adminRoleProtection(String role) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(getServerPort(), userData.toRegistrationRequest());
        UserDynamoDbRepository.addRoleByEmail(userData.getEmail(), Constants.ROLE_ADMIN);
        UserDynamoDbRepository.removeRoleByEmail(userData.getEmail(), role);
        TokenResponse tokenResponse = IndexPageActions.login(getServerPort(), userData.toLoginRequest());
        String accessToken = tokenResponse.getAccessToken()
            .getJwt();

        CommonUtils.verifyMissingRole(() -> SkyXploreAdminActions.getGetByTypeResponse(getServerPort(), accessToken, GameItemType.GAME, UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> SkyXploreAdminActions.getGetItemResponse(getServerPort(), accessToken, UUID.randomUUID(), GameItemType.GAME, UUID.randomUUID()));
    }

    @DataProvider(parallel = true)
    public Object[][] adminRoleProvider() {
        return new Object[][]{
            new Object[]{Constants.ROLE_ADMIN},
            new Object[]{Constants.ROLE_SKYXPLORE},
            new Object[]{Constants.ROLE_ACCESS}
        };
    }
}
