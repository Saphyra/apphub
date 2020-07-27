package com.github.saphyra.integration.backend.index;

import com.github.saphyra.apphub.integration.common.framework.IndexPageActions;
import com.github.saphyra.apphub.integration.common.TestBase;
import com.github.saphyra.apphub.integration.common.framework.Constants;
import com.github.saphyra.apphub.integration.common.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.common.framework.ErrorCode;
import com.github.saphyra.apphub.integration.common.framework.localization.Language;
import com.github.saphyra.apphub.integration.common.framework.localization.LocalizationKey;
import com.github.saphyra.apphub.integration.common.framework.localization.LocalizationProperties;
import com.github.saphyra.apphub.integration.common.model.ErrorResponse;
import com.github.saphyra.apphub.integration.common.model.RegistrationParameters;
import com.github.saphyra.apphub.integration.common.model.RegistrationRequest;
import io.restassured.response.Response;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;

import static com.github.saphyra.apphub.integration.common.framework.localization.LocalizationKey.ERROR_CODE_PASSWORD_TOO_LONG;
import static com.github.saphyra.apphub.integration.common.framework.localization.LocalizationKey.ERROR_CODE_PASSWORD_TOO_SHORT;
import static com.github.saphyra.apphub.integration.common.framework.localization.LocalizationKey.ERROR_CODE_USERNAME_ALREADY_EXISTS;
import static com.github.saphyra.apphub.integration.common.framework.localization.LocalizationKey.ERROR_CODE_USERNAME_TOO_LONG;
import static com.github.saphyra.apphub.integration.common.framework.localization.LocalizationKey.ERROR_CODE_USERNAME_TOO_SHORT;
import static org.assertj.core.api.Assertions.assertThat;

public class RegistrationTest extends TestBase {
    @DataProvider(name = "localeDataProvider", parallel = true)
    public Object[] localeDataProvider() {
        return Language.values();
    }

    @Test(dataProvider = "localeDataProvider")
    public void register_emailInvalid(Language locale) {
        RegistrationRequest registrationRequest = RegistrationParameters.invalidEmailParameters()
            .toRegistrationRequest();

        Response response = IndexPageActions.getRegistrationResponse(locale, registrationRequest);

        assertThat(response.getStatusCode()).isEqualTo(400);

        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(locale, LocalizationKey.ERROR_CODE_INVALID_PARAM));
        assertThat(errorResponse.getParams().get("email")).isEqualTo("invalid format");
    }

    @Test(dataProvider = "localeDataProvider")
    public void register_emailAlreadyExists(Language locale) {
        RegistrationRequest existingEmailRegistrationRequest = RegistrationParameters.validParameters()
            .toRegistrationRequest();
        IndexPageActions.registerUser(locale, existingEmailRegistrationRequest);

        RegistrationRequest registrationRequest = RegistrationParameters.validParameters()
            .toBuilder()
            .email(existingEmailRegistrationRequest.getEmail())
            .build()
            .toRegistrationRequest();

        Response response = IndexPageActions.getRegistrationResponse(locale, registrationRequest);

        assertThat(response.getStatusCode()).isEqualTo(409);

        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(locale, LocalizationKey.ERROR_CODE_EMAIL_ALREADY_IN_USE));
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.EMAIL_ALREADY_EXISTS.name());
    }

    @Test(dataProvider = "localeDataProvider")
    public void register_usernameTooShort(Language locale) {
        RegistrationRequest registrationRequest = RegistrationParameters.tooShortUsernameParameters()
            .toRegistrationRequest();

        Response response = IndexPageActions.getRegistrationResponse(locale, registrationRequest);

        assertThat(response.getStatusCode()).isEqualTo(400);

        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.USERNAME_TOO_SHORT.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(locale, ERROR_CODE_USERNAME_TOO_SHORT));
    }

    @Test(dataProvider = "localeDataProvider")
    public void register_usernameTooLong(Language locale) {
        RegistrationRequest registrationRequest = RegistrationParameters.tooLongUsernameParameters()
            .toRegistrationRequest();

        Response response = IndexPageActions.getRegistrationResponse(locale, registrationRequest);

        assertThat(response.getStatusCode()).isEqualTo(400);

        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.USERNAME_TOO_LONG.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(locale, ERROR_CODE_USERNAME_TOO_LONG));
    }

    @Test(dataProvider = "localeDataProvider")
    public void register_usernameAlreadyExists(Language locale) {
        RegistrationRequest existingEmailRegistrationRequest = RegistrationParameters.validParameters()
            .toRegistrationRequest();
        IndexPageActions.registerUser(locale, existingEmailRegistrationRequest);

        RegistrationRequest registrationRequest = RegistrationParameters.validParameters()
            .toBuilder()
            .username(existingEmailRegistrationRequest.getUsername())
            .build()
            .toRegistrationRequest();

        Response response = IndexPageActions.getRegistrationResponse(locale, registrationRequest);

        assertThat(response.getStatusCode()).isEqualTo(409);

        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(locale, ERROR_CODE_USERNAME_ALREADY_EXISTS));
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.USERNAME_ALREADY_EXISTS.name());
    }

    @Test(dataProvider = "localeDataProvider")
    public void register_passwordTooShort(Language locale) {
        RegistrationRequest registrationRequest = RegistrationParameters.tooShortPasswordParameters()
            .toRegistrationRequest();

        Response response = IndexPageActions.getRegistrationResponse(locale, registrationRequest);

        assertThat(response.getStatusCode()).isEqualTo(400);

        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.PASSWORD_TOO_SHORT.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(locale, ERROR_CODE_PASSWORD_TOO_SHORT));
    }

    @Test(dataProvider = "localeDataProvider")
    public void register_passwordTooLong(Language locale) {
        RegistrationRequest registrationRequest = RegistrationParameters.tooLongPasswordParameters()
            .toRegistrationRequest();

        Response response = IndexPageActions.getRegistrationResponse(locale, registrationRequest);

        assertThat(response.getStatusCode()).isEqualTo(400);

        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.PASSWORD_TOO_LONG.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(locale, ERROR_CODE_PASSWORD_TOO_LONG));
    }

    @Test
    public void successfulRegistration(){
        Language locale = Language.HUNGARIAN;

        RegistrationRequest registrationRequest = RegistrationParameters.validParameters()
            .toRegistrationRequest();
        Response response = IndexPageActions.getRegistrationResponse(locale, registrationRequest);

        assertThat(response.getStatusCode()).isEqualTo(200);

        List<String> roles = DatabaseUtil.getRolesByUserId(DatabaseUtil.getUserIdByEmail(registrationRequest.getEmail()));
        assertThat(roles).containsExactlyInAnyOrder(Constants.ROLE_NOTEBOOK);
    }
}
