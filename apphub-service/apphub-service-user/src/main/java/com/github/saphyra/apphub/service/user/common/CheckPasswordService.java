package com.github.saphyra.apphub.service.user.common;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.encryption.impl.PasswordService;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.user.data.dao.user.UserDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class CheckPasswordService {
    private final PasswordService passwordService;
    private final UserDao userDao;

    public void checkPassword(UUID userId, String password) {
        String hash = userDao.findByIdValidated(userId)
            .getPassword();

        if (!passwordService.authenticate(password, hash)) {
            throw ExceptionFactory.notLoggedException(HttpStatus.BAD_REQUEST, ErrorCode.INCORRECT_PASSWORD, "Incorrect password");
        }
    }
}
