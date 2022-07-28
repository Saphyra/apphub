package com.github.saphyra.apphub.service.user.data.service.account;

import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.user.authentication.dao.AccessTokenDao;
import com.github.saphyra.apphub.service.user.common.CheckPasswordService;
import com.github.saphyra.apphub.service.user.data.dao.user.User;
import com.github.saphyra.apphub.service.user.data.dao.user.UserDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeleteAccountService {
    private final CheckPasswordService checkPasswordService;
    private final UserDao userDao;
    private final AccessTokenDao accessTokenDao;
    private final DateTimeUtil dateTimeUtil;

    public void deleteAccount(UUID userId, String password) {
        if (isNull(password)) {
            throw ExceptionFactory.invalidParam("password", "must not be null");
        }

        User user = checkPasswordService.checkPassword(userId, password);

        deleteAccount(user);
    }

    public void deleteAccount(User user) {
        user.setMarkedForDeletion(true);
        user.setMarkedForDeletionAt(dateTimeUtil.getCurrentTime());
        userDao.save(user);
        accessTokenDao.deleteByUserId(user.getUserId());
    }
}
