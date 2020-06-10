package com.github.saphyra.apphub.integration.common.model;

import com.github.saphyra.apphub.integration.common.framework.RandomDataProvider;
import lombok.Builder;
import lombok.Data;

import static com.github.saphyra.apphub.integration.common.framework.DataConstants.INVALID_CONFIRM_PASSWORD;
import static com.github.saphyra.apphub.integration.common.framework.DataConstants.INVALID_EMAIL;
import static com.github.saphyra.apphub.integration.common.framework.DataConstants.TOO_LONG_PASSWORD;
import static com.github.saphyra.apphub.integration.common.framework.DataConstants.TOO_LONG_USERNAME;
import static com.github.saphyra.apphub.integration.common.framework.DataConstants.TOO_SHORT_PASSWORD;
import static com.github.saphyra.apphub.integration.common.framework.DataConstants.TOO_SHORT_USERNAME;
import static com.github.saphyra.apphub.integration.common.framework.DataConstants.VALID_PASSWORD;

@Data
@Builder(toBuilder = true)
public class RegistrationParameters {
    private final String username;
    private final String password;
    private final String email;
    private final String confirmPassword;

    public RegistrationRequest toRegistrationRequest() {
        return RegistrationRequest.builder()
            .username(username)
            .password(password)
            .email(email)
            .build();
    }

    public LoginRequest toLoginRequest() {
        return LoginRequest.builder()
            .email(email)
            .password(password)
            .rememberMe(false)
            .build();
    }
    
    public static RegistrationParameters validParameters() {
        return validParameters(VALID_PASSWORD);
    }

    private static RegistrationParameters validParameters(String password) {
        return RegistrationParameters.builder()
            .email(RandomDataProvider.generateEmail())
            .username(RandomDataProvider.generateUsername())
            .password(password)
            .confirmPassword(password)
            .build();
    }



    public static RegistrationParameters invalidEmailParameters() {
        return validParameters().toBuilder()
            .email(INVALID_EMAIL)
            .build();
    }

    public static RegistrationParameters tooShortUsernameParameters() {
        return validParameters().toBuilder()
            .username(TOO_SHORT_USERNAME)
            .build();
    }

    public static RegistrationParameters tooLongUsernameParameters() {
        return validParameters().toBuilder()
            .username(TOO_LONG_USERNAME)
            .build();
    }

    public static RegistrationParameters tooShortPasswordParameters() {
        return validParameters().toBuilder()
            .password(TOO_SHORT_PASSWORD)
            .confirmPassword(TOO_SHORT_PASSWORD)
            .build();
    }

    public static RegistrationParameters tooLongPasswordParameters() {
        return validParameters().toBuilder()
            .password(TOO_LONG_PASSWORD)
            .confirmPassword(TOO_LONG_PASSWORD)
            .build();
    }

    public static RegistrationParameters incorrectConfirmPasswordParameters() {
        return validParameters().toBuilder()
            .confirmPassword(INVALID_CONFIRM_PASSWORD)
            .build();
    }
}
