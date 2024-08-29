package com.github.saphyra.apphub.integration.backend.account;

import com.github.saphyra.apphub.integration.action.backend.AccountActions;
import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.CommonUtils;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.SleepUtil;
import com.github.saphyra.apphub.integration.structure.api.OneParamRequest;
import com.github.saphyra.apphub.integration.structure.api.user.ChangeEmailRequest;
import com.github.saphyra.apphub.integration.structure.api.user.ChangePasswordRequest;
import com.github.saphyra.apphub.integration.structure.api.user.ChangeUsernameRequest;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.testng.annotations.Test;

import java.util.UUID;

public class AccountRoleProtectionTest extends BackEndTest {
    @Test(groups = {"be", "account"})
    public void accountRoleProtection() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(getServerPort(), userData);

        DatabaseUtil.removeRoleByEmail(userData.getEmail(), Constants.ROLE_ACCESS);

        SleepUtil.sleep(3000);

        CommonUtils.verifyMissingRole(() -> AccountActions.getChangeEmailResponse(getServerPort(), accessTokenId, new ChangeEmailRequest()));
        CommonUtils.verifyMissingRole(() -> AccountActions.getChangePasswordResponse(getServerPort(), accessTokenId, new ChangePasswordRequest()));
        CommonUtils.verifyMissingRole(() -> AccountActions.getChangeUsernameResponse(getServerPort(), accessTokenId, new ChangeUsernameRequest()));
        CommonUtils.verifyMissingRole(() -> AccountActions.getDeleteAccountResponse(getServerPort(), accessTokenId, new OneParamRequest<>()));
    }
}
