package com.github.saphyra.apphub.integration.backend.account;

import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.action.backend.AccountActions;
import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.framework.DataConstants;
import com.github.saphyra.apphub.integration.framework.ErrorCode;
import com.github.saphyra.apphub.integration.framework.RandomDataProvider;
import com.github.saphyra.apphub.integration.localization.Language;
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
    @Test(dataProvider = "languageDataProvider", groups = {"be", "account"})
    public void changeUsername(Language language) {
        RegistrationParameters userData1 = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData1);

        nullUsername(language, userData1, accessTokenId);
        tooShortUsername(language, userData1, accessTokenId);
        tooLongUsername(language, userData1, accessTokenId);
        usernameAlreadyExists(language, userData1, accessTokenId);
        nullPassword(language, accessTokenId);
        incorrectPassword(language, accessTokenId);
        successfulChange(language, userData1, accessTokenId);
    }

    private static void nullUsername(Language language, RegistrationParameters userData1, UUID accessTokenId) {
        ChangeUsernameRequest nullUsernameRequest = ChangeUsernameRequest.builder()
            .username(null)
            .password(userData1.getPassword())
            .build();
        Response nullUsernameResponse = AccountActions.getChangeUsernameResponse(language, accessTokenId, nullUsernameRequest);
        verifyInvalidParam(language, nullUsernameResponse, "username", "must not be null");
    }

    private static void tooShortUsername(Language language, RegistrationParameters userData1, UUID accessTokenId) {
        ChangeUsernameRequest tooShortUsernameRequest = ChangeUsernameRequest.builder()
            .username(DataConstants.TOO_SHORT_USERNAME)
            .password(userData1.getPassword())
            .build();
        Response tooShortUsernameResponse = AccountActions.getChangeUsernameResponse(language, accessTokenId, tooShortUsernameRequest);
        verifyInvalidParam(language, tooShortUsernameResponse, "username", "too short");
    }

    private static void tooLongUsername(Language language, RegistrationParameters userData1, UUID accessTokenId) {
        ChangeUsernameRequest tooLongUsernameRequest = ChangeUsernameRequest.builder()
            .username(DataConstants.TOO_LONG_USERNAME)
            .password(userData1.getPassword())
            .build();
        Response tooLongUsernameResponse = AccountActions.getChangeUsernameResponse(language, accessTokenId, tooLongUsernameRequest);
        verifyInvalidParam(language, tooLongUsernameResponse, "username", "too long");
    }

    private static void usernameAlreadyExists(Language language, RegistrationParameters userData1, UUID accessTokenId) {
        RegistrationParameters userData2 = RegistrationParameters.validParameters();
        IndexPageActions.registerAndLogin(language, userData2);

        ChangeUsernameRequest usernameAlreadyExistsRequest = ChangeUsernameRequest.builder()
            .username(userData2.getUsername())
            .password(userData1.getPassword())
            .build();
        Response usernameAlreadyExistsResponse = AccountActions.getChangeUsernameResponse(language, accessTokenId, usernameAlreadyExistsRequest);
        verifyErrorResponse(language, usernameAlreadyExistsResponse, 409, ErrorCode.USERNAME_ALREADY_EXISTS);
    }

    private static void nullPassword(Language language, UUID accessTokenId) {
        ChangeUsernameRequest nullPasswordRequest = ChangeUsernameRequest.builder()
            .username(RandomDataProvider.generateUsername())
            .password(null)
            .build();
        Response nullPasswordResponse = AccountActions.getChangeUsernameResponse(language, accessTokenId, nullPasswordRequest);
        verifyInvalidParam(language, nullPasswordResponse, "password", "must not be null");
    }

    private static void incorrectPassword(Language language, UUID accessTokenId) {
        ChangeUsernameRequest incorrectPasswordRequest = ChangeUsernameRequest.builder()
            .username(RandomDataProvider.generateUsername())
            .password(DataConstants.INCORRECT_PASSWORD)
            .build();
        Response incorrectPasswordResponse = AccountActions.getChangeUsernameResponse(language, accessTokenId, incorrectPasswordRequest);
        verifyBadRequest(language, incorrectPasswordResponse, ErrorCode.INCORRECT_PASSWORD);
    }

    private static void successfulChange(Language language, RegistrationParameters userData1, UUID accessTokenId) {
        ChangeUsernameRequest successfulChangeRequest = ChangeUsernameRequest.builder()
            .username(RandomDataProvider.generateUsername())
            .password(userData1.getPassword())
            .build();
        Response successfulChangeResponse = AccountActions.getChangeUsernameResponse(language, accessTokenId, successfulChangeRequest);
        assertThat(successfulChangeResponse.getStatusCode()).isEqualTo(200);
    }
}
