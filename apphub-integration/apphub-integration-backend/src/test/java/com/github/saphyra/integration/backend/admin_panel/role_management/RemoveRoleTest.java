package com.github.saphyra.integration.backend.admin_panel.role_management;

import com.github.saphyra.apphub.integration.backend.actions.IndexPageActions;
import com.github.saphyra.apphub.integration.backend.actions.RoleManagementPageActions;
import com.github.saphyra.apphub.integration.backend.model.RoleRequest;
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

public class RemoveRoleTest extends TestBase {
    @DataProvider(name = "localeDataProvider", parallel = true)
    public Object[] localeDataProvider() {
        return Language.values();
    }

    @Test(dataProvider = "localeDataProvider")
    public void nullUserId(Language language) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        DatabaseUtil.addRoleByEmail(userData.getEmail(), Constants.ROLE_ADMIN);

        RoleRequest roleRequest = RoleRequest.builder()
            .userId(null)
            .role(Constants.ROLE_ADMIN)
            .build();

        Response response = RoleManagementPageActions.getRemoveRoleResponse(language, accessTokenId, roleRequest);

        assertThat(response.getStatusCode()).isEqualTo(400);
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, LocalizationKey.ERROR_CODE_INVALID_PARAM));
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(errorResponse.getParams().get("userId")).isEqualTo("must not be null");
    }

    @Test(dataProvider = "localeDataProvider")
    public void nullRole(Language language) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        DatabaseUtil.addRoleByEmail(userData.getEmail(), Constants.ROLE_ADMIN);

        RoleRequest roleRequest = RoleRequest.builder()
            .userId(UUID.randomUUID())
            .role(null)
            .build();

        Response response = RoleManagementPageActions.getRemoveRoleResponse(language, accessTokenId, roleRequest);

        assertThat(response.getStatusCode()).isEqualTo(400);
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, LocalizationKey.ERROR_CODE_INVALID_PARAM));
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(errorResponse.getParams().get("role")).isEqualTo("must not be null or blank");
    }

    @Test(dataProvider = "localeDataProvider")
    public void blankRole(Language language) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        DatabaseUtil.addRoleByEmail(userData.getEmail(), Constants.ROLE_ADMIN);

        RoleRequest roleRequest = RoleRequest.builder()
            .userId(UUID.randomUUID())
            .role(" ")
            .build();

        Response response = RoleManagementPageActions.getRemoveRoleResponse(language, accessTokenId, roleRequest);

        assertThat(response.getStatusCode()).isEqualTo(400);
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, LocalizationKey.ERROR_CODE_INVALID_PARAM));
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(errorResponse.getParams().get("role")).isEqualTo("must not be null or blank");
    }

    @Test(dataProvider = "localeDataProvider")
    public void userNotFound(Language language) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        DatabaseUtil.addRoleByEmail(userData.getEmail(), Constants.ROLE_ADMIN);

        RoleRequest roleRequest = RoleRequest.builder()
            .userId(UUID.randomUUID())
            .role(Constants.ROLE_ADMIN)
            .build();

        Response response = RoleManagementPageActions.getRemoveRoleResponse(language, accessTokenId, roleRequest);

        assertThat(response.getStatusCode()).isEqualTo(404);
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, LocalizationKey.ERROR_CODE_USER_NOT_FOUND));
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND.name());
    }

    @Test(dataProvider = "localeDataProvider")
    public void roleNotExist(Language language) {
        RegistrationParameters testUser = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(language, testUser.toRegistrationRequest());
        UUID userId = DatabaseUtil.getUserIdByEmail(testUser.getEmail());

        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        DatabaseUtil.addRoleByEmail(userData.getEmail(), Constants.ROLE_ADMIN);

        RoleRequest roleRequest = RoleRequest.builder()
            .userId(userId)
            .role(Constants.ROLE_ADMIN)
            .build();

        Response response = RoleManagementPageActions.getRemoveRoleResponse(language, accessTokenId, roleRequest);

        assertThat(response.getStatusCode()).isEqualTo(404);
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, LocalizationKey.ERROR_CODE_ROLE_NOT_FOUND));
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.ROLE_NOT_FOUND.name());
    }

    @Test
    public void removeRole() {
        Language language = Language.HUNGARIAN;

        RegistrationParameters testUser = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(language, testUser.toRegistrationRequest());
        UUID userId = DatabaseUtil.getUserIdByEmail(testUser.getEmail());

        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        DatabaseUtil.addRoleByEmail(userData.getEmail(), Constants.ROLE_ADMIN);

        RoleRequest roleRequest = RoleRequest.builder()
            .userId(userId)
            .role(Constants.ROLE_ADMIN)
            .build();

        RoleManagementPageActions.addRole(language, accessTokenId, roleRequest);

        RoleManagementPageActions.removeRole(language, accessTokenId, roleRequest);

        List<UserRoleResponse> responses = RoleManagementPageActions.getRoles(language, accessTokenId, testUser.getUsername());
        assertThat(responses.get(0).getRoles()).isEmpty();
    }
}
