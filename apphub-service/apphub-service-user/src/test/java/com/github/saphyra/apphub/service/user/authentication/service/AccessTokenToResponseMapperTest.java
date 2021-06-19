package com.github.saphyra.apphub.service.user.authentication.service;

import com.github.saphyra.apphub.api.user.model.response.InternalAccessTokenResponse;
import com.github.saphyra.apphub.service.user.authentication.dao.AccessToken;
import com.github.saphyra.apphub.service.user.data.dao.role.Role;
import com.github.saphyra.apphub.service.user.data.dao.role.RoleDao;
import com.github.saphyra.apphub.service.user.disabled_role.dao.DisabledRoleEntity;
import com.github.saphyra.apphub.service.user.disabled_role.dao.DisabledRoleRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class AccessTokenToResponseMapperTest {
    private static final String DISABLED_ROLE = "disabled-role";
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String ROLE = "role";
    private static final UUID ACCESS_TOKEN_ID = UUID.randomUUID();

    @Mock
    private RoleDao roleDao;

    @Mock
    private DisabledRoleRepository disabledRoleRepository;

    @InjectMocks
    private AccessTokenToResponseMapper underTest;

    @Mock
    private Role role;

    @Mock
    private Role disabledRole;

    @Mock
    private AccessToken accessToken;

    @Test
    public void map() {
        given(disabledRoleRepository.findAll()).willReturn(Arrays.asList(new DisabledRoleEntity(DISABLED_ROLE)));
        given(roleDao.getByUserId(USER_ID)).willReturn(Arrays.asList(role, disabledRole));
        given(role.getRole()).willReturn(ROLE);
        given(disabledRole.getRole()).willReturn(DISABLED_ROLE);

        given(accessToken.getAccessTokenId()).willReturn(ACCESS_TOKEN_ID);
        given(accessToken.getUserId()).willReturn(USER_ID);

        InternalAccessTokenResponse result = underTest.map(accessToken);

        assertThat(result.getAccessTokenId()).isEqualTo(ACCESS_TOKEN_ID);
        assertThat(result.getUserId()).isEqualTo(USER_ID);
        assertThat(result.getRoles()).containsExactly(ROLE);
    }
}