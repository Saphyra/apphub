package com.github.saphyra.apphub.service.user.data.service.register;

import com.github.saphyra.apphub.api.etc.user.model.account.RegistrationRequest;
import com.github.saphyra.apphub.lib.common_util.CommonConfigProperties;
import com.github.saphyra.apphub.service.user.data.service.validator.EmailValidator;
import com.github.saphyra.apphub.service.user.data.service.validator.PasswordValidator;
import com.github.saphyra.apphub.service.user.data.service.validator.UsernameValidator;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class RegistrationRequestValidatorTest {
    private static final String EMAIL = "asd@asd.asd";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String LOCALE = "locale";

    @Mock
    private EmailValidator emailValidator;

    @Mock
    private PasswordValidator passwordValidator;

    @Mock
    private UsernameValidator usernameValidator;

    @Mock
    private CommonConfigProperties commonConfigProperties;

    @InjectMocks
    private RegistrationRequestValidator underTest;

    @Test
    void nullLanguage(){
        given(commonConfigProperties.getSupportedLocales()).willReturn(List.of());

        RegistrationRequest registrationRequest = RegistrationRequest.builder()
            .username(USERNAME)
            .email(EMAIL)
            .password(PASSWORD)
            .language(null)
            .build();

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(registrationRequest), "language", "must not be null");
    }

    @Test
    void unsupportedLanguage(){
        given(commonConfigProperties.getSupportedLocales()).willReturn(List.of());

        RegistrationRequest registrationRequest = RegistrationRequest.builder()
            .username(USERNAME)
            .email(EMAIL)
            .password(PASSWORD)
            .language(LOCALE)
            .build();

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(registrationRequest), "language", "must be one of []");
    }

    @Test
    public void valid() {
        given(commonConfigProperties.getSupportedLocales()).willReturn(List.of(LOCALE));

        RegistrationRequest registrationRequest = RegistrationRequest.builder()
            .username(USERNAME)
            .email(EMAIL)
            .password(PASSWORD)
            .language(LOCALE)
            .build();

        underTest.validate(registrationRequest);

        verify(emailValidator).validateEmail(EMAIL);
        verify(usernameValidator).validateUsername(USERNAME);
        verify(passwordValidator).validatePassword(PASSWORD);
    }
}