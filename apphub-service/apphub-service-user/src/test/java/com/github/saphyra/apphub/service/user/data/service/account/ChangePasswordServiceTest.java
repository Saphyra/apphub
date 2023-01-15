package com.github.saphyra.apphub.service.user.data.service.account;

import com.github.saphyra.apphub.api.user.model.request.ChangePasswordRequest;
import com.github.saphyra.apphub.lib.encryption.impl.PasswordService;
import com.github.saphyra.apphub.service.user.common.CheckPasswordService;
import com.github.saphyra.apphub.service.user.data.dao.user.User;
import com.github.saphyra.apphub.service.user.data.dao.user.UserDao;
import com.github.saphyra.apphub.service.user.data.service.validator.PasswordValidator;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.After;
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
public class ChangePasswordServiceTest {
    private static final String NEW_PASSWORD = "new-password";
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String PASSWORD_HASH = "password-hash";
    private static final String PASSWORD = "password";
    private static final String NEW_PASSWORD_HASH = "new-password-hash";

    @Mock
    private PasswordService passwordService;

    @Mock
    private PasswordValidator passwordValidator;

    @Mock
    private UserDao userDao;

    @Mock
    private CheckPasswordService checkPasswordService;

    @InjectMocks
    private ChangePasswordService underTest;

    @Mock
    private User user;

    @After
    public void check() {
        verify(passwordValidator).validatePassword(NEW_PASSWORD, "newPassword");
    }

    @Test
    public void nullPassword() {
        Throwable ex = catchThrowable(() -> underTest.changePassword(USER_ID, ChangePasswordRequest.builder().newPassword(NEW_PASSWORD).build()));


        ExceptionValidator.validateInvalidParam(ex, "password", "must not be null");
    }

    @Test
    public void changePassword() {
        given(checkPasswordService.checkPassword(USER_ID, PASSWORD)).willReturn(user);
        given(passwordService.hashPassword(NEW_PASSWORD, USER_ID)).willReturn(NEW_PASSWORD_HASH);

        underTest.changePassword(USER_ID, ChangePasswordRequest.builder().newPassword(NEW_PASSWORD).password(PASSWORD).build());

        verify(user).setPassword(NEW_PASSWORD_HASH);
        verify(userDao).save(user);
    }
}