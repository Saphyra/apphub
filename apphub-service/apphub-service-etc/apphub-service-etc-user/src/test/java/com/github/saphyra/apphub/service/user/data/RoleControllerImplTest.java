package com.github.saphyra.apphub.service.user.data;

import com.github.saphyra.apphub.api.etc.user.model.role.RoleRequest;
import com.github.saphyra.apphub.api.etc.user.model.role.UserRoleResponse;
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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class RoleControllerImplTest {
    private static final String QUERY_STRING = "query-string";
    private static final String ROLE = "role";
    private static final String PASSWORD = "password";
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private RoleAdditionService roleAdditionService;

    @Mock
    private UserQueryService userQueryService;

    @Mock
    private RoleRemovalService roleRemovalService;

    @Mock
    private RoleToAllService roleToAllService;

    @Mock
    private AddRoleToAllProperties addRoleToAllProperties;

    @Mock
    private UserRoleResponseMapper userRoleResponseMapper;

    @InjectMocks
    private RoleControllerImpl underTest;

    @Mock
    private User user;

    @Mock
    private RoleRequest roleRequest;

    @Mock
    private AccessToken accessToken;

    @Mock
    private UserRoleResponse userRoleResponse;

    @Test
    public void getRoles() {
        given(userQueryService.getUsers(QUERY_STRING)).willReturn(List.of(user));
        given(userRoleResponseMapper.map(List.of(user))).willReturn(List.of(userRoleResponse));

        List<UserRoleResponse> result = underTest.getRoles(new OneParamRequest<>(QUERY_STRING));

        assertThat(result).containsExactly(userRoleResponse);
    }

    @Test
    public void addRole() {
        given(accessToken.getUserId()).willReturn(USER_ID);
        given(roleAdditionService.addRole(USER_ID, roleRequest)).willReturn(user);
        given(userRoleResponseMapper.map(user)).willReturn(userRoleResponse);

        assertThat(underTest.addRole(roleRequest, accessToken)).isEqualTo(userRoleResponse);
    }

    @Test
    public void removeRole() {
        given(accessToken.getUserId()).willReturn(USER_ID);
        given(roleRemovalService.removeRole(USER_ID, roleRequest)).willReturn(user);
        given(userRoleResponseMapper.map(user)).willReturn(userRoleResponse);

        assertThat(underTest.removeRole(roleRequest, accessToken)).isEqualTo(userRoleResponse);
    }

    @Test
    public void addToAll() {
        given(accessToken.getUserId()).willReturn(USER_ID);

        underTest.addToAll(new OneParamRequest<>(PASSWORD), ROLE, accessToken);

        verify(roleToAllService).addToAll(USER_ID, PASSWORD, ROLE);
    }

    @Test
    public void removeFromAll() {
        given(accessToken.getUserId()).willReturn(USER_ID);

        underTest.removeFromAll(new OneParamRequest<>(PASSWORD), ROLE, accessToken);

        verify(roleToAllService).removeFromAll(USER_ID, PASSWORD, ROLE);
    }

    @Test
    public void rolesForAllRestricted() {
        given(addRoleToAllProperties.getRestrictedRoles()).willReturn(List.of(ROLE));

        List<String> result = underTest.getRolesForAllRestrictedRoles();

        assertThat(result).containsExactly(ROLE);
    }

    @Test
    void isUserAdmin() {
        given(accessToken.getRoles())
            .willReturn(List.of(Role.ADMIN))
            .willReturn(List.of());

        assertThat(underTest.isUserAdmin(accessToken)).returns(true, OneParamResponse::getValue);
        assertThat(underTest.isUserAdmin(accessToken)).returns(false, OneParamResponse::getValue);
    }
}