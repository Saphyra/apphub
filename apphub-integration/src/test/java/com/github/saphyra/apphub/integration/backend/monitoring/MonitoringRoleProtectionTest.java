package com.github.saphyra.apphub.integration.backend.monitoring;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.MonitoringActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.CommonUtils;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.structure.api.authorization.TokenResponse;
import com.github.saphyra.apphub.integration.structure.api.monitoring.Feature;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class MonitoringRoleProtectionTest extends BackEndTest {
    @Test(dataProvider = "roleProvider", groups = {"be", "monitoring", "role-protection"})
    public void monitoringRoleProtection(String role) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(getServerPort(), userData.toRegistrationRequest());
        DatabaseUtil.addRoleByEmail(userData.getEmail(), Constants.ROLE_ADMIN);
        DatabaseUtil.removeRoleByEmail(userData.getEmail(), role);
        TokenResponse tokenResponse = IndexPageActions.login(getServerPort(), userData.toLoginRequest());
        String accessToken = tokenResponse.getAccessToken()
            .getJwt();


        CommonUtils.verifyMissingRole(() -> MonitoringActions.getGetFeaturesResponse(getServerPort(), accessToken));
        CommonUtils.verifyMissingRole(() -> MonitoringActions.getGetFunctionalitiesResponse(getServerPort(), accessToken, Feature.ELITE_BASE_MESSAGE_PROCESSING));
        CommonUtils.verifyMissingRole(() -> MonitoringActions.getGetServicesResponse(getServerPort(), accessToken, Feature.ELITE_BASE_MESSAGE_PROCESSING, "asd"));
        CommonUtils.verifyMissingRole(() -> MonitoringActions.getGetMetricsResponse(getServerPort(), accessToken, "asd", Feature.ELITE_BASE_MESSAGE_PROCESSING, "asd", "asd"));
    }

    @DataProvider(parallel = true)
    public Object[][] roleProvider() {
        return new Object[][]{
            new Object[]{Constants.ROLE_ADMIN},
            new Object[]{Constants.ROLE_ACCESS}
        };
    }
}
