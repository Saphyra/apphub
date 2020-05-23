package com.github.saphyra.apphub.service.user.data.dao.role;


import com.github.saphyra.apphub.lib.common_util.UuidConverter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class RoleDaoTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String USER_ID_STRING = "user-id";

    @Mock
    private RoleConverter converter;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UuidConverter uuidConverter;

    @InjectMocks
    private RoleDao underTest;

    @Mock
    private RoleEntity entity;

    @Mock
    private Role role;

    @Test
    public void getByUserId() {
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(roleRepository.getByUserId(USER_ID_STRING)).willReturn(Arrays.asList(entity));
        given(converter.convertEntity(Arrays.asList(entity))).willReturn(Arrays.asList(role));

        List<Role> result = underTest.getByUserId(USER_ID);

        assertThat(result).containsExactly(role);
    }
}