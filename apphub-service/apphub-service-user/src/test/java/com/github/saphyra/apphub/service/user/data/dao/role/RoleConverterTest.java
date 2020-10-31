package com.github.saphyra.apphub.service.user.data.dao.role;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class RoleConverterTest {
    private static final String ROLE_ID_STRING = "role-id";
    private static final String USER_ID_STRING = "user-id";
    private static final String ROLE = "role";
    private static final UUID ROLE_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private UuidConverter uuidConverter;

    @InjectMocks
    private RoleConverter underTest;

    @Test
    public void convertEntity() {
        RoleEntity entity = RoleEntity.builder()
            .roleId(ROLE_ID_STRING)
            .userId(USER_ID_STRING)
            .role(ROLE)
            .build();
        given(uuidConverter.convertEntity(ROLE_ID_STRING)).willReturn(ROLE_ID);
        given(uuidConverter.convertEntity(USER_ID_STRING)).willReturn(USER_ID);

        Role result = underTest.convertEntity(entity);

        assertThat(result.getRoleId()).isEqualTo(ROLE_ID);
        assertThat(result.getUserId()).isEqualTo(USER_ID);
        assertThat(result.getRole()).isEqualTo(ROLE);
    }

    @Test
    public void convertDomain() {
        Role role = Role.builder()
            .roleId(ROLE_ID)
            .userId(USER_ID)
            .role(ROLE)
            .build();
        given(uuidConverter.convertDomain(ROLE_ID)).willReturn(ROLE_ID_STRING);
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);

        RoleEntity result = underTest.convertDomain(role);

        assertThat(result.getRoleId()).isEqualTo(ROLE_ID_STRING);
        assertThat(result.getUserId()).isEqualTo(USER_ID_STRING);
        assertThat(result.getRole()).isEqualTo(ROLE);
    }
}