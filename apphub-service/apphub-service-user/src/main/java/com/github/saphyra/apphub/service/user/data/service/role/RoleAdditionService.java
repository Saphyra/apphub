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

@Service
@Slf4j
@RequiredArgsConstructor
public class RoleAdditionService {
    private final RoleDao roleDao;
    private final RoleFactory roleFactory;

    public void addRole(RoleRequest roleRequest) {
        if(roleDao.findByUserIdAndRole(roleRequest.getUserId(), roleRequest.getRole()).isPresent()){
            throw new ConflictException(new ErrorMessage(ErrorCode.ROLE_ALREADY_EXISTS.name()), String.format("Role %s already exists for user %s", roleRequest.getRole(), roleRequest.getUserId()));
        }

        Role role = roleFactory.create(roleRequest.getUserId(), roleRequest.getRole());
        roleDao.save(role);
    }
}
