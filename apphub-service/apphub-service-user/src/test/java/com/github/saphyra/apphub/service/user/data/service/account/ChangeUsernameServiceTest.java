package com.github.saphyra.apphub.service.user.data.service.account;

import com.github.saphyra.apphub.api.user.model.account.ChangeUsernameRequest;
import com.github.saphyra.apphub.service.user.common.CheckPasswordService;
import com.github.saphyra.apphub.service.user.data.dao.user.User;
import com.github.saphyra.apphub.service.user.data.dao.user.UserDao;
import com.github.saphyra.apphub.service.user.data.service.validator.UsernameValidator;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ChangeUsernameServiceTest {
    private static final String USERNAME = "username";
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String PASSWORD = "password";
    private static final String PASSWORD_HASH = "password-hash";

    @Mock
    private CheckPasswordService checkPasswordService;

    @Mock
    private UserDao userDao;

    @Mock
    private UsernameValidator usernameValidator;

    @InjectMocks
    private ChangeUsernameService underTest;

    @Mock
    private User user;

    @AfterEach
    public void check() {
        verify(usernameValidator).validateUsername(USERNAME);
    }

    @Test
    public void nullPassword() {
        Throwable ex = catchThrowable(() -> underTest.changeUsername(USER_ID, ChangeUsernameRequest.builder().username(USERNAME).build()));

        ExceptionValidator.validateInvalidParam(ex, "password", "must not be null");
    }

    @Test
    public void changeUsername() {
        given(checkPasswordService.checkPassword(USER_ID, PASSWORD)).willReturn(user);

        underTest.changeUsername(USER_ID, ChangeUsernameRequest.builder().password(PASSWORD).username(USERNAME).build());

        verify(user).setUsername(USERNAME);
        verify(userDao).save(user);
    }
}