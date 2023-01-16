package com.github.saphyra.apphub.service.user.data.service.account;

import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.service.user.authentication.dao.AccessTokenDao;
import com.github.saphyra.apphub.service.user.common.CheckPasswordService;
import com.github.saphyra.apphub.service.user.data.dao.user.User;
import com.github.saphyra.apphub.service.user.data.dao.user.UserDao;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class DeleteAccountServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String PASSWORD = "password";
    private static final LocalDateTime CURRENT_DATE = LocalDateTime.now();

    @Mock
    private CheckPasswordService checkPasswordService;

    @Mock
    private UserDao userDao;

    @Mock
    private AccessTokenDao accessTokenDao;

    @Mock
    private DateTimeUtil dateTimeUtil;

    @InjectMocks
    private DeleteAccountService underTest;

    @Mock
    private User user;

    @Test
    public void nullPassword() {
        Throwable ex = catchThrowable(() -> underTest.deleteAccount(USER_ID, null));

        ExceptionValidator.validateInvalidParam(ex, "password", "must not be null");
    }

    @Test
    public void deleteAccount() {
        given(user.getUserId()).willReturn(USER_ID);
        given(checkPasswordService.checkPassword(USER_ID, PASSWORD)).willReturn(user);
        given(dateTimeUtil.getCurrentDateTime()).willReturn(CURRENT_DATE);

        underTest.deleteAccount(USER_ID, PASSWORD);

        verify(user).setMarkedForDeletion(true);
        verify(user).setMarkedForDeletionAt(CURRENT_DATE);
        verify(userDao).save(user);
        verify(accessTokenDao).deleteByUserId(USER_ID);
    }
}