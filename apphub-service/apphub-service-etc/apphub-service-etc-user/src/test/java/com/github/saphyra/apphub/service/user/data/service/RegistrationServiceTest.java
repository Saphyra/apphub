package com.github.saphyra.apphub.service.user.data.service;

import com.github.saphyra.apphub.api.etc.user.model.account.RegistrationRequest;
import com.github.saphyra.apphub.service.user.data.dao.user.User;
import com.github.saphyra.apphub.service.user.data.dao.user.UserDao;
import com.github.saphyra.apphub.service.user.data.dao.user.UserFactory;
import com.github.saphyra.apphub.service.user.data.service.validator.RegistrationRequestValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class RegistrationServiceTest {
    private static final String EMAIL = "email";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String LOCALE = "locale";
    private static final String ROLE = "role";
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private RegistrationRequestValidator registrationRequestValidator;

    @Mock
    private UserDao userDao;

    @Mock
    private UserFactory userFactory;

    @InjectMocks
    private RegistrationService underTest;

    @Mock
    private User user;

    @Test
    public void register() {
        RegistrationRequest registrationRequest = RegistrationRequest.builder()
            .email(EMAIL)
            .username(USERNAME)
            .password(PASSWORD)
            .language(LOCALE)
            .build();
        given(userFactory.create(EMAIL, USERNAME, PASSWORD, LOCALE)).willReturn(user);

        underTest.register(registrationRequest);

        verify(registrationRequestValidator).validate(registrationRequest);
        verify(userDao).saveNew(user);
    }
}