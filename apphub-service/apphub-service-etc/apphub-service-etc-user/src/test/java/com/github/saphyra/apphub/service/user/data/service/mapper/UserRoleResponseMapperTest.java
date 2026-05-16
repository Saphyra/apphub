package com.github.saphyra.apphub.service.user.data.service.mapper;

import com.github.saphyra.apphub.api.etc.user.model.role.UserRoleResponse;
import com.github.saphyra.apphub.lib.common_domain.Role;
import com.github.saphyra.apphub.service.user.data.dao.user.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class UserRoleResponseMapperTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String EMAIL = "test@example.com";
    private static final String USERNAME = "testuser";
    private static final List<Role> ROLES = List.of(Role.ACCESS);

    @InjectMocks
    private UserRoleResponseMapper underTest;

    @Test
    void map() {
        User user = User.builder()
            .userId(USER_ID)
            .email(EMAIL)
            .username(USERNAME)
            .password("password")
            .language("en")
            .roles(ROLES)
            .build();

        UserRoleResponse result = underTest.map(user);

        assertThat(result.getUserId()).isEqualTo(USER_ID);
        assertThat(result.getEmail()).isEqualTo(EMAIL);
        assertThat(result.getUsername()).isEqualTo(USERNAME);
        assertThat(result.getRoles()).isEqualTo(ROLES);
    }
}