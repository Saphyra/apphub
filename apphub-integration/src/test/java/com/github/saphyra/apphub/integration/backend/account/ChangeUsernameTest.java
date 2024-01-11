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

import java.util.UUID;

import static com.github.saphyra.apphub.integration.framework.ResponseValidator.verifyBadRequest;
import static com.github.saphyra.apphub.integration.framework.ResponseValidator.verifyErrorResponse;
import static com.github.saphyra.apphub.integration.framework.ResponseValidator.verifyInvalidParam;
import static org.assertj.core.api.Assertions.assertThat;

public class ChangeUsernameTest extends BackEndTest {
    @Test(groups = {"be", "account"})
    public void changeUsername() {
        RegistrationParameters userData1 = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(userData1);

        nullUsername(userData1, accessTokenId);
        tooShortUsername(userData1, accessTokenId);
        tooLongUsername(userData1, accessTokenId);
        usernameAlreadyExists(userData1, accessTokenId);
        nullPassword(accessTokenId);
        incorrectPassword(accessTokenId);
        successfulChange(userData1, accessTokenId);
    }

    private static void nullUsername(RegistrationParameters userData1, UUID accessTokenId) {
        ChangeUsernameRequest nullUsernameRequest = ChangeUsernameRequest.builder()
            .username(null)
            .password(userData1.getPassword())
            .build();
        Response nullUsernameResponse = AccountActions.getChangeUsernameResponse(accessTokenId, nullUsernameRequest);
        verifyInvalidParam(nullUsernameResponse, "username", "must not be null");
    }

    private static void tooShortUsername(RegistrationParameters userData1, UUID accessTokenId) {
        ChangeUsernameRequest tooShortUsernameRequest = ChangeUsernameRequest.builder()
            .username(DataConstants.TOO_SHORT_USERNAME)
            .password(userData1.getPassword())
            .build();
        Response tooShortUsernameResponse = AccountActions.getChangeUsernameResponse(accessTokenId, tooShortUsernameRequest);
        verifyInvalidParam(tooShortUsernameResponse, "username", "too short");
    }

    private static void tooLongUsername(RegistrationParameters userData1, UUID accessTokenId) {
        ChangeUsernameRequest tooLongUsernameRequest = ChangeUsernameRequest.builder()
            .username(DataConstants.TOO_LONG_USERNAME)
            .password(userData1.getPassword())
            .build();
        Response tooLongUsernameResponse = AccountActions.getChangeUsernameResponse(accessTokenId, tooLongUsernameRequest);
        verifyInvalidParam(tooLongUsernameResponse, "username", "too long");
    }

    private static void usernameAlreadyExists(RegistrationParameters userData1, UUID accessTokenId) {
        RegistrationParameters userData2 = RegistrationParameters.validParameters();
        IndexPageActions.registerAndLogin(userData2);

        ChangeUsernameRequest usernameAlreadyExistsRequest = ChangeUsernameRequest.builder()
            .username(userData2.getUsername())
            .password(userData1.getPassword())
            .build();
        Response usernameAlreadyExistsResponse = AccountActions.getChangeUsernameResponse(accessTokenId, usernameAlreadyExistsRequest);
        verifyErrorResponse(usernameAlreadyExistsResponse, 409, ErrorCode.USERNAME_ALREADY_EXISTS);
    }

    private static void nullPassword(UUID accessTokenId) {
        ChangeUsernameRequest nullPasswordRequest = ChangeUsernameRequest.builder()
            .username(RandomDataProvider.generateUsername())
            .password(null)
            .build();
        Response nullPasswordResponse = AccountActions.getChangeUsernameResponse(accessTokenId, nullPasswordRequest);
        verifyInvalidParam(nullPasswordResponse, "password", "must not be null");
    }

    private static void incorrectPassword(UUID accessTokenId) {
        ChangeUsernameRequest incorrectPasswordRequest = ChangeUsernameRequest.builder()
            .username(RandomDataProvider.generateUsername())
            .password(DataConstants.INCORRECT_PASSWORD)
            .build();
        Response incorrectPasswordResponse = AccountActions.getChangeUsernameResponse(accessTokenId, incorrectPasswordRequest);
        verifyBadRequest(incorrectPasswordResponse, ErrorCode.INCORRECT_PASSWORD);
    }

    private static void successfulChange(RegistrationParameters userData1, UUID accessTokenId) {
        ChangeUsernameRequest successfulChangeRequest = ChangeUsernameRequest.builder()
            .username(RandomDataProvider.generateUsername())
            .password(userData1.getPassword())
            .build();
        Response successfulChangeResponse = AccountActions.getChangeUsernameResponse(accessTokenId, successfulChangeRequest);
        assertThat(successfulChangeResponse.getStatusCode()).isEqualTo(200);
    }
}
