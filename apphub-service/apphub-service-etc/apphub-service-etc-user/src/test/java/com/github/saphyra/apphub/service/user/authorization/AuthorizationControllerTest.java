package com.github.saphyra.apphub.service.user.authorization;

import com.github.saphyra.apphub.api.etc.user.model.authorization.AuthorizationRequest;
import com.github.saphyra.apphub.api.etc.user.model.authorization.AuthorizationResponse;
import com.github.saphyra.apphub.api.etc.user.model.authorization.AuthorizationResult;
import com.github.saphyra.apphub.lib.common_domain.Role;
import com.github.saphyra.apphub.lib.common_domain.AccessToken;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import com.github.saphyra.apphub.service.user.ban.service.BanService;
import com.github.saphyra.apphub.service.user.common.CheckPasswordService;
import com.github.saphyra.apphub.service.user.data.dao.user.User;
import com.github.saphyra.apphub.service.user.data.dao.user.UserDao;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doThrow;

@SuppressWarnings("resource")
@ExtendWith(MockitoExtension.class)
class AuthorizationControllerTest {
    private static final String USER_IDENTIFIER = "user-identifier";
    private static final LocalDateTime CURRENT_TIME = LocalDateTime.now();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String PASSWORD = "password";

    @Mock
    private UserDao userDao;

    @Mock
    private DateTimeUtil dateTimeUtil;

    @Mock
    private AccessTokenProvider accessTokenProvider;

    @Mock
    private CheckPasswordService checkPasswordService;

    @Mock
    private BanService banService;

    @InjectMocks
    private AuthorizationController underTest;

    @Mock
    private User user;

    @Test
    void authorize_userNotFound() {
        AuthorizationRequest request = AuthorizationRequest.builder()
            .userIdentifier(USER_IDENTIFIER)
            .build();
        given(userDao.findByUserIdentifier(USER_IDENTIFIER)).willReturn(Optional.empty());

        assertThat(underTest.authorize(request))
            .returns(AuthorizationResult.USER_NOT_FOUND, AuthorizationResponse::getAuthorizationResult);
    }

    @Test
    void authorize_userMarkedForDeletion() {
        AuthorizationRequest request = AuthorizationRequest.builder()
            .userIdentifier(USER_IDENTIFIER)
            .build();
        given(userDao.findByUserIdentifier(USER_IDENTIFIER)).willReturn(Optional.of(user));
        given(user.isMarkedForDeletion()).willReturn(true);

        assertThat(underTest.authorize(request))
            .returns(AuthorizationResult.USER_NOT_FOUND, AuthorizationResponse::getAuthorizationResult);
    }

    @Test
    void authorize_userLocked() {
        AuthorizationRequest request = AuthorizationRequest.builder()
            .userIdentifier(USER_IDENTIFIER)
            .build();
        given(userDao.findByUserIdentifier(USER_IDENTIFIER)).willReturn(Optional.of(user));
        given(user.isMarkedForDeletion()).willReturn(false);
        given(user.getLockedUntil()).willReturn(CURRENT_TIME.plusSeconds(1));
        given(user.getUserId()).willReturn(USER_ID);
        given(dateTimeUtil.getCurrentDateTime()).willReturn(CURRENT_TIME);

        assertThat(underTest.authorize(request))
            .returns(AuthorizationResult.USER_LOCKED, AuthorizationResponse::getAuthorizationResult)
            .returns(USER_ID, AuthorizationResponse::getUserId);
    }

