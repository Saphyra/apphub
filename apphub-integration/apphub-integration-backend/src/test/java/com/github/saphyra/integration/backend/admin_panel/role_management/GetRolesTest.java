package com.github.saphyra.integration.backend.admin_panel.role_management;

import com.github.saphyra.apphub.integration.backend.actions.IndexPageActions;
import com.github.saphyra.apphub.integration.backend.actions.RoleManagementPageActions;
import com.github.saphyra.apphub.integration.backend.model.UserRoleResponse;
import com.github.saphyra.apphub.integration.common.TestBase;
import com.github.saphyra.apphub.integration.common.framework.Constants;
import com.github.saphyra.apphub.integration.common.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.common.framework.ErrorCode;
import com.github.saphyra.apphub.integration.common.framework.localization.Language;
import com.github.saphyra.apphub.integration.common.framework.localization.LocalizationKey;
import com.github.saphyra.apphub.integration.common.framework.localization.LocalizationProperties;
import com.github.saphyra.apphub.integration.common.model.ErrorResponse;
import com.github.saphyra.apphub.integration.common.model.RegistrationParameters;
import io.restassured.response.Response;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class GetRolesTest extends TestBase {
    @DataProvider(name = "localeDataProvider", parallel = true)
    public Object[] localeDataProvider() {
        return Language.values();
    }

    @Test(dataProvider = "localeDataProvider")
    public void getRoles_nullQueryString(Language language) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        DatabaseUtil.addRoleByEmail(userData.getEmail(), Constants.ROLE_ADMIN);

        Response response = RoleManagementPageActions.getRolesResponse(language, accessTokenId, null);

        assertThat(response.getStatusCode()).isEqualTo(400);
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, LocalizationKey.ERROR_CODE_INVALID_PARAM));
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(errorResponse.getParams().get("searchText")).isEqualTo("must not be null");
    }

    @Test(dataProvider = "localeDataProvider")
    public void getRoles_tooShortQueryString(Language language) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        DatabaseUtil.addRoleByEmail(userData.getEmail(), Constants.ROLE_ADMIN);

        Response response = RoleManagementPageActions.getRolesResponse(language, accessTokenId, "as");

        assertThat(response.getStatusCode()).isEqualTo(400);
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, LocalizationKey.ERROR_CODE_INVALID_PARAM));
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(errorResponse.getParams().get("searchText")).isEqualTo("too short");
    }

    @Test
    public void getRoles() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        Language language = Language.HUNGARIAN;
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        DatabaseUtil.addRoleByEmail(userData.getEmail(), Constants.ROLE_ADMIN);

        List<UserRoleResponse> response = RoleManagementPageActions.getRoles(language, accessTokenId, userData.getEmail());

        assertThat(response).hasSize(1);
        UserRoleResponse userRoleResponse = response.get(0);
        assertThat(userRoleResponse.getEmail()).isEqualTo(userData.getEmail());
        assertThat(userRoleResponse.getUsername()).isEqualTo(userData.getUsername());
        assertThat(userRoleResponse.getRoles()).containsExactly(Constants.ROLE_ADMIN);
    }
}
