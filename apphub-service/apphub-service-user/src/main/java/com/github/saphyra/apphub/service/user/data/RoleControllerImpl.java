package com.github.saphyra.apphub.service.user.data;

import com.github.saphyra.apphub.api.user.model.request.RoleRequest;
import com.github.saphyra.apphub.api.user.model.response.UserRoleResponse;
import com.github.saphyra.apphub.api.user.server.RoleController;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.service.user.data.service.role.AddRoleToAllProperties;
import com.github.saphyra.apphub.service.user.data.service.role.RoleToAllService;
import com.github.saphyra.apphub.service.user.data.service.role.RoleAdditionService;
import com.github.saphyra.apphub.service.user.data.service.role.RoleQueryService;
import com.github.saphyra.apphub.service.user.data.service.role.RoleRemovalService;
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
    public void addRole(RoleRequest roleRequest) {
        log.info("AddRoleRequest: {}", roleRequest);
        roleAdditionService.addRole(roleRequest);
    }

    @Override
    public void removeRole(RoleRequest roleRequest) {
        log.info("RemoveRoleRequest: {}", roleRequest);
        roleRemovalService.removeRole(roleRequest);
    }

    @Override
    public void addToAll(OneParamRequest<String> password, String role, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to add role {} to all", accessTokenHeader.getUserId(), role);
        roleToAllService.addToAll(accessTokenHeader.getUserId(), password.getValue(), role);
    }

    @Override
    public void removeFromAll(OneParamRequest<String> password, String role, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to remove role {} from all", accessTokenHeader.getUserId(), role);
        roleToAllService.removeFromAll(accessTokenHeader.getUserId(), password.getValue(), role);
    }

    @Override
    public List<String> getRolesForAllRestrictedRoles() {
        log.info("Querying roles cannot be added to all...");
        return addRoleToAllProperties.getRestrictedRoles();
    }
}
