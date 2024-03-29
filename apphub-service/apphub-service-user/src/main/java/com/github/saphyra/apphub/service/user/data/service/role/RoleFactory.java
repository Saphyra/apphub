package com.github.saphyra.apphub.service.user.data.service.role;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.user.data.dao.role.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
class RoleFactory {
    private final IdGenerator idGenerator;

    Role create(UUID userId, String role) {
        return Role.builder()
            .roleId(idGenerator.randomUuid())
            .userId(userId)
            .role(role)
            .build();
    }
}
