package com.github.saphyra.apphub.integration.structure.api.user;

import com.github.saphyra.apphub.integration.framework.DataConstants;
import com.github.saphyra.apphub.integration.framework.RandomDataProvider;
import lombok.Builder;
import lombok.Data;

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
            .userIdentifier(email)
            .password(password)
            .rememberMe(false)
            .build();
    }

    public static RegistrationParameters validParameters() {
        return validParameters(DataConstants.VALID_PASSWORD);
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
            .email(DataConstants.INVALID_EMAIL)
            .build();
    }

    public static RegistrationParameters tooShortUsernameParameters() {
        return validParameters().toBuilder()
            .username(DataConstants.TOO_SHORT_USERNAME)
            .build();
    }

    public static RegistrationParameters tooLongUsernameParameters() {
        return validParameters().toBuilder()
            .username(DataConstants.TOO_LONG_USERNAME)
            .build();
    }

    public static RegistrationParameters tooShortPasswordParameters() {
        return validParameters().toBuilder()
            .password(DataConstants.TOO_SHORT_PASSWORD)
            .confirmPassword(DataConstants.TOO_SHORT_PASSWORD)
            .build();
    }

    public static RegistrationParameters tooLongPasswordParameters() {
        return validParameters().toBuilder()
            .password(DataConstants.TOO_LONG_PASSWORD)
            .confirmPassword(DataConstants.TOO_LONG_PASSWORD)
            .build();
    }

    public static RegistrationParameters incorrectConfirmPasswordParameters() {
        return validParameters().toBuilder()
            .confirmPassword(DataConstants.INVALID_CONFIRM_PASSWORD)
            .build();
    }
}
