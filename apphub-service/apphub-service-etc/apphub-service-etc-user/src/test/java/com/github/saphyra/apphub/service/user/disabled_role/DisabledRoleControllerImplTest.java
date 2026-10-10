package com.github.saphyra.apphub.service.user.disabled_role;

import com.github.saphyra.apphub.api.etc.user.model.role.DisabledRoleResponse;
import com.github.saphyra.apphub.api.platform.event_gateway.client.EventGatewayApiClient;
import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.lib.common_domain.AccessToken;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.lib.common_domain.Role;
import com.github.saphyra.apphub.lib.event.EmptyEvent;
import com.github.saphyra.apphub.service.user.common.CheckPasswordService;
import com.github.saphyra.apphub.service.user.disabled_role.dao.DisabledRoleEntity;
import com.github.saphyra.apphub.service.user.disabled_role.dao.DisabledRoleRepository;
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
public class DisabledRoleControllerImplTest {
    private static final String PASSWORD = "password";
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private DisabledRoleRepository repository;

    @Mock
    private DisabledRoleProperties properties;

    @Mock
    private CheckPasswordService checkPasswordService;

    @Mock
    private EventGatewayApiClient eventGatewayApiClient;

    @InjectMocks
    private DisabledRoleControllerImpl underTest;

    @Mock
    private AccessToken accessToken;

    @Test
    public void disableRole() {
        given(properties.getRolesCanBeDisabled()).willReturn(Arrays.asList(Role.TEST, Role.ADMIN));
        given(accessToken.getUserId()).willReturn(USER_ID);
        given(repository.findAll()).willReturn(List.of(new DisabledRoleEntity(Role.TEST)));

        List<DisabledRoleResponse> result = underTest.disableRole(new OneParamRequest<>(PASSWORD), Role.TEST, accessToken);

        verify(repository).save(new DisabledRoleEntity(Role.TEST));
        verify(checkPasswordService).checkPassword(USER_ID, PASSWORD);
        assertThat(result).containsExactlyInAnyOrder(new DisabledRoleResponse(Role.TEST, true), new DisabledRoleResponse(Role.ADMIN, false));
        verify(eventGatewayApiClient).sendEvent(SendEventRequest.builder().eventName(EmptyEvent.PLATFORM_DISABLED_ROLES_CHANGED).build());
    }

    @Test
    public void enableRole() {
        given(accessToken.getUserId()).willReturn(USER_ID);
        given(repository.existsById(Role.TEST)).willReturn(true);
        given(properties.getRolesCanBeDisabled()).willReturn(Arrays.asList(Role.TEST, Role.ADMIN));
        given(repository.findAll()).willReturn(List.of(new DisabledRoleEntity(Role.TEST)));

        List<DisabledRoleResponse> result = underTest.enableRole(new OneParamRequest<>(PASSWORD), Role.TEST, accessToken);

        verify(repository).deleteById(Role.TEST);
        verify(checkPasswordService).checkPassword(USER_ID, PASSWORD);
        verify(eventGatewayApiClient).sendEvent(SendEventRequest.builder().eventName(EmptyEvent.PLATFORM_DISABLED_ROLES_CHANGED).build());

        assertThat(result).containsExactlyInAnyOrder(new DisabledRoleResponse(Role.TEST, true), new DisabledRoleResponse(Role.ADMIN, false));
    }

    @Test
    public void getDisabledRoles() {
        given(properties.getRolesCanBeDisabled()).willReturn(Arrays.asList(Role.TEST, Role.ADMIN));
        given(repository.findAll()).willReturn(List.of(new DisabledRoleEntity(Role.TEST)));

        List<DisabledRoleResponse> result = underTest.getDisabledRoles();

        assertThat(result).containsExactlyInAnyOrder(new DisabledRoleResponse(Role.TEST, true), new DisabledRoleResponse(Role.ADMIN, false));
    }
}