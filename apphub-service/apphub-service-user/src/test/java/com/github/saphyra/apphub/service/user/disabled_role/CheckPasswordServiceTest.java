package com.github.saphyra.apphub.service.user.disabled_role;

import com.github.saphyra.apphub.lib.common_util.ErrorCode;
import com.github.saphyra.apphub.lib.encryption.impl.PasswordService;
import com.github.saphyra.apphub.lib.exception.BadRequestException;
import com.github.saphyra.apphub.service.user.data.dao.user.User;
import com.github.saphyra.apphub.service.user.data.dao.user.UserDao;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class CheckPasswordServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String PASSWORD_HASH = "password-hash";
    private static final String PASSWORD = "password";

    @Mock
    private PasswordService passwordService;

    @Mock
    private UserDao userDao;

    @InjectMocks
    private CheckPasswordService underTest;

    @Mock
    private User user;

    @Before
    public void setUp() {
        given(userDao.findByIdValidated(USER_ID)).willReturn(user);
        given(user.getPassword()).willReturn(PASSWORD_HASH);
    }

    @Test
    public void incorrectPassword() {
        given(passwordService.authenticate(PASSWORD, PASSWORD_HASH)).willReturn(false);

        Throwable ex = catchThrowable(() -> underTest.checkPassword(USER_ID, PASSWORD));

        assertThat(ex).isInstanceOf(BadRequestException.class);
        BadRequestException exception = (BadRequestException) ex;
        assertThat(exception.getErrorMessage().getErrorCode()).isEqualTo(ErrorCode.BAD_PASSWORD.name());
    }

    @Test
    public void correctPassword() {
        given(passwordService.authenticate(PASSWORD, PASSWORD_HASH)).willReturn(true);

        underTest.checkPassword(USER_ID, PASSWORD);

        //No exception thrown
    }
}