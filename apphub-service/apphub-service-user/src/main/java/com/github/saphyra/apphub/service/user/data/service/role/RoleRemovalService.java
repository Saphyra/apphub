package com.github.saphyra.apphub.service.user.data.service.role;

import com.github.saphyra.apphub.api.user.model.role.RoleRequest;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.user.common.CheckPasswordService;
import com.github.saphyra.apphub.service.user.data.dao.role.Role;
import com.github.saphyra.apphub.service.user.data.dao.role.RoleDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class RoleRemovalService {
    private final RoleDao roleDao;
    private final RoleRequestValidator roleRequestValidator;
    private final CheckPasswordService checkPasswordService;

    public void removeRole(UUID userId, RoleRequest roleRequest) {
        roleRequestValidator.validate(roleRequest);
        checkPasswordService.checkPassword(userId, roleRequest.getPassword());

        Optional<Role> role = roleDao.findByUserIdAndRole(roleRequest.getUserId(), roleRequest.getRole());
        if (role.isPresent()) {
            roleDao.delete(role.get());
        } else {
            throw ExceptionFactory.notLoggedException(HttpStatus.NOT_FOUND, ErrorCode.ROLE_NOT_FOUND, String.format("Role %s is not present for user %s", roleRequest.getRole(), roleRequest.getUserId()));
        }
    }
}
