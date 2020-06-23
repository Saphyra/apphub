package com.github.saphyra.apphub.lib.security.role;

import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class RequiredRoleCheckerTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String ROLE = "role";

    @Mock
    private AccessTokenProvider accessTokenProvider;

    @InjectMocks
    private RequiredRoleChecker underTest;

    @Mock
    private AccessTokenHeader accessTokenHeader;

    @Mock
    private RoleSetting roleSetting;

    @Test
    public void accessTokenNotPresent() {
        given(accessTokenProvider.getOptional()).willReturn(Optional.empty());

        boolean result = underTest.hasRequiredRoles(Arrays.asList(roleSetting));

        assertThat(result).isFalse();
    }

    @Test
    public void hasNotRequiredRole() {
        given(accessTokenProvider.getOptional()).willReturn(Optional.of(accessTokenHeader));
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);
        given(accessTokenHeader.getRoles()).willReturn(Collections.emptyList());
        given(roleSetting.getRequiredRoles()).willReturn(Arrays.asList(ROLE));

        boolean result = underTest.hasRequiredRoles(Arrays.asList(roleSetting));

        assertThat(result).isFalse();
    }

    @Test
    public void hasRequiredRole() {
        given(accessTokenProvider.getOptional()).willReturn(Optional.of(accessTokenHeader));
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);
        given(accessTokenHeader.getRoles()).willReturn(Arrays.asList(ROLE));
        given(roleSetting.getRequiredRoles()).willReturn(Arrays.asList(ROLE));

        boolean result = underTest.hasRequiredRoles(Arrays.asList(roleSetting));

        assertThat(result).isTrue();
    }
}