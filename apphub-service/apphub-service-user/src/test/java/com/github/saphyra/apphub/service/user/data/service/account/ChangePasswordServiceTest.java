package com.github.saphyra.apphub.service.user.data.service.account;

import com.github.saphyra.apphub.api.user.model.account.ChangePasswordRequest;
import com.github.saphyra.apphub.lib.encryption.impl.PasswordService;
import com.github.saphyra.apphub.lib.event.EmptyEvent;
import com.github.saphyra.apphub.service.user.authentication.dao.AccessToken;
import com.github.saphyra.apphub.service.user.authentication.dao.AccessTokenDao;
import com.github.saphyra.apphub.service.user.common.CheckPasswordService;
import com.github.saphyra.apphub.service.user.common.EventGatewayProxy;
import com.github.saphyra.apphub.service.user.data.dao.user.User;
import com.github.saphyra.apphub.service.user.data.dao.user.UserDao;
import com.github.saphyra.apphub.service.user.data.service.validator.PasswordValidator;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
public class ChangePasswordServiceTest {
    private static final String NEW_PASSWORD = "new-password";
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String PASSWORD = "password";
    private static final String NEW_PASSWORD_HASH = "new-password-hash";
    private static final UUID ACCESS_TOKEN_ID = UUID.randomUUID();

    @Mock
    private PasswordService passwordService;

    @Mock
    private PasswordValidator passwordValidator;

    @Mock
    private UserDao userDao;

    @Mock
    private CheckPasswordService checkPasswordService;

    @Mock
    private AccessTokenDao accessTokenDao;

    @Mock
    private EventGatewayProxy eventGatewayProxy;

    @InjectMocks
    private ChangePasswordService underTest;

    @Mock
    private User user;

    @Mock
    private AccessToken accessToken;

    @AfterEach
    public void check() {
        then(passwordValidator).should().validatePassword(NEW_PASSWORD, "newPassword");
    }

    @Test
    public void nullPassword() {
        ChangePasswordRequest request = ChangePasswordRequest.builder()
            .newPassword(NEW_PASSWORD)
            .password(null)
            .deactivateAllSessions(false)
            .build();

        Throwable ex = catchThrowable(() -> underTest.changePassword(USER_ID, request));


        ExceptionValidator.validateInvalidParam(ex, "password", "must not be null or blank");
    }

    @Test
    void nullDeactivateAllSessions() {
        ChangePasswordRequest request = ChangePasswordRequest.builder()
            .newPassword(NEW_PASSWORD)
            .password(PASSWORD)
            .deactivateAllSessions(null)
            .build();

        Throwable ex = catchThrowable(() -> underTest.changePassword(USER_ID, request));


        ExceptionValidator.validateInvalidParam(ex, "deactivateAllSessions", "must not be null");
    }

    @Test
    public void changePassword() {
        given(checkPasswordService.checkPassword(USER_ID, PASSWORD)).willReturn(user);
        given(passwordService.hashPassword(NEW_PASSWORD, USER_ID)).willReturn(NEW_PASSWORD_HASH);

        ChangePasswordRequest request = ChangePasswordRequest.builder()
            .newPassword(NEW_PASSWORD)
            .password(PASSWORD)
            .deactivateAllSessions(false)
            .build();
        underTest.changePassword(USER_ID, request);

        then(user).should().setPassword(NEW_PASSWORD_HASH);
        then(userDao).should().save(user);
        then(accessTokenDao).shouldHaveNoInteractions();
    }

    @Test
    public void changePassword_deactivateAllSessions() {
        given(checkPasswordService.checkPassword(USER_ID, PASSWORD)).willReturn(user);
        given(passwordService.hashPassword(NEW_PASSWORD, USER_ID)).willReturn(NEW_PASSWORD_HASH);
        given(accessTokenDao.getByUserId(USER_ID)).willReturn(List.of(accessToken));
        given(accessToken.getAccessTokenId()).willReturn(ACCESS_TOKEN_ID);

        ChangePasswordRequest request = ChangePasswordRequest.builder()
            .newPassword(NEW_PASSWORD)
            .password(PASSWORD)
            .deactivateAllSessions(true)
            .build();
        underTest.changePassword(USER_ID, request);

        then(user).should().setPassword(NEW_PASSWORD_HASH);
        then(userDao).should().save(user);
        then(accessTokenDao).should().deleteAll(List.of(accessToken));
        then(eventGatewayProxy).should().sendEvent(EmptyEvent.ACCESS_TOKENS_INVALIDATED, List.of(ACCESS_TOKEN_ID), true);
    }
}