package com.github.saphyra.apphub.service.user.authentication.service;

import com.github.saphyra.apphub.api.user.model.response.InternalAccessTokenResponse;
import com.github.saphyra.apphub.service.user.authentication.dao.AccessToken;
import com.github.saphyra.apphub.service.user.data.dao.role.Role;
import com.github.saphyra.apphub.service.user.data.dao.role.RoleDao;
import com.github.saphyra.apphub.service.user.disabled_role.dao.DisabledRoleEntity;
import com.github.saphyra.apphub.service.user.disabled_role.dao.DisabledRoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
@RequiredArgsConstructor
@Slf4j
public class AccessTokenToResponseMapper {
    private final RoleDao roleDao;
    private final DisabledRoleRepository disabledRoleRepository;

    public InternalAccessTokenResponse map(AccessToken accessToken) {
        List<String> disabledRoles = StreamSupport.stream(disabledRoleRepository.findAll().spliterator(), false)
            .map(DisabledRoleEntity::getRole)
            .collect(Collectors.toList());

        return InternalAccessTokenResponse.builder()
            .accessTokenId(accessToken.getAccessTokenId())
            .userId(accessToken.getUserId())
            .roles(getRoles(accessToken.getUserId(), disabledRoles))
            .build();
    }

    private List<String> getRoles(UUID userId, List<String> disabledRoles) {
        return roleDao.getByUserId(userId)
            .stream()
            .map(Role::getRole)
            .filter(role -> !disabledRoles.contains(role))
            .collect(Collectors.toList());
    }
}
