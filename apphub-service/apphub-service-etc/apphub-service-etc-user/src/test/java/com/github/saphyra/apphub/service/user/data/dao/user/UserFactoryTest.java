package com.github.saphyra.apphub.service.user.data.dao.user;

import com.github.saphyra.apphub.lib.common_domain.Role;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.encryption.impl.PasswordService;
import com.github.saphyra.apphub.service.user.config.properties.RegistrationProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class UserFactoryTest {
    private static final String EMAIL = "email@email.com";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String LANGUAGE = "hu";
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String HASHED_PASSWORD = "hashed-password";
    private static final List<Role> DEFAULT_ROLES = List.of(Role.ACCESS);

    @Mock
    private IdGenerator idGenerator;

    @Mock
    private PasswordService passwordService;

    @Mock
    private RegistrationProperties registrationProperties;

    @InjectMocks
    private UserFactory underTest;

    @Test
    void create() {
        given(idGenerator.randomUuid()).willReturn(USER_ID);
        given(passwordService.hashPassword(PASSWORD, USER_ID)).willReturn(HASHED_PASSWORD);
        given(registrationProperties.getDefaultRoles()).willReturn(DEFAULT_ROLES);

        User result = underTest.create(EMAIL, USERNAME, PASSWORD, LANGUAGE);

        assertThat(result.getUserId()).isEqualTo(USER_ID);
        assertThat(result.getEmail()).isEqualTo(EMAIL);
        assertThat(result.getUsername()).isEqualTo(USERNAME);
        assertThat(result.getPassword()).isEqualTo(HASHED_PASSWORD);
        assertThat(result.getLanguage()).isEqualTo(LANGUAGE);
        assertThat(result.getRoles()).isEqualTo(DEFAULT_ROLES);
    }
}