package com.github.saphyra.apphub.service.user.data.service.register;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.encryption.impl.PasswordService;
import com.github.saphyra.apphub.service.user.data.dao.user.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class UserFactoryTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String PASSWORD = "password";
    private static final String HASHED_PASSWORD = "hashed-password";
    private static final String EMAIL = "email";
    private static final String USERNAME = "username";
    private static final String LOCALE = "locale";

    @Mock
    private IdGenerator idGenerator;

    @Mock
    private PasswordService passwordService;

    @InjectMocks
    private UserFactory underTest;

    @Test
    public void create() {
        given(idGenerator.randomUUID()).willReturn(USER_ID);
        given(passwordService.hashPassword(PASSWORD)).willReturn(HASHED_PASSWORD);

        User result = underTest.create(EMAIL, USERNAME, PASSWORD, LOCALE);

        assertThat(result.getUserId()).isEqualTo(USER_ID);
        assertThat(result.getUsername()).isEqualTo(USERNAME);
        assertThat(result.getEmail()).isEqualTo(EMAIL);
        assertThat(result.getPassword()).isEqualTo(HASHED_PASSWORD);
        assertThat(result.getLanguage()).isEqualTo(LOCALE);
    }
}