package com.github.saphyra.integration.backend.admin_panel.disabled_role_management;

import com.github.saphyra.apphub.integration.backend.BackEndTest;
import com.github.saphyra.apphub.integration.backend.actions.admin_panel.DisabledRoleActions;
import com.github.saphyra.apphub.integration.backend.model.DisabledRoleResponse;
import com.github.saphyra.apphub.integration.common.framework.Constants;
import com.github.saphyra.apphub.integration.common.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.common.framework.ErrorCode;
import com.github.saphyra.apphub.integration.backend.actions.IndexPageActions;
import com.github.saphyra.apphub.integration.common.framework.localization.Language;
import com.github.saphyra.apphub.integration.common.framework.localization.LocalizationKey;
import com.github.saphyra.apphub.integration.common.framework.localization.LocalizationProperties;
import com.github.saphyra.apphub.integration.common.model.ErrorResponse;
import com.github.saphyra.apphub.integration.common.model.RegistrationParameters;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class DisabledRoleCrud extends BackEndTest {
    @Test(dataProvider = "languageDataProvider")
    public void disabledRoleCrud(Language language) {
        RegistrationParameters testUser = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(language, testUser.toRegistrationRequest());

        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        DatabaseUtil.addRoleByEmail(userData.getEmail(), Constants.ROLE_ADMIN);

        //Initial check
        assertThat(DisabledRoleActions.getDisabledRoles(language, accessTokenId)).contains(new DisabledRoleResponse(Constants.ROLE_TEST, false));
        //Disable role
        Response unknownRoleResponse = DisabledRoleActions.getDisableRoleResponse(language, accessTokenId, testUser.getPassword(), "asd");
        verifyBadRequest(language, unknownRoleResponse);

        Response incorrectPasswordDisableResponse = DisabledRoleActions.getDisableRoleResponse(language, accessTokenId, "asd", Constants.ROLE_TEST);
        verifyBadPassword(language, incorrectPasswordDisableResponse);

        Response disableRoleResponse = DisabledRoleActions.getDisableRoleResponse(language, accessTokenId, testUser.getPassword(), Constants.ROLE_TEST);
        assertThat(disableRoleResponse.getStatusCode()).isEqualTo(200);
        assertThat(DisabledRoleActions.getDisabledRoles(language, accessTokenId)).contains(new DisabledRoleResponse(Constants.ROLE_TEST, true));

        //Enable role
        Response incorrectPasswordEnableResponse = DisabledRoleActions.getEnableRoleResponse(language, accessTokenId, "asd", Constants.ROLE_TEST);
        verifyBadPassword(language, incorrectPasswordEnableResponse);

        Response enableRoleResponse = DisabledRoleActions.getEnableRoleResponse(language, accessTokenId, testUser.getPassword(), Constants.ROLE_TEST);
        assertThat(enableRoleResponse.getStatusCode()).isEqualTo(200);
        assertThat(DisabledRoleActions.getDisabledRoles(language, accessTokenId)).contains(new DisabledRoleResponse(Constants.ROLE_TEST, false));
    }

    private void verifyBadPassword(Language language, Response response) {
        assertThat(response.getStatusCode()).isEqualTo(400);
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.BAD_PASSWORD.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, LocalizationKey.BAD_PASSWORD));
    }

    private void verifyBadRequest(Language language, Response response) {
        assertThat(response.getStatusCode()).isEqualTo(400);
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, LocalizationKey.INVALID_PARAM));
        assertThat(errorResponse.getParams()).containsEntry("role", "unknown or cannot be disabled");
    }
}
