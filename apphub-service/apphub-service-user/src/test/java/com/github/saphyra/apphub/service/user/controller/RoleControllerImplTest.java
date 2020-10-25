package com.github.saphyra.apphub.service.user.controller;

import com.github.saphyra.apphub.api.user.model.request.RoleRequest;
import com.github.saphyra.apphub.api.user.model.response.UserRoleResponse;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.service.user.data.service.role.RoleAdditionService;
import com.github.saphyra.apphub.service.user.data.service.role.RoleQueryService;
import com.github.saphyra.apphub.service.user.data.service.role.RoleRemovalService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class RoleControllerImplTest {
    private static final String QUERY_STRING = "query-string";

    @Mock
    private RoleAdditionService roleAdditionService;

    @Mock
    private RoleQueryService roleQueryService;

    @Mock
    private RoleRemovalService roleRemovalService;

    @InjectMocks
    private RoleControllerImpl underTest;

    @Mock
    private UserRoleResponse userRoleResponse;

    @Mock
    private RoleRequest roleRequest;

    @Test
    public void getRoles() {
        given(roleQueryService.getRoles(QUERY_STRING)).willReturn(Arrays.asList(userRoleResponse));

        List<UserRoleResponse> result = underTest.getRoles(new OneParamRequest<>(QUERY_STRING));

        assertThat(result).containsExactly(userRoleResponse);
    }

    @Test
    public void addRole() {
        underTest.addRole(roleRequest);

        verify(roleAdditionService).addRole(roleRequest);
    }

    @Test
    public void removeRole() {
        underTest.removeRole(roleRequest);

        verify(roleRemovalService).removeRole(roleRequest);
    }
}