package com.github.saphyra.apphub.service.user.data.service.account;

import com.github.saphyra.apphub.api.user.model.account.ChangeEmailRequest;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.user.common.CheckPasswordService;
import com.github.saphyra.apphub.service.user.data.dao.user.User;
import com.github.saphyra.apphub.service.user.data.dao.user.UserDao;
import com.github.saphyra.apphub.service.user.data.service.validator.EmailValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChangeEmailService {
    private final EmailValidator emailValidator;
    private final CheckPasswordService checkPasswordService;
    private final UserDao userDao;

    public void changeEmail(UUID userId, ChangeEmailRequest request) {
        emailValidator.validateEmail(request.getEmail());

        if (isNull(request.getPassword())) {
            throw ExceptionFactory.invalidParam("password", "must not be null");
        }

        User user = checkPasswordService.checkPassword(userId, request.getPassword());

        user.setEmail(request.getEmail().toLowerCase());
        userDao.save(user);
    }
}
