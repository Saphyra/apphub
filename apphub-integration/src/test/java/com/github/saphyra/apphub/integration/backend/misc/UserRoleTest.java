package com.github.saphyra.apphub.integration.backend.misc;

import com.github.saphyra.apphub.integration.action.backend.AccessTokenActions;
import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.UtilActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.structure.api.authorization.TokenResponse;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class UserRoleTest extends BackEndTest {
    @Test(groups = {"be", "misc"})
    public void isAdminTest() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(getServerPort(), userData.toRegistrationRequest());
        TokenResponse tokenResponse = IndexPageActions.login(getServerPort(), userData.toLoginRequest());
        String accessToken = tokenResponse.getAccessToken()
            .getJwt();

        assertThat(UtilActions.isUserAdmin(getServerPort(), accessToken)).isFalse();

        DatabaseUtil.addRoleByEmail(userData.getEmail(), Constants.ROLE_ADMIN);
        accessToken = AccessTokenActions.refresh(getServerPort(), tokenResponse.getRefreshToken().getJwt())
            .getAccessToken()
            .getJwt();
        assertThat(UtilActions.isUserAdmin(getServerPort(), accessToken)).isTrue();
    }
}
