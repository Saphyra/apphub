package com.github.saphyra.apphub.service.user.data.service;

import com.github.saphyra.apphub.api.etc.user.model.role.RoleRequest;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.user.common.CheckPasswordService;
import com.github.saphyra.apphub.service.user.data.dao.user.User;
import com.github.saphyra.apphub.service.user.data.dao.user.UserDao;
import com.github.saphyra.apphub.service.user.data.service.validator.RoleRequestValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class RoleRemovalService {
    private final RoleRequestValidator roleRequestValidator;
    private final CheckPasswordService checkPasswordService;
    private final UserDao userDao;

    public User removeRole(UUID userId, RoleRequest roleRequest) {
        roleRequestValidator.validate(roleRequest);
        checkPasswordService.checkPassword(userId, roleRequest.getPassword());

        User user = userDao.findByUserIdValidated(roleRequest.getUserId());

        if (!user.getRoles().contains(roleRequest.getRole())) {
            throw ExceptionFactory.notLoggedException(HttpStatus.NOT_FOUND, ErrorCode.ROLE_NOT_FOUND, String.format("Role %s is not present for user %s", roleRequest.getRole(), roleRequest.getUserId()));
        }

        userDao.removeRole(roleRequest.getUserId(), roleRequest.getRole());
        user.getRoles().remove(roleRequest.getRole());

        return user;
    }
}
