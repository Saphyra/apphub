package com.github.saphyra.apphub.service.user.data.service.account;

import com.github.saphyra.apphub.api.user.model.request.ChangePasswordRequest;
import com.github.saphyra.apphub.lib.encryption.impl.PasswordService;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.user.common.CheckPasswordService;
import com.github.saphyra.apphub.service.user.data.dao.user.User;
import com.github.saphyra.apphub.service.user.data.dao.user.UserDao;
import com.github.saphyra.apphub.service.user.data.service.validator.PasswordValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChangePasswordService {
    private final CheckPasswordService checkPasswordService;
    private final PasswordService passwordService;
    private final PasswordValidator passwordValidator;
    private final UserDao userDao;

    public void changePassword(UUID userId, ChangePasswordRequest request) {
        passwordValidator.validatePassword(request.getNewPassword(), "newPassword");

        if (isNull(request.getPassword())) {
            throw ExceptionFactory.invalidParam("password", "must not be null");
        }

        User user = checkPasswordService.checkPassword(userId, request.getPassword());

        user.setPassword(passwordService.hashPassword(request.getNewPassword(), userId));
        userDao.save(user);
    }
}
