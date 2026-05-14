package com.github.saphyra.apphub.service.user.data;

import com.github.saphyra.apphub.api.etc.user.model.role.RoleRequest;
import com.github.saphyra.apphub.api.etc.user.model.role.UserRoleResponse;
import com.github.saphyra.apphub.api.etc.user.server.RoleController;
import com.github.saphyra.apphub.lib.common_domain.AccessToken;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.lib.common_domain.OneParamResponse;
import com.github.saphyra.apphub.lib.common_domain.Role;
import com.github.saphyra.apphub.service.user.config.properties.AddRoleToAllProperties;
import com.github.saphyra.apphub.service.user.data.dao.user.User;
import com.github.saphyra.apphub.service.user.data.service.RoleAdditionService;
import com.github.saphyra.apphub.service.user.data.service.RoleRemovalService;
import com.github.saphyra.apphub.service.user.data.service.RoleToAllService;
import com.github.saphyra.apphub.service.user.data.service.UserQueryService;
import com.github.saphyra.apphub.service.user.data.service.mapper.UserRoleResponseMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@RestController
class RoleControllerImpl implements RoleController {
    private final RoleAdditionService roleAdditionService;
    private final UserQueryService userQueryService;
    private final RoleRemovalService roleRemovalService;
    private final RoleToAllService roleToAllService;
    private final AddRoleToAllProperties addRoleToAllProperties;
    private final UserRoleResponseMapper userRoleResponseMapper;

    @Override
    public List<UserRoleResponse> getRoles(OneParamRequest<String> queryString) {
        log.info("getRoles called with query string {}", queryString);
        return userRoleResponseMapper.map(userQueryService.getUsers(queryString.getValue()));
    }

    @Override
    public UserRoleResponse addRole(RoleRequest roleRequest, AccessToken accessToken) {
        log.info("AddRoleRequest: {}", roleRequest);
        User user = roleAdditionService.addRole(accessToken.getUserId(), roleRequest);

        return userRoleResponseMapper.map(user);
    }

    @Override
    public UserRoleResponse removeRole(RoleRequest roleRequest, AccessToken accessToken) {
        log.info("RemoveRoleRequest: {}", roleRequest);
        User user = roleRemovalService.removeRole(accessToken.getUserId(), roleRequest);

        return userRoleResponseMapper.map(user);
    }

    @Override
    public void addToAll(OneParamRequest<String> password, String role, AccessToken accessToken) {
        log.info("{} wants to add role {} to all", accessToken.getUserId(), role);
        roleToAllService.addToAll(accessToken.getUserId(), password.getValue(), role);
    }

    @Override
    public void removeFromAll(OneParamRequest<String> password, String role, AccessToken accessToken) {
        log.info("{} wants to remove role {} from all", accessToken.getUserId(), role);
        roleToAllService.removeFromAll(accessToken.getUserId(), password.getValue(), role);
    }

    @Override
    public List<String> getRolesForAllRestrictedRoles() {
        log.info("Querying roles cannot be added to all...");
        return addRoleToAllProperties.getRestrictedRoles();
    }

    @Override
    public OneParamResponse<Boolean> isUserAdmin(AccessToken accessToken) {
        log.info("Checking if user {} is admin", accessToken.getUserId());

        return new OneParamResponse<>(accessToken.getRoles().contains(Role.ADMIN));
    }
}
