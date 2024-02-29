package com.github.saphyra.apphub.service.user.data;

import com.github.saphyra.apphub.api.user.model.request.RoleRequest;
import com.github.saphyra.apphub.api.user.model.response.UserRoleResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.service.user.data.service.role.AddRoleToAllProperties;
import com.github.saphyra.apphub.service.user.data.service.role.RoleAdditionService;
import com.github.saphyra.apphub.service.user.data.service.role.RoleQueryService;
import com.github.saphyra.apphub.service.user.data.service.role.RoleRemovalService;
import com.github.saphyra.apphub.service.user.data.service.role.RoleToAllService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
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
    private RoleQueryService roleQueryService;

    @Mock
    private RoleRemovalService roleRemovalService;

    @Mock
    private RoleToAllService roleToAllService;

    @Mock
    private AddRoleToAllProperties addRoleToAllProperties;

    @InjectMocks
    private RoleControllerImpl underTest;

    @Mock
    private UserRoleResponse userRoleResponse;

    @Mock
    private RoleRequest roleRequest;

    @Mock
    private AccessTokenHeader accessTokenHeader;

    @Test
    public void getRoles() {
        given(roleQueryService.getRoles(QUERY_STRING)).willReturn(Arrays.asList(userRoleResponse));

        List<UserRoleResponse> result = underTest.getRoles(new OneParamRequest<>(QUERY_STRING));

        assertThat(result).containsExactly(userRoleResponse);
    }

    @Test
    public void addRole() {
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);

        underTest.addRole(roleRequest, accessTokenHeader);

        verify(roleAdditionService).addRole(USER_ID, roleRequest);
    }

    @Test
    public void removeRole() {
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);

        underTest.removeRole(roleRequest, accessTokenHeader);

        verify(roleRemovalService).removeRole(USER_ID, roleRequest);
    }

    @Test
    public void addToAll() {
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);

        underTest.addToAll(new OneParamRequest<>(PASSWORD), ROLE, accessTokenHeader);

        verify(roleToAllService).addToAll(USER_ID, PASSWORD, ROLE);
    }

    @Test
    public void removeFromAll() {
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);

        underTest.removeFromAll(new OneParamRequest<>(PASSWORD), ROLE, accessTokenHeader);

        verify(roleToAllService).removeFromAll(USER_ID, PASSWORD, ROLE);
    }

    @Test
    public void rolesForAllRestricted() {
        given(addRoleToAllProperties.getRestrictedRoles()).willReturn(List.of(ROLE));

        List<String> result = underTest.getRolesForAllRestrictedRoles();

        assertThat(result).containsExactly(ROLE);
    }
}