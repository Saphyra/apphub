package com.github.saphyra.integration.backend.account;

import com.github.saphyra.apphub.integration.backend.BackEndTest;
import com.github.saphyra.apphub.integration.backend.actions.AccountActions;
import com.github.saphyra.apphub.integration.backend.model.account.ChangeUsernameRequest;
import com.github.saphyra.apphub.integration.common.framework.DataConstants;
import com.github.saphyra.apphub.integration.common.framework.ErrorCode;
import com.github.saphyra.apphub.integration.common.framework.IndexPageActions;
import com.github.saphyra.apphub.integration.common.framework.RandomDataProvider;
import com.github.saphyra.apphub.integration.common.framework.localization.Language;
import com.github.saphyra.apphub.integration.common.framework.localization.LocalizationKey;
import com.github.saphyra.apphub.integration.common.framework.localization.LocalizationProperties;
import com.github.saphyra.apphub.integration.common.model.ErrorResponse;
import com.github.saphyra.apphub.integration.common.model.RegistrationParameters;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class ChangeUsernameTest extends BackEndTest {
    @Test(dataProvider = "languageDataProvider")
    public void changeUsername(Language language) {
        RegistrationParameters userData1 = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData1);

        //Null username
        ChangeUsernameRequest nullUsernameRequest = ChangeUsernameRequest.builder()
            .username(null)
            .password(userData1.getPassword())
            .build();
        Response nullUsernameResponse = AccountActions.getChangeUsernameResponse(language, accessTokenId, nullUsernameRequest);
        verifyInvalidParam(language, nullUsernameResponse, "username", "must not be null");

        //Too short username
        ChangeUsernameRequest tooShortUsernameRequest = ChangeUsernameRequest.builder()
            .username(DataConstants.TOO_SHORT_USERNAME)
            .password(userData1.getPassword())
            .build();
        Response tooShortUsernameResponse = AccountActions.getChangeUsernameResponse(language, accessTokenId, tooShortUsernameRequest);
        verifyBadRequest(language, tooShortUsernameResponse, ErrorCode.USERNAME_TOO_SHORT);

        //Too long username
        ChangeUsernameRequest tooLongUsernameRequest = ChangeUsernameRequest.builder()
            .username(DataConstants.TOO_LONG_USERNAME)
            .password(userData1.getPassword())
            .build();
        Response tooLongUsernameResponse = AccountActions.getChangeUsernameResponse(language, accessTokenId, tooLongUsernameRequest);
        verifyBadRequest(language, tooLongUsernameResponse, ErrorCode.USERNAME_TOO_LONG);

        //Username already exists
        RegistrationParameters userData2 = RegistrationParameters.validParameters();
        IndexPageActions.registerAndLogin(language, userData2);

        ChangeUsernameRequest usernameAlreadyExistsRequest = ChangeUsernameRequest.builder()
            .username(userData2.getUsername())
            .password(userData1.getPassword())
            .build();
        Response usernameAlreadyExistsResponse = AccountActions.getChangeUsernameResponse(language, accessTokenId, usernameAlreadyExistsRequest);
        verifyError(language, usernameAlreadyExistsResponse, ErrorCode.USERNAME_ALREADY_EXISTS, 409);

        //Null password
        ChangeUsernameRequest nullPasswordRequest = ChangeUsernameRequest.builder()
            .username(RandomDataProvider.generateUsername())
            .password(null)
            .build();
        Response nullPasswordResponse = AccountActions.getChangeUsernameResponse(language, accessTokenId, nullPasswordRequest);
        verifyInvalidParam(language, nullPasswordResponse, "password", "must not be null");

        //Incorrect password
        ChangeUsernameRequest incorrectPasswordRequest = ChangeUsernameRequest.builder()
            .username(RandomDataProvider.generateUsername())
            .password(DataConstants.INVALID_PASSWORD)
            .build();
        Response incorrectPasswordResponse = AccountActions.getChangeUsernameResponse(language, accessTokenId, incorrectPasswordRequest);
        verifyError(language, incorrectPasswordResponse, ErrorCode.BAD_PASSWORD, 400);

        //Successful change
        ChangeUsernameRequest successfulChangeRequest = ChangeUsernameRequest.builder()
            .username(RandomDataProvider.generateUsername())
            .password(userData1.getPassword())
            .build();
        Response successfulChangeResponse = AccountActions.getChangeUsernameResponse(language, accessTokenId, successfulChangeRequest);
        assertThat(successfulChangeResponse.getStatusCode()).isEqualTo(200);
    }

    private void verifyInvalidParam(Language locale, Response response, String field, String value) {
        ErrorResponse errorResponse = verifyBadRequest(locale, response, ErrorCode.INVALID_PARAM);
        assertThat(errorResponse.getParams().get(field)).isEqualTo(value);
    }

    private ErrorResponse verifyBadRequest(Language locale, Response response, ErrorCode errorCode) {
        return verifyError(locale, response, errorCode, 400);
    }

    private ErrorResponse verifyError(Language locale, Response response, ErrorCode errorCode, int httpStatus) {
        assertThat(response.getStatusCode()).isEqualTo(httpStatus);
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(errorCode.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(locale, LocalizationKey.fromErrorCode(errorCode)));
        return errorResponse;
    }
}
