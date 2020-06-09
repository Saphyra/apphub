package com.github.saphyra.apphub.integration.common.model;

import com.github.saphyra.util.IdGenerator;
import lombok.Builder;
import lombok.Data;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@Builder(toBuilder = true)
public class RegistrationParameters {
    private static final IdGenerator ID_GENERATOR = new IdGenerator();

    private static final String VALID_PASSWORD = "valid-password";
    private static final String TOO_SHORT_USERNAME = "to";
    private static final String TOO_SHORT_PASSWORD = "passw";
    private static final String INVALID_CONFIRM_PASSWORD = "invalid-confirm-password";
    private static final String INVALID_EMAIL = "asd";

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
        String[] userNameCharacters = ("user-" + ID_GENERATOR.generateRandomId()).split("");
        String userName = Arrays.stream(userNameCharacters)
            .limit(30)
            .collect(Collectors.joining());
        return RegistrationParameters.builder()
            .email(generateEmail())
            .username(userName)
            .password(password)
            .confirmPassword(password)
            .build();
    }

    public static String generateEmail() {
        return "email-" + ID_GENERATOR.generateRandomId() + "@test.com";
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
        String userName = Stream.generate(() -> "a")
            .limit(31)
            .collect(Collectors.joining());
        return validParameters().toBuilder()
            .username(userName)
            .build();
    }

    public static RegistrationParameters tooShortPasswordParameters() {
        return validParameters().toBuilder()
            .password(TOO_SHORT_PASSWORD)
            .confirmPassword(TOO_SHORT_PASSWORD)
            .build();
    }

    public static RegistrationParameters tooLongPasswordParameters() {
        String password = Stream.generate(() -> "a")
            .limit(31)
            .collect(Collectors.joining());
        return validParameters().toBuilder()
            .password(password)
            .confirmPassword(password)
            .build();
    }

    public static RegistrationParameters incorrectConfirmPasswordParameters() {
        return validParameters().toBuilder()
            .confirmPassword(INVALID_CONFIRM_PASSWORD)
            .build();
    }
}
