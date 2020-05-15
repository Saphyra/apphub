package com.github.saphyra.apphub.service.user.data.service.register;

import com.github.saphyra.apphub.api.user.model.request.RegistrationRequest;
import com.github.saphyra.apphub.service.user.data.dao.user.User;
import com.github.saphyra.apphub.service.user.data.dao.user.UserDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class RegistrationServiceTest {
    private static final String EMAIL = "email";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String LOCALE = "locale";

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
            .build();
        given(userFactory.create(EMAIL, USERNAME, PASSWORD, LOCALE)).willReturn(user);

        underTest.register(registrationRequest, LOCALE);

        verify(registrationRequestValidator).validate(registrationRequest);
        verify(userDao).save(user);
    }
}