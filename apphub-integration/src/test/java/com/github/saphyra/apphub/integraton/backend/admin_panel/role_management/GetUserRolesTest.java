package com.github.saphyra.apphub.integraton.backend.admin_panel.role_management;

import com.github.saphyra.apphub.integration.BackEndTest;
import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.admin_panel.RoleManagementActions;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.localization.Language;
import com.github.saphyra.apphub.integration.structure.user.RegistrationParameters;
import com.github.saphyra.apphub.integration.structure.user.UserRoleResponse;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.List;
import java.util.UUID;

import static com.github.saphyra.apphub.integration.framework.ResponseValidator.verifyInvalidParam;
import static org.assertj.core.api.Assertions.assertThat;

public class GetUserRolesTest extends BackEndTest {
    @Test(dataProvider = "languageDataProvider")
    public void getRoles(Language language) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);
        DatabaseUtil.addRoleByEmail(userData.getEmail(), Constants.ROLE_ADMIN);

        //Null query string
        Response nullQueryStringResponse = RoleManagementActions.getRolesResponse(language, accessTokenId, null);
        verifyInvalidParam(language, nullQueryStringResponse, "searchText", "must not be null");

        //Too short query string
        Response tooShortQueryStringResponse = RoleManagementActions.getRolesResponse(language, accessTokenId, "as");
        verifyInvalidParam(language, tooShortQueryStringResponse, "searchText", "too short");

        //Get user roles
        List<UserRoleResponse> successfulQueryResponse = RoleManagementActions.getRoles(language, accessTokenId, userData.getEmail());

        assertThat(successfulQueryResponse).hasSize(1);
        UserRoleResponse userRoleResponse = successfulQueryResponse.get(0);
        assertThat(userRoleResponse.getEmail()).isEqualTo(userData.getEmail());
        assertThat(userRoleResponse.getUsername()).isEqualTo(userData.getUsername());
        assertThat(userRoleResponse.getRoles()).containsExactlyInAnyOrder(Constants.ROLE_ADMIN, Constants.ROLE_NOTEBOOK, Constants.ROLE_SKYXPLORE, Constants.ROLE_ACCESS, Constants.ROLE_TRAINING, Constants.ROLE_UTILS);
    }
}