package com.github.saphyra.apphub.service.user.data.service.register;

import com.github.saphyra.apphub.api.user.model.request.RegistrationRequest;
import com.github.saphyra.apphub.service.user.data.service.validator.EmailValidator;
import com.github.saphyra.apphub.service.user.data.service.validator.PasswordValidator;
import com.github.saphyra.apphub.service.user.data.service.validator.UsernameValidator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class RegistrationRequestValidatorTest {
    private static final String EMAIL = "asd@asd.asd";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";

    @Mock
    private EmailValidator emailValidator;

    @Mock
    private PasswordValidator passwordValidator;

    @Mock
    private UsernameValidator usernameValidator;

    @InjectMocks
    private RegistrationRequestValidator underTest;

    @Test
    public void validatePassword() {
        RegistrationRequest registrationRequest = RegistrationRequest.builder()
            .username(USERNAME)
            .email(EMAIL)
            .password(PASSWORD)
            .build();

        underTest.validate(registrationRequest);

        verify(emailValidator).validateEmail(EMAIL);
        verify(usernameValidator).validateUsername(USERNAME);
        verify(passwordValidator).validatePassword(PASSWORD);
    }
}