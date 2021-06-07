package com.github.saphyra.integration.backend.index;

import com.github.saphyra.apphub.integration.backend.BackEndTest;
import com.github.saphyra.apphub.integration.common.framework.Constants;
import com.github.saphyra.apphub.integration.common.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.common.framework.ErrorCode;
import com.github.saphyra.apphub.integration.common.framework.IndexPageActions;
import com.github.saphyra.apphub.integration.common.framework.localization.Language;
import com.github.saphyra.apphub.integration.common.framework.localization.LocalizationKey;
import com.github.saphyra.apphub.integration.common.framework.localization.LocalizationProperties;
import com.github.saphyra.apphub.integration.common.model.ErrorResponse;
import com.github.saphyra.apphub.integration.common.model.RegistrationParameters;
import com.github.saphyra.apphub.integration.common.model.RegistrationRequest;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class RegistrationTest extends BackEndTest {
    @Test(dataProvider = "languageDataProvider")
    public void register(Language language) {
        //Invalid e-mail
        RegistrationRequest invalidEmailRequest = RegistrationParameters.invalidEmailParameters()
            .toRegistrationRequest();
        Response invalidEmailResponse = IndexPageActions.getRegistrationResponse(language, invalidEmailRequest);
        verifyInvalidParam(language, invalidEmailResponse);

        //Username too short
        RegistrationRequest usernameTooShortRequest = RegistrationParameters.tooShortUsernameParameters()
            .toRegistrationRequest();
        Response usernameTooShortResponse = IndexPageActions.getRegistrationResponse(language, usernameTooShortRequest);
        verifyBadRequest(language, usernameTooShortResponse, ErrorCode.USERNAME_TOO_SHORT);

        //Username too long
        RegistrationRequest usernameTooLongRequest = RegistrationParameters.tooLongUsernameParameters()
            .toRegistrationRequest();
        Response usernameTooLongResponse = IndexPageActions.getRegistrationResponse(language, usernameTooLongRequest);
        verifyBadRequest(language, usernameTooLongResponse, ErrorCode.USERNAME_TOO_LONG);

        //Password too short
        RegistrationRequest passwordTooShortRequest = RegistrationParameters.tooShortPasswordParameters()
            .toRegistrationRequest();
        Response passwordTooShortResponse = IndexPageActions.getRegistrationResponse(language, passwordTooShortRequest);
        verifyBadRequest(language, passwordTooShortResponse, ErrorCode.PASSWORD_TOO_SHORT);

        //Password too long
        RegistrationRequest passwordTooLongRequest = RegistrationParameters.tooLongPasswordParameters()
            .toRegistrationRequest();
        Response passwordTooLongResponse = IndexPageActions.getRegistrationResponse(language, passwordTooLongRequest);
        verifyBadRequest(language, passwordTooLongResponse, ErrorCode.PASSWORD_TOO_LONG);

        //Existing e-mail
        RegistrationRequest existingUserRequest = RegistrationParameters.validParameters()
            .toRegistrationRequest();
        IndexPageActions.registerUser(language, existingUserRequest);

        RegistrationRequest existingEmailRequest = RegistrationParameters.validParameters()
            .toBuilder()
            .email(existingUserRequest.getEmail())
            .build()
            .toRegistrationRequest();
        Response existingEmailResponse = IndexPageActions.getRegistrationResponse(language, existingEmailRequest);
        verifyResponse(language, existingEmailResponse, ErrorCode.EMAIL_ALREADY_EXISTS, 409);

        //Existing username
        RegistrationRequest existingUsernameRequest = RegistrationParameters.validParameters()
            .toBuilder()
            .username(existingUserRequest.getUsername())
            .build()
            .toRegistrationRequest();
        Response existingUsernameResponse = IndexPageActions.getRegistrationResponse(language, existingUsernameRequest);
        verifyResponse(language, existingUsernameResponse, ErrorCode.USERNAME_ALREADY_EXISTS, 409);

        //Successful registration
        RegistrationRequest registrationRequest = RegistrationParameters.validParameters()
            .toRegistrationRequest();
        Response response = IndexPageActions.getRegistrationResponse(language, registrationRequest);

        assertThat(response.getStatusCode()).isEqualTo(200);

        List<String> roles = DatabaseUtil.getRolesByUserId(DatabaseUtil.getUserIdByEmail(registrationRequest.getEmail()));
        assertThat(roles).containsExactlyInAnyOrder(Constants.ROLE_NOTEBOOK, Constants.ROLE_SKYXPLORE);
    }

    private void verifyInvalidParam(Language locale, Response response) {
        ErrorResponse errorResponse = verifyBadRequest(locale, response, ErrorCode.INVALID_PARAM);
        assertThat(errorResponse.getParams().get("email")).isEqualTo("invalid format");
    }

    private ErrorResponse verifyBadRequest(Language locale, Response response, ErrorCode errorCode) {
        return verifyResponse(locale, response, errorCode, 400);
    }

    private ErrorResponse verifyResponse(Language locale, Response response, ErrorCode errorCode, int httpStatus) {
        assertThat(response.getStatusCode()).isEqualTo(httpStatus);
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(errorCode.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(locale, LocalizationKey.fromErrorCode(errorCode)));
        return errorResponse;
    }
}
