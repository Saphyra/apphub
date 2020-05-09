package com.github.saphyra.apphub.service.user.authentication.service;

import com.github.saphyra.apphub.api.user.model.request.LoginRequest;
import com.github.saphyra.apphub.lib.common_util.ErrorCode;
import com.github.saphyra.apphub.lib.error_handler.domain.ErrorMessage;
import com.github.saphyra.apphub.lib.error_handler.exception.UnauthorizedException;
import com.github.saphyra.apphub.service.user.authentication.dao.AccessToken;
import com.github.saphyra.apphub.service.user.authentication.dao.AccessTokenDao;
import com.github.saphyra.apphub.service.user.data.dao.User;
import com.github.saphyra.apphub.service.user.data.dao.UserDao;
import com.github.saphyra.encryption.impl.PasswordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static org.apache.commons.lang.BooleanUtils.isTrue;

@Component
@Slf4j
@RequiredArgsConstructor
public class LoginService {
    private final AccessTokenDao accessTokenDao;
    private final AccessTokenFactory accessTokenFactory;
    private final UserDao userDao;
    private final PasswordService passwordService;

    public AccessToken login(LoginRequest loginRequest) {
        User user = userDao.findByEmail(loginRequest.getEmail())
            .orElseThrow(() -> unauthorizedException(String.format("User not found with email %s", loginRequest.getEmail())));

        if (!passwordService.authenticate(loginRequest.getPassword(), user.getPassword())) {
            throw unauthorizedException("Invalid password");
        }

        AccessToken accessToken = accessTokenFactory.create(user.getUserId(), isTrue(loginRequest.getRememberMe()));
        accessTokenDao.save(accessToken);
        return accessToken;
    }

    private UnauthorizedException unauthorizedException(String logMessage) {
        return new UnauthorizedException(new ErrorMessage(ErrorCode.BAD_CREDENTIALS.name()), logMessage);
    }
}
