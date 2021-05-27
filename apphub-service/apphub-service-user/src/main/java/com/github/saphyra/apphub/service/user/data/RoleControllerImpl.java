package com.github.saphyra.apphub.service.user.data;

import com.github.saphyra.apphub.api.user.model.request.RoleRequest;
import com.github.saphyra.apphub.api.user.model.response.UserRoleResponse;
import com.github.saphyra.apphub.api.user.server.RoleController;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
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
}
