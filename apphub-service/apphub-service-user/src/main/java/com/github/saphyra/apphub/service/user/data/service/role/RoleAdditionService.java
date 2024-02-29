package com.github.saphyra.apphub.service.user.data.service.role;

import com.github.saphyra.apphub.api.user.model.request.RoleRequest;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.user.common.CheckPasswordService;
import com.github.saphyra.apphub.service.user.data.dao.role.Role;
import com.github.saphyra.apphub.service.user.data.dao.role.RoleDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoleAdditionService {
    private final RoleDao roleDao;
    private final RoleFactory roleFactory;
    private final RoleRequestValidator roleRequestValidator;
    private final CheckPasswordService checkPasswordService;

    public void addRole(UUID userId, RoleRequest roleRequest) {
        roleRequestValidator.validate(roleRequest);

        checkPasswordService.checkPassword(userId, roleRequest.getPassword());

        addRole(roleRequest.getUserId(), roleRequest.getRole());
    }

    public void addRole(UUID userId, String roleString) {
        if (roleDao.findByUserIdAndRole(userId, roleString).isPresent()) {
            throw ExceptionFactory.notLoggedException(HttpStatus.CONFLICT, ErrorCode.ROLE_ALREADY_EXISTS, String.format("Role %s already exists for user %s", roleString, userId));
        }

        Role role = roleFactory.create(userId, roleString);
        roleDao.save(role);
    }
}
