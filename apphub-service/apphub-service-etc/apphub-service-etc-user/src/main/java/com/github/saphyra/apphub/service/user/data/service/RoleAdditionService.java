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
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoleAdditionService {
    private final UserDao userDao;
    private final RoleRequestValidator roleRequestValidator;
    private final CheckPasswordService checkPasswordService;

    public User addRole(UUID userId, RoleRequest roleRequest) {
        roleRequestValidator.validate(roleRequest);

        checkPasswordService.checkPassword(userId, roleRequest.getPassword());

        User user = userDao.findByUserIdValidated(roleRequest.getUserId());

        if (user.getRoles().contains(roleRequest.getRole())) {
            throw ExceptionFactory.notLoggedException(HttpStatus.CONFLICT, ErrorCode.ROLE_ALREADY_EXISTS, String.format("Role %s already exists for user %s", roleRequest.getRole(), user.getUserId()));
        }
        user.getRoles().add(roleRequest.getRole());
        userDao.addRole(user.getUserId(), roleRequest.getRole());

        return user;
    }
}
