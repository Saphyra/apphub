package com.github.saphyra.apphub.integraton.backend.admin_panel.disabled_role_management;

import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.admin_panel.DisabledRoleActions;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.ErrorCode;
import com.github.saphyra.apphub.integration.localization.Language;
import com.github.saphyra.apphub.integration.structure.DisabledRoleResponse;
import com.github.saphyra.apphub.integration.structure.user.RegistrationParameters;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.UUID;
import java.util.stream.Stream;

import static com.github.saphyra.apphub.integration.framework.ResponseValidator.verifyBadRequest;
import static com.github.saphyra.apphub.integration.framework.ResponseValidator.verifyErrorResponse;
import static com.github.saphyra.apphub.integration.framework.ResponseValidator.verifyInvalidParam;
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
        UUID ati = accessTokenId;
        Stream.generate(() -> "")
            .limit(2)
            .forEach(s -> {
                Response incorrectPasswordDisableResponse = DisabledRoleActions.getDisableRoleResponse(language, ati, "asd", Constants.ROLE_TEST);
                verifyBadRequest(language, incorrectPasswordDisableResponse, ErrorCode.INCORRECT_PASSWORD);
            });

        Response accountLockedDisableRoleResponse = DisabledRoleActions.getDisableRoleResponse(language, accessTokenId, "asd", Constants.ROLE_TEST);
        verifyErrorResponse(language, accountLockedDisableRoleResponse, 401, ErrorCode.ACCOUNT_LOCKED);

        DatabaseUtil.unlockUserByEmail(userData.getEmail());
        accessTokenId = IndexPageActions.login(language, userData.toLoginRequest());

        //Disable role
        Response disableRoleResponse = DisabledRoleActions.getDisableRoleResponse(language, accessTokenId, testUser.getPassword(), Constants.ROLE_TEST);
        assertThat(disableRoleResponse.getStatusCode()).isEqualTo(200);
        assertThat(DisabledRoleActions.getDisabledRoles(language, accessTokenId)).contains(new DisabledRoleResponse(Constants.ROLE_TEST, true));

        //Enable role - Incorrect password
        UUID ati2 = accessTokenId;
        Stream.generate(() -> "")
            .limit(2)
            .forEach(s -> {
                Response incorrectPasswordEnableResponse = DisabledRoleActions.getEnableRoleResponse(language, ati2, "asd", Constants.ROLE_TEST);
                verifyBadRequest(language, incorrectPasswordEnableResponse, ErrorCode.INCORRECT_PASSWORD);
            });

        Response accountLockedEnableResponse = DisabledRoleActions.getEnableRoleResponse(language, ati2, "asd", Constants.ROLE_TEST);
        verifyErrorResponse(language, accountLockedEnableResponse, 401, ErrorCode.ACCOUNT_LOCKED);

        DatabaseUtil.unlockUserByEmail(userData.getEmail());
        accessTokenId = IndexPageActions.login(language, userData.toLoginRequest());

        //Enable role
        Response enableRoleResponse = DisabledRoleActions.getEnableRoleResponse(language, accessTokenId, testUser.getPassword(), Constants.ROLE_TEST);
        assertThat(enableRoleResponse.getStatusCode()).isEqualTo(200);
        assertThat(DisabledRoleActions.getDisabledRoles(language, accessTokenId)).contains(new DisabledRoleResponse(Constants.ROLE_TEST, false));
    }
}
