package com.github.saphyra.apphub.api.user.server;

import com.github.saphyra.apphub.api.user.model.request.RoleRequest;
import com.github.saphyra.apphub.api.user.model.response.UserRoleResponse;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.lib.config.Endpoints;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

public interface RoleController {
    @RequestMapping(method = RequestMethod.POST, path = Endpoints.USER_DATA_GET_USER_ROLES)
    List<UserRoleResponse> getRoles(@RequestBody OneParamRequest<String> queryString);

    @RequestMapping(method = RequestMethod.PUT, path = Endpoints.USER_DATA_ADD_ROLE)
    void addRole(@RequestBody RoleRequest roleRequest);

    @RequestMapping(method = RequestMethod.DELETE, path = Endpoints.USER_DATA_REMOVE_ROLE)
    void removeRole(@RequestBody RoleRequest roleRequest);
}
