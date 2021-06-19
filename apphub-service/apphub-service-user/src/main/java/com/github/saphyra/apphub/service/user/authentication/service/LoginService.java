package com.github.saphyra.apphub.service.user.authentication.service;

import com.github.saphyra.apphub.api.user.model.request.LoginRequest;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.encryption.impl.PasswordService;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.user.authentication.dao.AccessToken;
import com.github.saphyra.apphub.service.user.authentication.dao.AccessTokenDao;
import com.github.saphyra.apphub.service.user.data.dao.user.User;
import com.github.saphyra.apphub.service.user.data.dao.user.UserDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import static org.apache.commons.lang3.BooleanUtils.isTrue;


@Component
@Slf4j
@RequiredArgsConstructor
public class LoginService {
    private final AccessTokenDao accessTokenDao;
    private final AccessTokenFactory accessTokenFactory;
    private final UserDao userDao;
    private final PasswordService passwordService;

    public AccessToken login(LoginRequest loginRequest) {
        User user = userDao.findByEmail(loginRequest.getEmail().toLowerCase())
            .filter(u -> !u.isMarkedForDeletion())
            .orElseThrow(() -> ExceptionFactory.notLoggedException(HttpStatus.UNAUTHORIZED, ErrorCode.BAD_CREDENTIALS, String.format("User not found with email %s", loginRequest.getEmail())));

        if (!passwordService.authenticate(loginRequest.getPassword(), user.getPassword())) {
            throw ExceptionFactory.notLoggedException(HttpStatus.UNAUTHORIZED, ErrorCode.BAD_CREDENTIALS, "Incorrect password");
        }

        AccessToken accessToken = accessTokenFactory.create(user.getUserId(), isTrue(loginRequest.getRememberMe()));
        accessTokenDao.save(accessToken);
        return accessToken;
    }
}
