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

import java.util.UUID;
import java.util.stream.Stream;

import static com.github.saphyra.apphub.integration.framework.ResponseValidator.verifyErrorResponse;
import static com.github.saphyra.apphub.integration.framework.ResponseValidator.verifyInvalidParam;
import static org.assertj.core.api.Assertions.assertThat;

public class DeleteAccountTest extends BackEndTest {
    @Test(groups = {"be", "account"})
    public void deleteAccount() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(getServerPort(), userData);

        nullPassword(accessTokenId);
        accessTokenId = incorrectPassword(userData, accessTokenId);
        successfulDeletion(userData, accessTokenId);
    }

    private static void nullPassword(UUID accessTokenId) {
        Response nullPasswordResponse = AccountActions.getDeleteAccountResponse(getServerPort(), accessTokenId, new OneParamRequest<>(null));
        verifyInvalidParam(nullPasswordResponse, "password", "must not be null");
    }

    private static UUID incorrectPassword(RegistrationParameters userData, UUID accessTokenId) {
        UUID ati = accessTokenId;
        Stream.generate(() -> "")
            .limit(2)
            .forEach(s -> {
                Response incorrectPasswordResponse = AccountActions.getDeleteAccountResponse(getServerPort(), ati, new OneParamRequest<>(DataConstants.INCORRECT_PASSWORD));
                ResponseValidator.verifyBadRequest(incorrectPasswordResponse, ErrorCode.INCORRECT_PASSWORD);
            });

        Response accountLockedResponse = AccountActions.getDeleteAccountResponse(getServerPort(), accessTokenId, new OneParamRequest<>(DataConstants.INCORRECT_PASSWORD));
        verifyErrorResponse(accountLockedResponse, 401, ErrorCode.ACCOUNT_LOCKED);

        DatabaseUtil.unlockUserByEmail(userData.getEmail());
        accessTokenId = IndexPageActions.login(getServerPort(), userData.toLoginRequest());
        return accessTokenId;
    }

    private static void successfulDeletion(RegistrationParameters userData, UUID accessTokenId) {
        Response response = AccountActions.getDeleteAccountResponse(getServerPort(), accessTokenId, new OneParamRequest<>(userData.getPassword()));
        assertThat(response.getStatusCode()).isEqualTo(200);
        Response loginResponse = IndexPageActions.getLoginResponse(getServerPort(), userData.toLoginRequest());
        assertThat(loginResponse.getStatusCode()).isEqualTo(401);
        ErrorResponse errorResponse = loginResponse.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.BAD_CREDENTIALS.name());
    }
}
