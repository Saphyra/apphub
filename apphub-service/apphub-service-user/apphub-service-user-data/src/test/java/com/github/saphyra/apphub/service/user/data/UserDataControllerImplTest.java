package com.github.saphyra.apphub.service.user.data;

import com.github.saphyra.apphub.api.user.data.model.request.RegistrationRequest;
import com.github.saphyra.apphub.api.user.data.model.response.InternalUserResponse;
import com.github.saphyra.apphub.service.user.data.dao.User;
import com.github.saphyra.apphub.service.user.data.dao.UserDao;
import com.github.saphyra.apphub.service.user.data.service.register.RegistrationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class UserDataControllerImplTest {
    private static final String EMAIL = "email";
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String USERNAME = "username";
    private static final String PASSWORD_HASH = "password-hash";

    @Mock
    private UserDao userDao;

    @Mock
    private RegistrationService registrationService;

    @InjectMocks
    private UserDataControllerImpl underTest;

    @Mock
    private RegistrationRequest registrationRequest;

    @Test(expected = IllegalStateException.class)
    public void findByEmail_notFound() {
        given(userDao.findByEmail(EMAIL)).willReturn(Optional.empty());

        underTest.findByEmail(EMAIL);
    }

    @Test
    public void findByEmail() {
        User user = User.builder()
            .userId(USER_ID)
            .username(USERNAME)
            .email(EMAIL)
            .password(PASSWORD_HASH)
            .build();
        given(userDao.findByEmail(EMAIL)).willReturn(Optional.of(user));

        InternalUserResponse result = underTest.findByEmail(EMAIL);

        assertThat(result.getUserId()).isEqualTo(USER_ID);
        assertThat(result.getUsername()).isEqualTo(USERNAME);
        assertThat(result.getEmail()).isEqualTo(EMAIL);
        assertThat(result.getPasswordHash()).isEqualTo(PASSWORD_HASH);
    }

    @Test
    public void register() {
        underTest.register(registrationRequest);

        verify(registrationService).register(registrationRequest);
    }
}