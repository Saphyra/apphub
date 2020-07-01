package com.github.saphyra.apphub.service.user.data.service.role;

import com.github.saphyra.apphub.api.user.model.request.RoleRequest;
import com.github.saphyra.apphub.lib.common_domain.ErrorMessage;
import com.github.saphyra.apphub.lib.common_util.ErrorCode;
import com.github.saphyra.apphub.lib.exception.ConflictException;
import com.github.saphyra.apphub.service.user.data.dao.role.Role;
import com.github.saphyra.apphub.service.user.data.dao.role.RoleDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoleAdditionService {
    private final RoleDao roleDao;
    private final RoleFactory roleFactory;
    private final RoleRequestValidator roleRequestValidator;

    public void addRole(RoleRequest roleRequest) {
        roleRequestValidator.validate(roleRequest);

        addRole(roleRequest.getUserId(), roleRequest.getRole());
    }

    public void addRole(UUID userId, String roleString) {
        if (roleDao.findByUserIdAndRole(userId, roleString).isPresent()) {
            throw new ConflictException(new ErrorMessage(ErrorCode.ROLE_ALREADY_EXISTS.name()), String.format("Role %s already exists for user %s", roleString, userId));
        }

        Role role = roleFactory.create(userId, roleString);
        roleDao.save(role);
    }
}
