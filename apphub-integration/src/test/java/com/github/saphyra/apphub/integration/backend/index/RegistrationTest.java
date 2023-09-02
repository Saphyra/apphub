package com.github.saphyra.apphub.integration.backend.index;

import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.ErrorCode;
import com.github.saphyra.apphub.integration.localization.Language;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationRequest;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.List;

import static com.github.saphyra.apphub.integration.framework.ResponseValidator.verifyErrorResponse;
import static com.github.saphyra.apphub.integration.framework.ResponseValidator.verifyInvalidParam;
import static org.assertj.core.api.Assertions.assertThat;

public class RegistrationTest extends BackEndTest {
    @Test(dataProvider = "languageDataProvider", groups = {"be", "index"})
    public void register(Language language) {
        invalidEmail(language);
        usernameTooShort(language);
        usernameTooLong(language);
        passwordTooShort(language);
        passwordTooLong(language);
        RegistrationRequest existingUserRequest = existingEmail(language);
        existingUsername(language, existingUserRequest);
        successfulRegistration(language);
    }

    private static void invalidEmail(Language language) {
        RegistrationRequest invalidEmailRequest = RegistrationParameters.invalidEmailParameters()
            .toRegistrationRequest();
        Response invalidEmailResponse = IndexPageActions.getRegistrationResponse(language, invalidEmailRequest);
        verifyInvalidParam(language, invalidEmailResponse, "email", "invalid format");
    }

    private static void usernameTooShort(Language language) {
        RegistrationRequest usernameTooShortRequest = RegistrationParameters.tooShortUsernameParameters()
            .toRegistrationRequest();
        Response usernameTooShortResponse = IndexPageActions.getRegistrationResponse(language, usernameTooShortRequest);
        verifyInvalidParam(language, usernameTooShortResponse, "username", "too short");
    }

    private static void usernameTooLong(Language language) {
        RegistrationRequest usernameTooLongRequest = RegistrationParameters.tooLongUsernameParameters()
            .toRegistrationRequest();
        Response usernameTooLongResponse = IndexPageActions.getRegistrationResponse(language, usernameTooLongRequest);
        verifyInvalidParam(language, usernameTooLongResponse, "username", "too long");
    }

    private static void passwordTooShort(Language language) {
        RegistrationRequest passwordTooShortRequest = RegistrationParameters.tooShortPasswordParameters()
            .toRegistrationRequest();
        Response passwordTooShortResponse = IndexPageActions.getRegistrationResponse(language, passwordTooShortRequest);
        verifyInvalidParam(language, passwordTooShortResponse, "password", "too short");
    }

    private static void passwordTooLong(Language language) {
        RegistrationRequest passwordTooLongRequest = RegistrationParameters.tooLongPasswordParameters()
            .toRegistrationRequest();
        Response passwordTooLongResponse = IndexPageActions.getRegistrationResponse(language, passwordTooLongRequest);
        verifyInvalidParam(language, passwordTooLongResponse, "password", "too long");
    }

    private static RegistrationRequest existingEmail(Language language) {
        RegistrationRequest existingUserRequest = RegistrationParameters.validParameters()
            .toRegistrationRequest();
        IndexPageActions.registerUser(language, existingUserRequest);

        RegistrationRequest existingEmailRequest = RegistrationParameters.validParameters()
            .toBuilder()
            .email(existingUserRequest.getEmail())
            .build()
            .toRegistrationRequest();
        Response existingEmailResponse = IndexPageActions.getRegistrationResponse(language, existingEmailRequest);
        verifyErrorResponse(language, existingEmailResponse, 409, ErrorCode.EMAIL_ALREADY_EXISTS);
        return existingUserRequest;
    }

    private static void existingUsername(Language language, RegistrationRequest existingUserRequest) {
        RegistrationRequest existingUsernameRequest = RegistrationParameters.validParameters()
            .toBuilder()
            .username(existingUserRequest.getUsername())
            .build()
            .toRegistrationRequest();
        Response existingUsernameResponse = IndexPageActions.getRegistrationResponse(language, existingUsernameRequest);
        verifyErrorResponse(language, existingUsernameResponse, 409, ErrorCode.USERNAME_ALREADY_EXISTS);
    }

    private static void successfulRegistration(Language language) {
        RegistrationRequest registrationRequest = RegistrationParameters.validParameters()
            .toRegistrationRequest();
        Response response = IndexPageActions.getRegistrationResponse(language, registrationRequest);
        assertThat(response.getStatusCode()).isEqualTo(200);
        List<String> roles = DatabaseUtil.getRolesByUserId(DatabaseUtil.getUserIdByEmail(registrationRequest.getEmail()));
        assertThat(roles).containsExactlyInAnyOrder(
            Constants.ROLE_NOTEBOOK,
            Constants.ROLE_SKYXPLORE,
            Constants.ROLE_ACCESS,
            Constants.ROLE_TRAINING,
            Constants.ROLE_UTILS,
            Constants.ROLE_COMMUNITY,
            Constants.ROLE_CALENDAR
        );
    }
}