    @Test
    void authorize_incorrectPassword() {
        AuthorizationRequest request = AuthorizationRequest.builder()
            .userIdentifier(USER_IDENTIFIER)
            .password(PASSWORD)
            .build();
        given(userDao.findByUserIdentifier(USER_IDENTIFIER)).willReturn(Optional.of(user));
        given(user.isMarkedForDeletion()).willReturn(false);
        given(user.getLockedUntil()).willReturn(CURRENT_TIME.minusSeconds(1));
        given(user.getUserId()).willReturn(USER_ID);
        doThrow(ExceptionFactory.notLoggedException(HttpStatus.UNAUTHORIZED, ErrorCode.INCORRECT_PASSWORD))
            .when(checkPasswordService)
            .checkPassword(USER_ID, PASSWORD);
        given(dateTimeUtil.getCurrentDateTime()).willReturn(CURRENT_TIME);

        assertThat(underTest.authorize(request))
            .returns(AuthorizationResult.INCORRECT_PASSWORD, AuthorizationResponse::getAuthorizationResult)
            .returns(USER_ID, AuthorizationResponse::getUserId);

        then(accessTokenProvider).should().set(AccessToken.builder().userId(USER_ID).build());
        then(accessTokenProvider).should().clear();
    }

    @Test
    void authorize_successfulAuthorization() {
        AuthorizationRequest request = AuthorizationRequest.builder()
            .userIdentifier(USER_IDENTIFIER)
            .password(PASSWORD)
            .build();
        given(userDao.findByUserIdentifier(USER_IDENTIFIER)).willReturn(Optional.of(user));
        given(user.isMarkedForDeletion()).willReturn(false);
        given(user.getLockedUntil()).willReturn(CURRENT_TIME.minusSeconds(1));
        given(user.getUserId()).willReturn(USER_ID);
        given(dateTimeUtil.getCurrentDateTime()).willReturn(CURRENT_TIME);
        given(userDao.findByUserId(USER_ID)).willReturn(Optional.of(user));
        given(banService.getActivelyBannedRolesOf(USER_ID)).willReturn(List.of());
        given(user.getRoles()).willReturn(List.of(Role.TEST));

        assertThat(underTest.authorize(request))
            .returns(AuthorizationResult.AUTHORIZED, AuthorizationResponse::getAuthorizationResult)
            .returns(USER_ID, AuthorizationResponse::getUserId)
            .returns(List.of(Role.TEST), AuthorizationResponse::getRoles);

        then(checkPasswordService).should().checkPassword(USER_ID, PASSWORD);
        then(accessTokenProvider).should().set(AccessToken.builder().userId(USER_ID).build());
        then(accessTokenProvider).should().clear();
    }

    @Test
    void getRoles_userNotFound() {
        given(userDao.findByUserId(USER_ID)).willReturn(Optional.empty());

        Throwable ex = catchThrowable(() -> underTest.getRoles(USER_ID));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.NOT_FOUND, ErrorCode.USER_NOT_FOUND);
    }

    @Test
    void getRoles_userMarkedForDeletion() {
        given(userDao.findByUserId(USER_ID)).willReturn(Optional.of(user));
        given(user.isMarkedForDeletion()).willReturn(true);

        Throwable ex = catchThrowable(() -> underTest.getRoles(USER_ID));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.NOT_FOUND, ErrorCode.USER_NOT_FOUND);
    }

    @Test
    void getRoles_roleBanned() {
        given(userDao.findByUserId(USER_ID)).willReturn(Optional.of(user));
        given(user.isMarkedForDeletion()).willReturn(false);
        given(banService.getActivelyBannedRolesOf(USER_ID)).willReturn(List.of(Role.TEST));
        given(user.getRoles()).willReturn(List.of(Role.TEST));

        List<Role> result = underTest.getRoles(USER_ID);

        assertThat(result).isEmpty();
    }

    @Test
    void getRoles() {
        given(userDao.findByUserId(USER_ID)).willReturn(Optional.of(user));
        given(user.isMarkedForDeletion()).willReturn(false);
        given(banService.getActivelyBannedRolesOf(USER_ID)).willReturn(List.of());
        given(user.getRoles()).willReturn(List.of(Role.TEST));

        List<Role> result = underTest.getRoles(USER_ID);

        assertThat(result).containsExactly(Role.TEST);
    }
}