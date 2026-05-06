package com.github.saphyra.apphub.integration.backend.account;

import com.github.saphyra.apphub.integration.action.backend.AccountActions;
import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.DataConstants;
import com.github.saphyra.apphub.integration.framework.ErrorCode;
import com.github.saphyra.apphub.integration.framework.RandomDataProvider;
import com.github.saphyra.apphub.integration.structure.api.user.ChangeUsernameRequest;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static com.github.saphyra.apphub.integration.framework.ResponseValidator.verifyBadRequest;
import static com.github.saphyra.apphub.integration.framework.ResponseValidator.verifyErrorResponse;
import static com.github.saphyra.apphub.integration.framework.ResponseValidator.verifyInvalidParam;
import static org.assertj.core.api.Assertions.assertThat;

public class ChangeUsernameTest extends BackEndTest {
    @Test(groups = {"be", "account"})
    public void changeUsername() {
        RegistrationParameters userData1 = RegistrationParameters.validParameters();
        String accessToken = IndexPageActions.registerAndLogin(getServerPort(), userData1);

        nullUsername(userData1, accessToken);
        tooShortUsername(userData1, accessToken);
        tooLongUsername(userData1, accessToken);
        usernameAlreadyExists(userData1, accessToken);
        nullPassword(accessToken);
        incorrectPassword(accessToken);
        successfulChange(userData1, accessToken);
    }

    private static void nullUsername(RegistrationParameters userData1, String accessToken) {
        ChangeUsernameRequest nullUsernameRequest = ChangeUsernameRequest.builder()
            .username(null)
            .password(userData1.getPassword())
            .build();
        Response nullUsernameResponse = AccountActions.getChangeUsernameResponse(getServerPort(), accessToken, nullUsernameRequest);
        verifyInvalidParam(nullUsernameResponse, "username", "must not be null");
    }

    private static void tooShortUsername(RegistrationParameters userData1, String accessToken) {
        ChangeUsernameRequest tooShortUsernameRequest = ChangeUsernameRequest.builder()
            .username(DataConstants.TOO_SHORT_USERNAME)
            .password(userData1.getPassword())
            .build();
        Response tooShortUsernameResponse = AccountActions.getChangeUsernameResponse(getServerPort(), accessToken, tooShortUsernameRequest);
        verifyInvalidParam(tooShortUsernameResponse, "username", "too short");
    }

    private static void tooLongUsername(RegistrationParameters userData1, String accessToken) {
        ChangeUsernameRequest tooLongUsernameRequest = ChangeUsernameRequest.builder()
            .username(DataConstants.TOO_LONG_USERNAME)
            .password(userData1.getPassword())
            .build();
        Response tooLongUsernameResponse = AccountActions.getChangeUsernameResponse(getServerPort(), accessToken, tooLongUsernameRequest);
        verifyInvalidParam(tooLongUsernameResponse, "username", "too long");
    }

    private static void usernameAlreadyExists(RegistrationParameters userData1, String accessToken) {
        RegistrationParameters userData2 = RegistrationParameters.validParameters();
        IndexPageActions.registerAndLogin(getServerPort(), userData2);

        ChangeUsernameRequest usernameAlreadyExistsRequest = ChangeUsernameRequest.builder()
            .username(userData2.getUsername())
            .password(userData1.getPassword())
            .build();
        Response usernameAlreadyExistsResponse = AccountActions.getChangeUsernameResponse(getServerPort(), accessToken, usernameAlreadyExistsRequest);
        verifyErrorResponse(usernameAlreadyExistsResponse, 409, ErrorCode.USERNAME_ALREADY_EXISTS);
    }

    private static void nullPassword(String accessToken) {
        ChangeUsernameRequest nullPasswordRequest = ChangeUsernameRequest.builder()
            .username(RandomDataProvider.generateUsername())
            .password(null)
            .build();
        Response nullPasswordResponse = AccountActions.getChangeUsernameResponse(getServerPort(), accessToken, nullPasswordRequest);
        verifyInvalidParam(nullPasswordResponse, "password", "must not be null");
    }

    private static void incorrectPassword(String accessToken) {
        ChangeUsernameRequest incorrectPasswordRequest = ChangeUsernameRequest.builder()
            .username(RandomDataProvider.generateUsername())
            .password(DataConstants.INCORRECT_PASSWORD)
            .build();
        Response incorrectPasswordResponse = AccountActions.getChangeUsernameResponse(getServerPort(), accessToken, incorrectPasswordRequest);
        verifyBadRequest(incorrectPasswordResponse, ErrorCode.INCORRECT_PASSWORD);
    }

    private static void successfulChange(RegistrationParameters userData1, String accessToken) {
        ChangeUsernameRequest successfulChangeRequest = ChangeUsernameRequest.builder()
            .username(RandomDataProvider.generateUsername())
            .password(userData1.getPassword())
            .build();
        Response successfulChangeResponse = AccountActions.getChangeUsernameResponse(getServerPort(), accessToken, successfulChangeRequest);
        assertThat(successfulChangeResponse.getStatusCode()).isEqualTo(200);
    }
}
