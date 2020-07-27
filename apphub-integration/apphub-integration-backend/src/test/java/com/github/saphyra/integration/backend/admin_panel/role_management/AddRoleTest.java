package com.github.saphyra.integration.backend.admin_panel.role_management;

import com.github.saphyra.apphub.integration.common.framework.IndexPageActions;
import com.github.saphyra.apphub.integration.backend.actions.RoleManagementPageActions;
import com.github.saphyra.apphub.integration.backend.model.RoleRequest;
import com.github.saphyra.apphub.integration.common.model.UserRoleResponse;
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

public class AddRoleTest extends TestBase {
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

        Response response = RoleManagementPageActions.getAddRoleResponse(language, accessTokenId, roleRequest);

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

        Response response = RoleManagementPageActions.getAddRoleResponse(language, accessTokenId, roleRequest);

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

        Response response = RoleManagementPageActions.getAddRoleResponse(language, accessTokenId, roleRequest);

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

        Response response = RoleManagementPageActions.getAddRoleResponse(language, accessTokenId, roleRequest);

        assertThat(response.getStatusCode()).isEqualTo(404);
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, LocalizationKey.ERROR_CODE_USER_NOT_FOUND));
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND.name());
    }

    @Test(dataProvider = "localeDataProvider")
    public void roleAlreadyExists(Language language) {
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

        Response response = RoleManagementPageActions.getAddRoleResponse(language, accessTokenId, roleRequest);

        assertThat(response.getStatusCode()).isEqualTo(409);
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, LocalizationKey.ERROR_CODE_ROLE_ALREADY_EXISTS));
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.ROLE_ALREADY_EXISTS.name());
    }

    @Test
    public void addRole() {
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

        List<UserRoleResponse> responses = RoleManagementPageActions.getRoles(language, accessTokenId, testUser.getUsername());
        assertThat(responses.get(0).getRoles()).containsExactlyInAnyOrder(Constants.ROLE_ADMIN, Constants.ROLE_NOTEBOOK);
    }
}
