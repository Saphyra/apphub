package com.github.saphyra.apphub.service.platform.main_gateway.service.authorization.authentication;

import com.github.saphyra.apphub.api.etc.user.client.DisabledRoleClient;
import com.github.saphyra.apphub.api.etc.user.model.role.DisabledRoleResponse;
import com.github.saphyra.apphub.lib.common_domain.Role;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.test.StepVerifier;

import java.util.List;

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

        StepVerifier.create(underTest.get())
            .expectNextMatches(roles -> roles.contains(Role.ACCESS))
            .verifyComplete();

        StepVerifier.create(underTest.get())
            .expectNextMatches(roles -> roles.contains(Role.ACCESS))
            .verifyComplete();

        then(client).should(times(1)).getDisabledRoles();
    }

    @Test
    void clear() {
        given(client.getDisabledRoles()).willReturn(List.of(disabledRoleResponse, enabledRoleResponse));
        given(disabledRoleResponse.isDisabled()).willReturn(true);
        given(enabledRoleResponse.isDisabled()).willReturn(false);
        given(disabledRoleResponse.getRole()).willReturn(Role.ACCESS);

        StepVerifier.create(underTest.get())
            .expectNextMatches(roles -> roles.contains(Role.ACCESS))
            .verifyComplete();

        underTest.clear();

        StepVerifier.create(underTest.get())
            .expectNextMatches(roles -> roles.contains(Role.ACCESS))
            .verifyComplete();

        then(client).should(times(2)).getDisabledRoles();
    }
}