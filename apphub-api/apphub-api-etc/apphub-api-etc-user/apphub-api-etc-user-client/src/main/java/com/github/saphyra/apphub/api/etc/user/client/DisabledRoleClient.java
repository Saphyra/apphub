package com.github.saphyra.apphub.api.etc.user.client;

import com.github.saphyra.apphub.api.etc.user.model.UserEndpoints;
import com.github.saphyra.apphub.api.etc.user.model.role.DisabledRoleResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "user-disabled-roles", url = "${serviceHosts.user}")
public interface DisabledRoleClient {
    /**
     * Currently disabled roles
     */
    @GetMapping(UserEndpoints.USER_DATA_GET_DISABLED_ROLES)
    List<DisabledRoleResponse> getDisabledRoles();
}
