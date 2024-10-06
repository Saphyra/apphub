package com.github.saphyra.apphub.service.user.data.service.account;

import com.github.saphyra.apphub.api.user.model.account.ChangeEmailRequest;
import com.github.saphyra.apphub.service.user.common.CheckPasswordService;
import com.github.saphyra.apphub.service.user.data.dao.user.User;
import com.github.saphyra.apphub.service.user.data.dao.user.UserDao;
import com.github.saphyra.apphub.service.user.data.service.validator.EmailValidator;
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
public class ChangeEmailServiceTest {
    private static final String EMAIL = "email";
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String PASSWORD_HASH = "password-hash";
    private static final String PASSWORD = "password";

    @Mock
    private EmailValidator emailValidator;

    @Mock
    private CheckPasswordService checkPasswordService;

    @Mock
    private UserDao userDao;

    @InjectMocks
    private ChangeEmailService underTest;

    @Mock
    private User user;

    @AfterEach
    public void check() {
        verify(emailValidator).validateEmail(EMAIL);
    }

    @Test
    public void nullPassword() {
        Throwable ex = catchThrowable(() -> underTest.changeEmail(USER_ID, ChangeEmailRequest.builder().email(EMAIL).build()));

        ExceptionValidator.validateInvalidParam(ex, "password", "must not be null");
    }

    @Test
    public void changeEmail() {
        given(checkPasswordService.checkPassword(USER_ID, PASSWORD)).willReturn(user);

        underTest.changeEmail(USER_ID, ChangeEmailRequest.builder().email(EMAIL).password(PASSWORD).build());

        verify(user).setEmail(EMAIL);
        verify(userDao).save(user);
    }
}