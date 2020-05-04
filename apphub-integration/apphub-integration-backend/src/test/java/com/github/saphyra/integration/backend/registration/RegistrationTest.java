package com.github.saphyra.integration.backend.registration;

import com.github.saphyra.apphub.integration.backend.actions.IndexPageActions;
import com.github.saphyra.apphub.integration.common.TestBase;
import com.github.saphyra.apphub.integration.common.framework.ErrorCode;
import com.github.saphyra.apphub.integration.common.model.ErrorResponse;
import com.github.saphyra.apphub.integration.common.model.RegistrationParameters;
import com.github.saphyra.apphub.integration.common.model.RegistrationRequest;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class RegistrationTest extends TestBase {
    @Test
    public void register_emailInvalid() {
        RegistrationRequest registrationRequest = RegistrationParameters.invalidEmailParameters()
            .toRegistrationRequest();

        Response response = IndexPageActions.getRegistrationResponse(registrationRequest);

        assertThat(response.getStatusCode()).isEqualTo(400);

        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getHttpStatus()).isEqualTo(400);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo("Érvénytelen paraméter.");
        assertThat(errorResponse.getParams().get("email")).isEqualTo("invalid format");
    }

    @Test
    public void register_emailAlreadyExists() {
        RegistrationRequest existingEmailRegistrationRequest = RegistrationParameters.validParameters()
            .toRegistrationRequest();
        IndexPageActions.registerUser(existingEmailRegistrationRequest);

        RegistrationRequest registrationRequest = RegistrationParameters.validParameters()
            .toBuilder()
            .email(existingEmailRegistrationRequest.getEmail())
            .build()
            .toRegistrationRequest();

        Response response = IndexPageActions.getRegistrationResponse(registrationRequest);

        assertThat(response.getStatusCode()).isEqualTo(409);

        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getHttpStatus()).isEqualTo(409);
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo("Az email foglalt.");
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.EMAIL_ALREADY_EXISTS.name());
    }

    @Test
    public void register_usernameTooShort() {
        RegistrationRequest registrationRequest = RegistrationParameters.tooShortUsernameParameters()
            .toRegistrationRequest();

        Response response = IndexPageActions.getRegistrationResponse(registrationRequest);

        assertThat(response.getStatusCode()).isEqualTo(400);

        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getHttpStatus()).isEqualTo(400);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.USERNAME_TOO_SHORT.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo("A felhasználónév túl rövid. (Minimum 3 karakter)");
    }

    @Test
    public void register_usernameTooLong() {
        RegistrationRequest registrationRequest = RegistrationParameters.tooLongUsernameParameters()
            .toRegistrationRequest();

        Response response = IndexPageActions.getRegistrationResponse(registrationRequest);

        assertThat(response.getStatusCode()).isEqualTo(400);

        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getHttpStatus()).isEqualTo(400);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.USERNAME_TOO_LONG.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo("A felhasználónév túl hosszú. (Maximum 30 karakter)");
    }

    @Test
    public void register_usernameAlreadyExists() {
        RegistrationRequest existingEmailRegistrationRequest = RegistrationParameters.validParameters()
            .toRegistrationRequest();
        IndexPageActions.registerUser(existingEmailRegistrationRequest);

        RegistrationRequest registrationRequest = RegistrationParameters.validParameters()
            .toBuilder()
            .username(existingEmailRegistrationRequest.getUsername())
            .build()
            .toRegistrationRequest();

        Response response = IndexPageActions.getRegistrationResponse(registrationRequest);

        assertThat(response.getStatusCode()).isEqualTo(409);

        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getHttpStatus()).isEqualTo(409);
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo("A felhasználónév foglalt.");
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.USERNAME_ALREADY_EXISTS.name());
    }

    @Test
    public void register_passwordTooShort() {
        RegistrationRequest registrationRequest = RegistrationParameters.tooShortPasswordParameters()
            .toRegistrationRequest();

        Response response = IndexPageActions.getRegistrationResponse(registrationRequest);

        assertThat(response.getStatusCode()).isEqualTo(400);

        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getHttpStatus()).isEqualTo(400);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.PASSWORD_TOO_SHORT.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo("A jelszó túl rövid. (Minimum 6 karakter)");
    }

    @Test
    public void register_passwordTooLong() {
        RegistrationRequest registrationRequest = RegistrationParameters.tooLongPasswordParameters()
            .toRegistrationRequest();

        Response response = IndexPageActions.getRegistrationResponse(registrationRequest);

        assertThat(response.getStatusCode()).isEqualTo(400);

        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getHttpStatus()).isEqualTo(400);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.PASSWORD_TOO_LONG.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo("A jelszó túl hosszú. (Maximum 30 karakter)");
    }
}
