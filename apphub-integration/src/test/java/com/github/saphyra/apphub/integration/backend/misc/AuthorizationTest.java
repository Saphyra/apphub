package com.github.saphyra.apphub.integration.backend.misc;

import com.github.saphyra.apphub.integration.action.backend.AccessTokenActions;
import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.ModulesActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.ErrorCode;
import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.ResponseValidator;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.framework.endpoints.UserEndpoints;
import com.github.saphyra.apphub.integration.structure.api.ErrorResponse;
import com.github.saphyra.apphub.integration.structure.api.authorization.TokenResponse;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class AuthorizationTest extends BackEndTest {
    @Test(groups = {"be", "misc"})
    public void callProtectedEndpointWithoutAccessToken() {
        Response response = RequestFactory.createRequest()
            .get(UrlFactory.create(getServerPort(), UserEndpoints.CHECK_SESSION));

        assertThat(response.getStatusCode()).isEqualTo(401);
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.NO_SESSION_AVAILABLE.name());
    }

    @Test(groups = {"be", "misc"})
    public void refreshToken() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(getServerPort(), userData.toRegistrationRequest());
        TokenResponse tokenResponse = IndexPageActions.login(getServerPort(), userData.toLoginRequest());

        TokenResponse newTokenResponse = AccessTokenActions.refresh(getServerPort(), tokenResponse.getRefreshToken().getJwt());

        ResponseValidator.verifyErrorResponse(AccessTokenActions.getRefreshResponse(getServerPort(), tokenResponse.getRefreshToken().getJwt()), 401, ErrorCode.NO_SESSION_AVAILABLE);

        ModulesActions.getModules(getServerPort(), newTokenResponse.getAccessToken().getJwt());
    }

    @Test(groups = {"be", "misc"})
    public void checkSession() {
        ResponseValidator.verifyErrorResponse(AccessTokenActions.getSessionCheckResponse(getServerPort(), "asd"), 401, ErrorCode.INVALID_TOKEN);

        RegistrationParameters userData = RegistrationParameters.validParameters();
        String accessToken = IndexPageActions.registerAndLogin(getServerPort(), userData);

        assertThat(AccessTokenActions.getSessionCheckResponse(getServerPort(), accessToken).getStatusCode()).isEqualTo(200);
    }
}
