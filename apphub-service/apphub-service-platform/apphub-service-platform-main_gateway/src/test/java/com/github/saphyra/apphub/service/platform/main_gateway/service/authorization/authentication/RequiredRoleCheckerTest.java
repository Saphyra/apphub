package com.github.saphyra.apphub.service.platform.main_gateway.service.authorization.authentication;

import com.github.saphyra.apphub.lib.common_domain.AccessToken;
import com.github.saphyra.apphub.lib.common_domain.Role;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class RequiredRoleCheckerTest {
    @Mock
    private DisabledRoleCache disabledRoleCache;

    @InjectMocks
    private RequiredRoleChecker underTest;

    @Mock
    private AccessToken accessToken;

    @Test
    void hasRequiredRoles() {
        given(disabledRoleCache.get()).willReturn(List.of());
        given(accessToken.getRoles()).willReturn(List.of(Role.ACCESS, Role.ADMIN));

        boolean result = underTest.hasRequiredRoles(Set.of(Role.ACCESS, Role.ADMIN), accessToken);

        assertThat(result).isTrue();
    }

    @Test
    void accessTokenDoesNotHaveRequiredRole() {
        given(disabledRoleCache.get()).willReturn(List.of());
        given(accessToken.getRoles()).willReturn(List.of(Role.ACCESS));

        boolean result = underTest.hasRequiredRoles(Set.of(Role.ACCESS, Role.ADMIN), accessToken);

        assertThat(result).isFalse();
    }

    @Test
    void requiredRoleIsDisabled() {
        given(disabledRoleCache.get()).willReturn(List.of(Role.ACCESS));
        given(accessToken.getRoles()).willReturn(List.of(Role.ACCESS, Role.ADMIN));

        boolean result = underTest.hasRequiredRoles(Set.of(Role.ACCESS, Role.ADMIN), accessToken);

        assertThat(result).isFalse();
    }
}