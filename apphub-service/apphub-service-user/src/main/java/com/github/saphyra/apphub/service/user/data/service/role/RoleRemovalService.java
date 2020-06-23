package com.github.saphyra.apphub.service.user.data.service.role;

import com.github.saphyra.apphub.api.user.model.request.RoleRequest;
import com.github.saphyra.apphub.lib.common_domain.ErrorMessage;
import com.github.saphyra.apphub.lib.common_util.ErrorCode;
import com.github.saphyra.apphub.lib.exception.NotFoundException;
import com.github.saphyra.apphub.service.user.data.dao.role.Role;
import com.github.saphyra.apphub.service.user.data.dao.role.RoleDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
//TODO unit test
public class RoleRemovalService {
    private final RoleDao roleDao;

    public void removeRole(RoleRequest roleRequest) {
        Optional<Role> role = roleDao.findByUserIdAndRole(roleRequest.getUserId(), roleRequest.getRole());
        if (role.isPresent()) {
            roleDao.delete(role.get());
        } else {
            throw new NotFoundException(new ErrorMessage(ErrorCode.ROLE_NOT_FOUND.name()), String.format("Role %s is not present for user %s", roleRequest.getRole(), roleRequest.getUserId()));
        }
    }
}
