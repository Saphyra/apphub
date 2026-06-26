package com.github.saphyra.apphub.integration.backend.admin_panel.role_management;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.admin_panel.RoleManagementActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.core.feature_lock.Feature;
import com.github.saphyra.apphub.integration.core.feature_lock.FeatureLocked;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.DynamoDbUtil;
import com.github.saphyra.apphub.integration.structure.api.authorization.TokenResponse;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import com.github.saphyra.apphub.integration.structure.api.user.UserRoleResponse;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.List;

import static com.github.saphyra.apphub.integration.framework.ResponseValidator.verifyInvalidParam;
import static org.assertj.core.api.Assertions.assertThat;

public class GetUserRolesTest extends BackEndTest {
    @Test(groups = {"be", "admin-panel"})
    @FeatureLocked(Feature.ROLE_TEST)
    public void getRoles() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(getServerPort(), userData.toRegistrationRequest());
        DynamoDbUtil.addRoleByEmail(userData.getEmail(), Constants.ROLE_ADMIN);
        TokenResponse tokenResponse = IndexPageActions.login(getServerPort(), userData.toLoginRequest());
        String accessToken = tokenResponse.getAccessToken()
            .getJwt();

        nullQueryString(accessToken);
        tooShortQueryString(accessToken);
        getUserRoles(userData, accessToken);
    }

    private static void nullQueryString(String accessToken) {
        Response nullQueryStringResponse = RoleManagementActions.getRolesResponse(getServerPort(), accessToken, null);
        verifyInvalidParam(nullQueryStringResponse, "query", "must not be null");
    }

    private static void tooShortQueryString(String accessToken) {
        Response tooShortQueryStringResponse = RoleManagementActions.getRolesResponse(getServerPort(), accessToken, "as");
        verifyInvalidParam(tooShortQueryStringResponse, "query", "too short");
    }

    private static void getUserRoles(RegistrationParameters userData, String accessToken) {
        List<UserRoleResponse> successfulQueryResponse = RoleManagementActions.getRoles(getServerPort(), accessToken, userData.getEmail());

        assertThat(successfulQueryResponse).hasSize(1);
        UserRoleResponse userRoleResponse = successfulQueryResponse.getFirst();
        assertThat(userRoleResponse.getEmail()).isEqualTo(userData.getEmail());
        assertThat(userRoleResponse.getUsername()).isEqualTo(userData.getUsername());
        assertThat(userRoleResponse.getRoles()).containsExactlyInAnyOrder(
            Constants.ROLE_ADMIN,
            Constants.ROLE_NOTEBOOK,
            Constants.ROLE_SKYXPLORE,
            Constants.ROLE_ACCESS,
            Constants.ROLE_TRAINING,
            Constants.ROLE_UTILS,
            Constants.ROLE_COMMUNITY,
            Constants.ROLE_CALENDAR,
            Constants.ROLE_ELITE_BASE,
            Constants.ROLE_TASK_MANAGER
        );
    }
}
