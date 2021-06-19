package com.github.saphyra.apphub.service.user.data.service.role;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.user.data.dao.role.Role;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class RoleFactoryTest {
    private static final UUID ROLE_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String ROLE = "role";

    @Mock
    private IdGenerator idGenerator;

    @InjectMocks
    private RoleFactory underTest;

    @Test
    public void create() {
        given(idGenerator.randomUuid()).willReturn(ROLE_ID);

        Role result = underTest.create(USER_ID, ROLE);

        assertThat(result.getRoleId()).isEqualTo(ROLE_ID);
        assertThat(result.getUserId()).isEqualTo(USER_ID);
        assertThat(result.getRole()).isEqualTo(ROLE);
    }
}