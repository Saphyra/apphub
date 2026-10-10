package com.github.saphyra.apphub.service.etc.modules.service;

import com.github.saphyra.apphub.api.etc.user.client.DisabledRoleClient;
import com.github.saphyra.apphub.api.etc.user.model.role.DisabledRoleResponse;
import com.github.saphyra.apphub.lib.common_domain.Role;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class DisabledRoleCacheTest {
    @Mock
    private DisabledRoleClient client;

    @InjectMocks
    private DisabledRoleCache underTest;

    @Mock
    private DisabledRoleResponse disabledRoleResponse;

    @Mock
    private DisabledRoleResponse enabledRoleResponse;

    @Test
    void get() {
        given(client.getDisabledRoles()).willReturn(List.of(disabledRoleResponse, enabledRoleResponse));
        given(disabledRoleResponse.isDisabled()).willReturn(true);
        given(enabledRoleResponse.isDisabled()).willReturn(false);
        given(disabledRoleResponse.getRole()).willReturn(Role.ACCESS);

        assertThat(underTest.get()).containsExactlyInAnyOrder(Role.ACCESS);
        assertThat(underTest.get()).containsExactlyInAnyOrder(Role.ACCESS);

        then(client).should(times(1)).getDisabledRoles();
    }

    @Test
    void clear() {
        given(client.getDisabledRoles()).willReturn(List.of(disabledRoleResponse, enabledRoleResponse));
        given(disabledRoleResponse.isDisabled()).willReturn(true);
        given(enabledRoleResponse.isDisabled()).willReturn(false);
        given(disabledRoleResponse.getRole()).willReturn(Role.ACCESS);

        assertThat(underTest.get()).containsExactlyInAnyOrder(Role.ACCESS);
        underTest.clear();
        assertThat(underTest.get()).containsExactlyInAnyOrder(Role.ACCESS);

        then(client).should(times(2)).getDisabledRoles();
    }
}