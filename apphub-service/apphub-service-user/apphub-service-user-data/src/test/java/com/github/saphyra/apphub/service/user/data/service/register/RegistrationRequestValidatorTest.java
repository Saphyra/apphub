package com.github.saphyra.apphub.service.user.data.service.register;

import com.github.saphyra.apphub.api.user.data.model.request.RegistrationRequest;
import com.github.saphyra.apphub.service.user.data.dao.User;
import com.github.saphyra.apphub.service.user.data.dao.UserDao;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class RegistrationRequestValidatorTest {
    private static final String VALID_EMAIL = "asd@asd.asd";
    private static final String VALID_USERNAME = "valid-username";
    private static final String VALID_PASSWORD = "valid-password";
    private static final String INVALID_EMAIL = "invalid-email";
    private static final String TOO_SHORT_USERNAME = "to";
    private static final String TOO_LONG_USERNAME = Stream.generate(() -> "a")
        .limit(31)
        .collect(Collectors.joining());
    private static final String TOO_LONG_PASSWORD = Stream.generate(() -> "a")
        .limit(31)
        .collect(Collectors.joining());

    @Mock
    private UserDao userDao;

    @InjectMocks
    private RegistrationRequestValidator underTest;

    private RegistrationRequest.RegistrationRequestBuilder builder;

    @Mock
    private User user;

    @Before
    public void setUp() {
        given(userDao.findByEmail(VALID_EMAIL)).willReturn(Optional.empty());
        given(userDao.findByUsername(VALID_USERNAME)).willReturn(Optional.empty());

        builder = RegistrationRequest.builder()
            .username(VALID_USERNAME)
            .email(VALID_EMAIL)
            .password(VALID_PASSWORD);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullEmail() {
        underTest.validate(builder.email(null).build());
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidEmail() {
        underTest.validate(builder.email(INVALID_EMAIL).build());
    }

    @Test(expected = IllegalArgumentException.class)
    public void emailAlreadyExists() {
        given(userDao.findByEmail(VALID_EMAIL)).willReturn(Optional.of(user));

        underTest.validate(builder.build());
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullUsername() {
        underTest.validate(builder.username(null).build());
    }

    @Test(expected = IllegalArgumentException.class)
    public void tooShortUsername() {
        underTest.validate(builder.username(TOO_SHORT_USERNAME).build());
    }

    @Test(expected = IllegalArgumentException.class)
    public void tooLongUsername() {
        underTest.validate(builder.username(TOO_LONG_USERNAME).build());

    }

    @Test(expected = IllegalArgumentException.class)
    public void usernameAlreadyExists() {
        given(userDao.findByUsername(VALID_USERNAME)).willReturn(Optional.of(user));

        underTest.validate(builder.build());
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullPassword() {
        underTest.validate(builder.password(null).build());
    }

    @Test(expected = IllegalArgumentException.class)
    public void tooShortPassword() {
        underTest.validate(builder.password("aaaaa").build());
    }

    @Test(expected = IllegalArgumentException.class)
    public void tooLongPassword() {
        underTest.validate(builder.password(TOO_LONG_PASSWORD).build());
    }

    @Test
    public void valid() {
        underTest.validate(builder.build());
    }
}