package com.github.saphyra.apphub.service.platform.main_gateway.service.authorization.authentication;

import com.github.saphyra.apphub.lib.common_domain.AccessToken;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_domain.Role;
import com.github.saphyra.apphub.lib.config.common.GenericEndpoints;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class RedirectUrlProviderTest {
    private static final UUID USER_ID = UUID.randomUUID();

    private final RedirectUrlProvider underTest = new RedirectUrlProvider();

    @Mock
    private RoleSetting roleSetting;

    @Mock
    private AccessToken accessToken;

    @Test
    void getRedirectUrl() {
        given(roleSetting.getRequiredRoles()).willReturn(List.of(Role.TEST));
        given(accessToken.getUserId()).willReturn(USER_ID);

        String result = underTest.getRedirectUrl(List.of(roleSetting), accessToken);

        assertThat(result).isEqualTo(String.format(
            "%s?error_code=%s&user_id=%s&required_roles=%s",
            GenericEndpoints.ERROR_PAGE,
            ErrorCode.MISSING_ROLE.name(),
            USER_ID,
            Role.TEST
        ));
    }
}