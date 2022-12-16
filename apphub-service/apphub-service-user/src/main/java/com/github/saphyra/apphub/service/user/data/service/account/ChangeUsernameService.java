package com.github.saphyra.apphub.service.user.data.service.account;

import com.github.saphyra.apphub.api.user.model.request.ChangeUsernameRequest;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.user.common.CheckPasswordService;
import com.github.saphyra.apphub.service.user.data.dao.user.User;
import com.github.saphyra.apphub.service.user.data.dao.user.UserDao;
import com.github.saphyra.apphub.service.user.data.service.validator.UsernameValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChangeUsernameService {
    private final CheckPasswordService checkPasswordService;
    private final UserDao userDao;
    private final UsernameValidator usernameValidator;

    public void changeUsername(UUID userId, ChangeUsernameRequest request) {
        usernameValidator.validateUsername(request.getUsername());

        if (isNull(request.getPassword())) {
            throw ExceptionFactory.invalidParam("password", "must not be null");
        }

        User user = checkPasswordService.checkPassword(userId, request.getPassword());

        user.setUsername(request.getUsername());
        userDao.save(user);
    }
}
