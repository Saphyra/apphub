package com.github.saphyra.apphub.integration.backend.account;

import com.github.saphyra.apphub.integration.action.backend.AccountActions;
import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.DataConstants;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.ErrorCode;
import com.github.saphyra.apphub.integration.framework.ResponseValidator;
import com.github.saphyra.apphub.integration.structure.api.ErrorResponse;
import com.github.saphyra.apphub.integration.structure.api.OneParamRequest;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.stream.Stream;

import static com.github.saphyra.apphub.integration.framework.ResponseValidator.verifyErrorResponse;
import static com.github.saphyra.apphub.integration.framework.ResponseValidator.verifyInvalidParam;
import static org.assertj.core.api.Assertions.assertThat;

public class DeleteAccountTest extends BackEndTest {
    @Test(groups = {"be", "account"})
    public void deleteAccount() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        String accessToken = IndexPageActions.registerAndLogin(getServerPort(), userData);

        nullPassword(accessToken);
        accessToken = incorrectPassword(userData, accessToken);
        successfulDeletion(userData, accessToken);
    }

    private static void nullPassword(String accessToken) {
        Response nullPasswordResponse = AccountActions.getDeleteAccountResponse(getServerPort(), accessToken, new OneParamRequest<>(null));
        verifyInvalidParam(nullPasswordResponse, "password", "must not be null");
    }

    private static String incorrectPassword(RegistrationParameters userData, String accessToken) {
        String ati = accessToken;
        Stream.generate(() -> "")
            .limit(2)
            .forEach(_ -> {
                Response incorrectPasswordResponse = AccountActions.getDeleteAccountResponse(getServerPort(), ati, new OneParamRequest<>(DataConstants.INCORRECT_PASSWORD));
                ResponseValidator.verifyBadRequest(incorrectPasswordResponse, ErrorCode.INCORRECT_PASSWORD);
            });

        Response accountLockedResponse = AccountActions.getDeleteAccountResponse(getServerPort(), accessToken, new OneParamRequest<>(DataConstants.INCORRECT_PASSWORD));
        verifyErrorResponse(accountLockedResponse, 423, ErrorCode.ACCOUNT_LOCKED);

        DatabaseUtil.unlockUserByEmail(userData.getEmail());
        accessToken = IndexPageActions.login(getServerPort(), userData.toLoginRequest())
            .getAccessToken()
            .getJwt();
        return accessToken;
    }

    private static void successfulDeletion(RegistrationParameters userData, String accessToken) {
        Response response = AccountActions.getDeleteAccountResponse(getServerPort(), accessToken, new OneParamRequest<>(userData.getPassword()));
        assertThat(response.getStatusCode()).isEqualTo(200);
        Response loginResponse = IndexPageActions.getLoginResponse(getServerPort(), userData.toLoginRequest());
        assertThat(loginResponse.getStatusCode()).isEqualTo(401);
        ErrorResponse errorResponse = loginResponse.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.BAD_CREDENTIALS.name());
    }
}
