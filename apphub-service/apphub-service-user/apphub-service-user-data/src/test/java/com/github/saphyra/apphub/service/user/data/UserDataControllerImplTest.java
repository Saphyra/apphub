package com.github.saphyra.apphub.service.user.data;

import com.github.saphyra.apphub.api.user.data.model.request.RegistrationRequest;
import com.github.saphyra.apphub.api.user.data.model.response.InternalUserResponse;
import com.github.saphyra.apphub.lib.common_util.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.UuidConverter;
import com.github.saphyra.apphub.lib.error_handler.exception.NotFoundException;
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
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class UserDataControllerImplTest {
    private static final String EMAIL = "email";
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String USERNAME = "username";
    private static final String PASSWORD_HASH = "password-hash";
    private static final String LOCALE = "locale";
    private static final String LANGUAGE = "language";
    private static final String USER_ID_STRING = "user-id-string";

    @Mock
    private UserDao userDao;

    @Mock
    private RegistrationService registrationService;

    @Mock
    private UuidConverter uuidConverter;

    @InjectMocks
    private UserDataControllerImpl underTest;

    @Mock
    private RegistrationRequest registrationRequest;

    @Mock
    private User user;

    @Test
    public void findByEmail_notFound() {
        given(userDao.findByEmail(EMAIL)).willReturn(Optional.empty());

        Throwable ex = catchThrowable(() -> underTest.findByEmail(EMAIL));

        assertThat(ex).isInstanceOf(NotFoundException.class);
        NotFoundException exception = (NotFoundException) ex;
        assertThat(exception.getErrorMessage().getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND.name());
    }

    @Test
    public void findByEmail() {
        User user = User.builder()
            .userId(USER_ID)
            .username(USERNAME)
            .email(EMAIL)
            .password(PASSWORD_HASH)
            .language(LANGUAGE)
            .build();
        given(userDao.findByEmail(EMAIL)).willReturn(Optional.of(user));

        InternalUserResponse result = underTest.findByEmail(EMAIL);

        assertThat(result.getUserId()).isEqualTo(USER_ID);
        assertThat(result.getUsername()).isEqualTo(USERNAME);
        assertThat(result.getEmail()).isEqualTo(EMAIL);
        assertThat(result.getPasswordHash()).isEqualTo(PASSWORD_HASH);
    }

    @Test
    public void getLanguage() {
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(userDao.findById(USER_ID_STRING)).willReturn(Optional.of(user));
        given(user.getLanguage()).willReturn(LOCALE);

        String result = underTest.getLanguage(USER_ID);

        assertThat(result).isEqualTo(LOCALE);
    }

    @Test
    public void getLanguage_userNotFound() {
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(userDao.findById(USER_ID_STRING)).willReturn(Optional.empty());

        Throwable ex = catchThrowable(() -> underTest.getLanguage(USER_ID));

        assertThat(ex).isInstanceOf(NotFoundException.class);
        NotFoundException exception = (NotFoundException) ex;
        assertThat(exception.getErrorMessage().getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND.name());
    }

    @Test
    public void register() {
        underTest.register(registrationRequest, LOCALE);

        verify(registrationService).register(registrationRequest, LOCALE);
    }
}