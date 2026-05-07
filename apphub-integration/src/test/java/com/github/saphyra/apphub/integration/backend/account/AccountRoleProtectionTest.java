package com.github.saphyra.apphub.integration.backend.account;

import com.github.saphyra.apphub.integration.action.backend.AccessTokenActions;
import com.github.saphyra.apphub.integration.action.backend.AccountActions;
import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.CommonUtils;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.structure.api.OneParamRequest;
import com.github.saphyra.apphub.integration.structure.api.authorization.TokenResponse;
import com.github.saphyra.apphub.integration.structure.api.user.ChangeEmailRequest;
import com.github.saphyra.apphub.integration.structure.api.user.ChangePasswordRequest;
import com.github.saphyra.apphub.integration.structure.api.user.ChangeUsernameRequest;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.testng.annotations.Test;

public class AccountRoleProtectionTest extends BackEndTest {
    @Test(groups = {"be", "account", "role-protection"})
    public void accountRoleProtection() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(getServerPort(), userData.toRegistrationRequest());
        TokenResponse tokenResponse = IndexPageActions.login(getServerPort(), userData.toLoginRequest());

        DatabaseUtil.removeRoleByEmail(userData.getEmail(), Constants.ROLE_ACCESS);
        tokenResponse = AccessTokenActions.refresh(getServerPort(), tokenResponse.getRefreshToken().getJwt());
        String accessToken = tokenResponse.getAccessToken()
            .getJwt();

        CommonUtils.verifyMissingRole(() -> AccountActions.getChangeEmailResponse(getServerPort(), accessToken, new ChangeEmailRequest()));
        CommonUtils.verifyMissingRole(() -> AccountActions.getChangePasswordResponse(getServerPort(), accessToken, new ChangePasswordRequest()));
        CommonUtils.verifyMissingRole(() -> AccountActions.getChangeUsernameResponse(getServerPort(), accessToken, new ChangeUsernameRequest()));
        CommonUtils.verifyMissingRole(() -> AccountActions.getDeleteAccountResponse(getServerPort(), accessToken, new OneParamRequest<>()));
    }
}
