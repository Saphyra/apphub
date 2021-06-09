package com.github.saphyra.apphub.service.user.data.service.account;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.encryption.impl.PasswordService;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.user.authentication.dao.AccessTokenDao;
import com.github.saphyra.apphub.service.user.data.dao.user.User;
import com.github.saphyra.apphub.service.user.data.dao.user.UserDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.UUID;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeleteAccountService {
    private final PasswordService passwordService;
    private final UserDao userDao;
    private final AccessTokenDao accessTokenDao;
    private final DateTimeUtil dateTimeUtil;

    @Transactional
    public void deleteAccount(UUID userId, String password) {
        if (isNull(password)) {
            throw ExceptionFactory.invalidParam("password", "must not be null");
        }

        User user = userDao.findByIdValidated(userId);
        if (!passwordService.authenticate(password, user.getPassword())) {
            throw ExceptionFactory.notLoggedException(HttpStatus.BAD_REQUEST, ErrorCode.INCORRECT_PASSWORD, "Incorrect password");
        }

        deleteAccount(user);
    }

    @Transactional
    public void deleteAccount(User user) {
        user.setMarkedForDeletion(true);
        user.setMarkedForDeletionAt(dateTimeUtil.getCurrentDate());
        userDao.save(user);
        accessTokenDao.deleteByUserId(user.getUserId());
    }
}
