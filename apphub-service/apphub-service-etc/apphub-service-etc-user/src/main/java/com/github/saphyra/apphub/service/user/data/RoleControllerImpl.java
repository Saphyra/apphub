package com.github.saphyra.apphub.service.user.data;

import com.github.saphyra.apphub.api.etc.user.model.role.RoleRequest;
import com.github.saphyra.apphub.api.etc.user.model.role.UserRoleResponse;
import com.github.saphyra.apphub.api.etc.user.server.RoleController;
import com.github.saphyra.apphub.lib.common_domain.AccessToken;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.lib.common_domain.OneParamResponse;
import com.github.saphyra.apphub.service.user.data.service.role.AddRoleToAllProperties;
import com.github.saphyra.apphub.service.user.data.service.role.RoleAdditionService;
import com.github.saphyra.apphub.service.user.data.service.role.RoleQueryService;
import com.github.saphyra.apphub.service.user.data.service.role.RoleRemovalService;
import com.github.saphyra.apphub.service.user.data.service.role.RoleToAllService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@RestController
class RoleControllerImpl implements RoleController {
    private final RoleAdditionService roleAdditionService;
    private final RoleQueryService roleQueryService;
    private final RoleRemovalService roleRemovalService;
    private final RoleToAllService roleToAllService;
    private final AddRoleToAllProperties addRoleToAllProperties;

    @Override
    public List<UserRoleResponse> getRoles(OneParamRequest<String> queryString) {
        log.info("getRoles called with query string {}", queryString);
        return roleQueryService.getRoles(queryString.getValue());
    }

    @Override
    public UserRoleResponse addRole(RoleRequest roleRequest, AccessToken accessToken) {
        log.info("AddRoleRequest: {}", roleRequest);
        roleAdditionService.addRole(accessToken.getUserId(), roleRequest);

        return roleQueryService.getRoles(roleRequest.getUserId());
    }

    @Override
    public UserRoleResponse removeRole(RoleRequest roleRequest, AccessToken accessToken) {
        log.info("RemoveRoleRequest: {}", roleRequest);
        roleRemovalService.removeRole(accessToken.getUserId(), roleRequest);

        return roleQueryService.getRoles(roleRequest.getUserId());
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

        return new OneParamResponse<>(accessToken.getRoles().contains(Constants.ROLE_ADMIN));
    }
}
