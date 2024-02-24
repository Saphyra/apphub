package com.github.saphyra.apphub.service.user.data.service.account;

import com.github.saphyra.apphub.api.user.model.request.ChangePasswordRequest;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.lib.encryption.impl.PasswordService;
import com.github.saphyra.apphub.lib.event.EmptyEvent;
import com.github.saphyra.apphub.service.user.authentication.dao.AccessToken;
import com.github.saphyra.apphub.service.user.authentication.dao.AccessTokenDao;
import com.github.saphyra.apphub.service.user.common.CheckPasswordService;
import com.github.saphyra.apphub.service.user.common.EventGatewayProxy;
import com.github.saphyra.apphub.service.user.data.dao.user.User;
import com.github.saphyra.apphub.service.user.data.dao.user.UserDao;
import com.github.saphyra.apphub.service.user.data.service.validator.PasswordValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChangePasswordService {
    private final CheckPasswordService checkPasswordService;
    private final PasswordService passwordService;
    private final PasswordValidator passwordValidator;
    private final UserDao userDao;
    private final AccessTokenDao accessTokenDao;
    private final EventGatewayProxy eventGatewayProxy;

    public void changePassword(UUID userId, ChangePasswordRequest request) {
        passwordValidator.validatePassword(request.getNewPassword(), "newPassword");
        ValidationUtil.notNull(request.getDeactivateAllSessions(), "deactivateAllSessions");
        ValidationUtil.notBlank(request.getPassword(), "password");

        User user = checkPasswordService.checkPassword(userId, request.getPassword());

        user.setPassword(passwordService.hashPassword(request.getNewPassword(), userId));
        userDao.save(user);

        if (request.getDeactivateAllSessions()) {
            List<AccessToken> accessTokens = accessTokenDao.getByUserId(userId);

            List<UUID> accessTokenIds = accessTokens.stream()
                .map(AccessToken::getAccessTokenId)
                .toList();

            accessTokenDao.deleteAll(accessTokens);
            eventGatewayProxy.sendEvent(EmptyEvent.ACCESS_TOKENS_INVALIDATED, accessTokenIds, true);
        }
    }
}
