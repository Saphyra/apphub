package com.github.saphyra.apphub.service.user.data.service.validator;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.user.data.dao.user.UserDao;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
public class UsernameValidator {
    private final UserDao userDao;

    public void validateUsername(String username) {
        if (isNull(username)) {
            throw ExceptionFactory.invalidParam("username", "must not be null");
        }

        if (username.length() < 3) {
            throw ExceptionFactory.invalidParam("username", "too short");
        }

        if (username.length() > 30) {
            throw ExceptionFactory.invalidParam("username", "too long");
        }

        if (userDao.findByUsernameOrEmail(username).isPresent()) {
            throw ExceptionFactory.notLoggedException(HttpStatus.CONFLICT, ErrorCode.USERNAME_ALREADY_EXISTS, "Username already exists.");
        }
    }
}
