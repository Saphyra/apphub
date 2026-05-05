package com.github.saphyra.apphub.service.user.common;

import com.github.saphyra.apphub.api.platform.authorization.client.AuthorizationClient;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.encryption.impl.PasswordService;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.lib.exception.NotLoggedException;
import com.github.saphyra.apphub.service.user.data.dao.user.User;
import com.github.saphyra.apphub.service.user.data.dao.user.UserDao;
import jakarta.transaction.Transactional;
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
    private final PasswordProperties passwordProperties;
    private final DateTimeUtil dateTimeUtil;
    private final AuthorizationClient authorizationClient;

    @Transactional(value = Transactional.TxType.REQUIRES_NEW, dontRollbackOn = NotLoggedException.class)
    public User checkPassword(UUID userId, String password) {
        User user = userDao.findByIdValidated(userId);
        String hash = user.getPassword();

        try {
            if (!passwordService.authenticate(password, userId, hash)) {
                if (passwordService.authenticateOld(password, user.getPassword())) {
                    log.info("User {} has old password. Updating...", userId);
                    user.setPassword(passwordService.hashPassword(password, userId));
                } else {
                    user.setPasswordFailureCount(user.getPasswordFailureCount() + 1);

                    if (user.getPasswordFailureCount() % passwordProperties.getLockAccountFailures() == 0) {
                        log.info("User {} has locked password.", userId);
                        user.setLockedUntil(dateTimeUtil.getCurrentDateTime().plusMinutes(passwordProperties.getLockedMinutes()));

                        authorizationClient.invalidateAllRefreshTokens(userId);

                        throw ExceptionFactory.notLoggedException(HttpStatus.LOCKED, ErrorCode.ACCOUNT_LOCKED, "Incorrect password. Account locked.");
                    }

                    log.info("User {} has {} incorrect password attempts.", user.getUserId(), user.getPasswordFailureCount());

                    throw ExceptionFactory.notLoggedException(HttpStatus.BAD_REQUEST, ErrorCode.INCORRECT_PASSWORD, "Incorrect password");
                }
            }

            user.setPasswordFailureCount(0);

            return user;
        } finally {
            userDao.save(user);
        }
    }
}
