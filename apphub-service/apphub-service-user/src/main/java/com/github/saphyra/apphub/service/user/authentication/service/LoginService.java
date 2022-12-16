package com.github.saphyra.apphub.service.user.authentication.service;

import com.github.saphyra.apphub.api.user.model.request.LoginRequest;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.lib.exception.RestException;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import com.github.saphyra.apphub.service.user.authentication.dao.AccessToken;
import com.github.saphyra.apphub.service.user.authentication.dao.AccessTokenDao;
import com.github.saphyra.apphub.service.user.common.CheckPasswordService;
import com.github.saphyra.apphub.service.user.data.dao.user.User;
import com.github.saphyra.apphub.service.user.data.dao.user.UserDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.BooleanUtils.isTrue;


@Component
@Slf4j
@RequiredArgsConstructor
public class LoginService {
    private final AccessTokenDao accessTokenDao;
    private final AccessTokenFactory accessTokenFactory;
    private final UserDao userDao;
    private final DateTimeUtil dateTimeUtil;
    private final CheckPasswordService checkPasswordService;
    private final AccessTokenProvider accessTokenProvider;

    public AccessToken login(LoginRequest loginRequest) {
        User user = userDao.findByEmail(loginRequest.getEmail().toLowerCase())
            .filter(u -> !u.isMarkedForDeletion())
            .orElseThrow(() -> ExceptionFactory.notLoggedException(HttpStatus.UNAUTHORIZED, ErrorCode.BAD_CREDENTIALS, String.format("User not found with email %s", loginRequest.getEmail())));

        if (nonNull(user.getLockedUntil()) && user.getLockedUntil().isAfter(dateTimeUtil.getCurrentDateTime())) {
            throw ExceptionFactory.notLoggedException(HttpStatus.UNAUTHORIZED, ErrorCode.ACCOUNT_LOCKED, "User account locked.");
        }

        try {
            accessTokenProvider.set(AccessTokenHeader.builder().userId(user.getUserId()).build());
            checkPasswordService.checkPassword(user.getUserId(), loginRequest.getPassword());
        } catch (RestException e) {
            if (e.getErrorMessage().getErrorCode() == ErrorCode.INCORRECT_PASSWORD) {
                throw ExceptionFactory.notLoggedException(HttpStatus.UNAUTHORIZED, ErrorCode.BAD_CREDENTIALS, "Incorrect password");
            }
            throw e;
        } finally {
            accessTokenProvider.clear();
        }

        AccessToken accessToken = accessTokenFactory.create(user.getUserId(), isTrue(loginRequest.getRememberMe()));
        accessTokenDao.save(accessToken);
        return accessToken;
    }
}
