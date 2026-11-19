package com.github.saphyra.apphub.api.etc.user.client;

import com.github.saphyra.apphub.api.etc.user.model.UserEndpoints;
import com.github.saphyra.apphub.api.etc.user.model.role.DisabledRoleResponse;
import org.springframework.web.service.annotation.GetExchange;

import java.util.List;

public interface DisabledRoleClient {
    /**
     * Currently disabled roles
     */
    @GetExchange(UserEndpoints.USER_DATA_GET_DISABLED_ROLES)
    List<DisabledRoleResponse> getDisabledRoles();
}
