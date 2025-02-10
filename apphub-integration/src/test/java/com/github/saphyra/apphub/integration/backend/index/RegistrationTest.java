package com.github.saphyra.apphub.integration.backend.index;

import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.ErrorCode;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationRequest;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.List;

import static com.github.saphyra.apphub.integration.framework.ResponseValidator.verifyErrorResponse;
import static com.github.saphyra.apphub.integration.framework.ResponseValidator.verifyInvalidParam;
import static org.assertj.core.api.Assertions.assertThat;

public class RegistrationTest extends BackEndTest {
    @Test(groups = {"be", "index"})
    public void register() {
        invalidEmail();
        usernameTooShort();
        usernameTooLong();
        passwordTooShort();
        passwordTooLong();
        RegistrationRequest existingUserRequest = existingEmail();
        existingUsername(existingUserRequest);
        successfulRegistration();
    }

    private static void invalidEmail() {
        RegistrationRequest invalidEmailRequest = RegistrationParameters.invalidEmailParameters()
            .toRegistrationRequest();
        Response invalidEmailResponse = IndexPageActions.getRegistrationResponse(getServerPort(), invalidEmailRequest);
        verifyInvalidParam(invalidEmailResponse, "email", "invalid format");
    }

    private static void usernameTooShort() {
        RegistrationRequest usernameTooShortRequest = RegistrationParameters.tooShortUsernameParameters()
            .toRegistrationRequest();
        Response usernameTooShortResponse = IndexPageActions.getRegistrationResponse(getServerPort(), usernameTooShortRequest);
        verifyInvalidParam(usernameTooShortResponse, "username", "too short");
    }

    private static void usernameTooLong() {
        RegistrationRequest usernameTooLongRequest = RegistrationParameters.tooLongUsernameParameters()
            .toRegistrationRequest();
        Response usernameTooLongResponse = IndexPageActions.getRegistrationResponse(getServerPort(), usernameTooLongRequest);
        verifyInvalidParam(usernameTooLongResponse, "username", "too long");
    }

    private static void passwordTooShort() {
        RegistrationRequest passwordTooShortRequest = RegistrationParameters.tooShortPasswordParameters()
            .toRegistrationRequest();
        Response passwordTooShortResponse = IndexPageActions.getRegistrationResponse(getServerPort(), passwordTooShortRequest);
        verifyInvalidParam(passwordTooShortResponse, "password", "too short");
    }

    private static void passwordTooLong() {
        RegistrationRequest passwordTooLongRequest = RegistrationParameters.tooLongPasswordParameters()
            .toRegistrationRequest();
        Response passwordTooLongResponse = IndexPageActions.getRegistrationResponse(getServerPort(), passwordTooLongRequest);
        verifyInvalidParam(passwordTooLongResponse, "password", "too long");
    }

    private static RegistrationRequest existingEmail() {
        RegistrationRequest existingUserRequest = RegistrationParameters.validParameters()
            .toRegistrationRequest();
        IndexPageActions.registerUser(getServerPort(), existingUserRequest);

        RegistrationRequest existingEmailRequest = RegistrationParameters.validParameters()
            .toBuilder()
            .email(existingUserRequest.getEmail())
            .build()
            .toRegistrationRequest();
        Response existingEmailResponse = IndexPageActions.getRegistrationResponse(getServerPort(), existingEmailRequest);
        verifyErrorResponse(existingEmailResponse, 409, ErrorCode.EMAIL_ALREADY_EXISTS);
        return existingUserRequest;
    }

    private static void existingUsername(RegistrationRequest existingUserRequest) {
        RegistrationRequest existingUsernameRequest = RegistrationParameters.validParameters()
            .toBuilder()
            .username(existingUserRequest.getUsername())
            .build()
            .toRegistrationRequest();
        Response existingUsernameResponse = IndexPageActions.getRegistrationResponse(getServerPort(), existingUsernameRequest);
        verifyErrorResponse(existingUsernameResponse, 409, ErrorCode.USERNAME_ALREADY_EXISTS);
    }

    private static void successfulRegistration() {
        RegistrationRequest registrationRequest = RegistrationParameters.validParameters()
            .toRegistrationRequest();
        Response response = IndexPageActions.getRegistrationResponse(getServerPort(), registrationRequest);
        assertThat(response.getStatusCode()).isEqualTo(200);
        List<String> roles = DatabaseUtil.getRolesByUserId(DatabaseUtil.getUserIdByEmail(registrationRequest.getEmail()));
        assertThat(roles).containsExactlyInAnyOrder(
            Constants.ROLE_NOTEBOOK,
            Constants.ROLE_SKYXPLORE,
            Constants.ROLE_ACCESS,
            Constants.ROLE_TRAINING,
            Constants.ROLE_UTILS,
            Constants.ROLE_COMMUNITY,
            Constants.ROLE_CALENDAR,
            Constants.ROLE_ELITE_BASE
        );
    }
}
