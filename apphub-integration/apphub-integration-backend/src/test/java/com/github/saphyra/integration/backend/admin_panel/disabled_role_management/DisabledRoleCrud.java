package com.github.saphyra.integration.backend.admin_panel.disabled_role_management;

import com.github.saphyra.apphub.integration.backend.BackEndTest;
import com.github.saphyra.apphub.integration.backend.actions.IndexPageActions;
import com.github.saphyra.apphub.integration.backend.actions.admin_panel.DisabledRoleActions;
import com.github.saphyra.apphub.integration.backend.model.DisabledRoleResponse;
import com.github.saphyra.apphub.integration.common.framework.Constants;
import com.github.saphyra.apphub.integration.common.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.common.framework.ErrorCode;
import com.github.saphyra.apphub.integration.common.framework.localization.Language;
import com.github.saphyra.apphub.integration.common.model.RegistrationParameters;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.UUID;

import static com.github.saphyra.apphub.integration.backend.ResponseValidator.verifyBadRequest;
import static com.github.saphyra.apphub.integration.backend.ResponseValidator.verifyInvalidParam;
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

        //Disable role - Unknown role
        Response unknownRoleResponse = DisabledRoleActions.getDisableRoleResponse(language, accessTokenId, testUser.getPassword(), "asd");
        verifyInvalidParam(language, unknownRoleResponse, "role", "unknown or cannot be disabled");

        //Disable role - Incorrect password
        Response incorrectPasswordDisableResponse = DisabledRoleActions.getDisableRoleResponse(language, accessTokenId, "asd", Constants.ROLE_TEST);
        verifyBadRequest(language, incorrectPasswordDisableResponse, ErrorCode.INCORRECT_PASSWORD);

        //Disable role
        Response disableRoleResponse = DisabledRoleActions.getDisableRoleResponse(language, accessTokenId, testUser.getPassword(), Constants.ROLE_TEST);
        assertThat(disableRoleResponse.getStatusCode()).isEqualTo(200);
        assertThat(DisabledRoleActions.getDisabledRoles(language, accessTokenId)).contains(new DisabledRoleResponse(Constants.ROLE_TEST, true));

        //Enable role - Bad password
        Response incorrectPasswordEnableResponse = DisabledRoleActions.getEnableRoleResponse(language, accessTokenId, "asd", Constants.ROLE_TEST);
        verifyBadRequest(language, incorrectPasswordEnableResponse, ErrorCode.INCORRECT_PASSWORD);

        //Enable role
        Response enableRoleResponse = DisabledRoleActions.getEnableRoleResponse(language, accessTokenId, testUser.getPassword(), Constants.ROLE_TEST);
        assertThat(enableRoleResponse.getStatusCode()).isEqualTo(200);
        assertThat(DisabledRoleActions.getDisabledRoles(language, accessTokenId)).contains(new DisabledRoleResponse(Constants.ROLE_TEST, false));
    }
}
