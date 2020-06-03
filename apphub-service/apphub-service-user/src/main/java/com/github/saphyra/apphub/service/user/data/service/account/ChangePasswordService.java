package com.github.saphyra.apphub.service.user.data.service.account;

import com.github.saphyra.apphub.api.user.model.request.ChangePasswordRequest;
import com.github.saphyra.apphub.lib.common_util.ErrorCode;
import com.github.saphyra.apphub.lib.error_handler.domain.ErrorMessage;
import com.github.saphyra.apphub.lib.error_handler.exception.BadRequestException;
import com.github.saphyra.apphub.service.user.data.dao.user.User;
import com.github.saphyra.apphub.service.user.data.dao.user.UserDao;
import com.github.saphyra.apphub.service.user.data.service.validator.PasswordValidator;
import com.github.saphyra.encryption.impl.PasswordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChangePasswordService {
    private final PasswordService passwordService;
    private final PasswordValidator passwordValidator;
    private final UserDao userDao;

    public void changePassword(UUID userId, ChangePasswordRequest request) {
        passwordValidator.validatePassword(request.getNewPassword(), "newPassword");

        if (isNull(request.getPassword())) {
            throw new BadRequestException(new ErrorMessage(ErrorCode.INVALID_PARAM.name(), "password", "must not be null"), "Password must not be null.");
        }

        User user = userDao.findById(userId);
        if (!passwordService.authenticate(request.getPassword(), user.getPassword())) {
            throw new BadRequestException(ErrorCode.BAD_PASSWORD.name(), "Bad password.");
        }

        user.setPassword(passwordService.hashPassword(request.getNewPassword()));
        userDao.save(user);
    }
}
