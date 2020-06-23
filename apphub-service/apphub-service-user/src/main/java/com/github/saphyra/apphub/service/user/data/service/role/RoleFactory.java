package com.github.saphyra.apphub.service.user.data.service.role;

import com.github.saphyra.apphub.service.user.data.dao.role.Role;
import com.github.saphyra.util.IdGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
//todo unit test
 class RoleFactory {
    private final IdGenerator idGenerator;

    Role create(UUID userId, String role){
        return Role.builder()
            .roleId(idGenerator.randomUUID())
            .userId(userId)
            .role(role)
            .build();
    }
}
