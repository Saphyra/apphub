package com.github.saphyra.apphub.service.user.data.service.account;

import com.github.saphyra.apphub.api.etc.user.model.account.ChangePasswordRequest;
import com.github.saphyra.apphub.api.platform.authorization.client.AuthorizationClient;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.lib.encryption.impl.PasswordService;
import com.github.saphyra.apphub.service.user.common.CheckPasswordService;
import com.github.saphyra.apphub.service.user.data.dao.user.User;
import com.github.saphyra.apphub.service.user.data.dao.user.UserDao;
import com.github.saphyra.apphub.service.user.data.service.validator.PasswordValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChangePasswordService {
    private final CheckPasswordService checkPasswordService;
    private final PasswordService passwordService;
    private final PasswordValidator passwordValidator;
    private final UserDao userDao;
    private final AuthorizationClient authorizationClient;

    public void changePassword(UUID userId, ChangePasswordRequest request) {
        passwordValidator.validatePassword(request.getNewPassword(), "newPassword");
        ValidationUtil.notNull(request.getDeactivateAllSessions(), "deactivateAllSessions");
        ValidationUtil.notBlank(request.getPassword(), "password");

        User user = checkPasswordService.checkPassword(userId, request.getPassword());

        user.setPassword(passwordService.hashPassword(request.getNewPassword(), userId));
        userDao.save(user);

        if (request.getDeactivateAllSessions()) {
            authorizationClient.deactivateAllSessions(userId);
        }
    }
}
