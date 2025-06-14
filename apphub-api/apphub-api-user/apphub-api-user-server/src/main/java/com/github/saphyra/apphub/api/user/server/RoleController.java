package com.github.saphyra.apphub.api.user.server;

import com.github.saphyra.apphub.api.user.model.role.RoleRequest;
import com.github.saphyra.apphub.api.user.model.role.UserRoleResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.lib.common_domain.OneParamResponse;
import com.github.saphyra.apphub.lib.config.common.endpoints.UserEndpoints;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

public interface RoleController {
    /**
     * Searching for users by the given query, and for their roles
     *
     * @param queryString Username or email of users to search for
     * @return Roles of matching users
     */
    @PostMapping(UserEndpoints.USER_DATA_GET_USER_ROLES)
    List<UserRoleResponse> getRoles(@RequestBody OneParamRequest<String> queryString);

    /**
     * Adding a specific role to the given user
     */
    @PutMapping(UserEndpoints.USER_DATA_ADD_ROLE)
    UserRoleResponse addRole(@RequestBody RoleRequest roleRequest, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    /**
     * Removing a specific role to the given user
     */
    @DeleteMapping(UserEndpoints.USER_DATA_REMOVE_ROLE)
    UserRoleResponse removeRole(@RequestBody RoleRequest roleRequest, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    /**
     * Adding a specific role to all the existing users, after checking the password of the admin
     */
    @PostMapping(UserEndpoints.USER_DATA_ADD_ROLE_TO_ALL)
    void addToAll(@RequestBody OneParamRequest<String> password, @PathVariable("role") String role, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    /**
     * Removing a specific role to all the existing users, after checking the password of the admin
     */
    @DeleteMapping(UserEndpoints.USER_DATA_REMOVE_ROLE_FROM_ALL)
    void removeFromAll(@RequestBody OneParamRequest<String> password, @PathVariable("role") String role, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    /**
     * For security reasons some roles cannot be added to all of the users by one click.
     */
    @GetMapping(UserEndpoints.USER_DATA_ROLES_FOR_ALL_RESTRICTED)
    List<String> getRolesForAllRestrictedRoles();

    @GetMapping(UserEndpoints.IS_ADMIN)
    //TODO API test
    OneParamResponse<Boolean> isUserAdmin(@RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);
}
