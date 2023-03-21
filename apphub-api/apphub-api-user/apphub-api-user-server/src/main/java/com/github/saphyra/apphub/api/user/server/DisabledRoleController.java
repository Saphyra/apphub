package com.github.saphyra.apphub.api.user.server;

import com.github.saphyra.apphub.api.user.model.response.DisabledRoleResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.config.common.Endpoints;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

public interface DisabledRoleController {
    /**
     * Disable a role for all the users after checking the password of the admin.
     * Feature is used when a feature is set as default, but should not be enabled, or a critical bug is discovered.
     */
    @PutMapping(Endpoints.USER_DATA_DISABLE_ROLE)
    void disableRole(@RequestBody OneParamRequest<String> password, @PathVariable("role") String role, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    /**
     * Re-enabling a role what had been disabled so users can access the feature
     */
    @DeleteMapping(Endpoints.USER_DATA_ENABLE_ROLE)
    void enableRole(@RequestBody OneParamRequest<String> password, @PathVariable("role") String role, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    /**
     * Currently disabled roles
     */
    @GetMapping(Endpoints.USER_DATA_GET_DISABLED_ROLES)
    List<DisabledRoleResponse> getDisabledRoles();
}
