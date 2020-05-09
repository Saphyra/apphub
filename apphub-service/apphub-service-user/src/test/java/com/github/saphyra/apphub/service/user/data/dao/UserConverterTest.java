package com.github.saphyra.apphub.service.user.data.dao;

import com.github.saphyra.apphub.lib.common_util.UuidConverter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class UserConverterTest {
    private static final String USER_ID_STRING = "user-id";
    private static final String USERNAME = "username";
    private static final String EMAIL = "email";
    private static final String PASSWORD = "password";
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String LANGUAGE = "language";

    @Mock
    private UuidConverter uuidConverter;

    @InjectMocks
    private UserConverter underTest;

    @Test
    public void convertEntity() {
        UserEntity entity = UserEntity.builder()
            .userId(USER_ID_STRING)
            .username(USERNAME)
            .email(EMAIL)
            .password(PASSWORD)
            .language(LANGUAGE)
            .build();
        given(uuidConverter.convertEntity(USER_ID_STRING)).willReturn(USER_ID);

        User result = underTest.convertEntity(entity);

        assertThat(result.getUserId()).isEqualTo(USER_ID);
        assertThat(result.getUsername()).isEqualTo(USERNAME);
        assertThat(result.getEmail()).isEqualTo(EMAIL);
        assertThat(result.getPassword()).isEqualTo(PASSWORD);
        assertThat(result.getLanguage()).isEqualTo(LANGUAGE);
    }

    @Test
    public void convertDomain() {
        User user = User.builder()
            .userId(USER_ID)
            .username(USERNAME)
            .email(EMAIL)
            .password(PASSWORD)
            .language(LANGUAGE)
            .build();
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);

        UserEntity result = underTest.convertDomain(user);

        assertThat(result.getUserId()).isEqualTo(USER_ID_STRING);
        assertThat(result.getUsername()).isEqualTo(USERNAME);
        assertThat(result.getEmail()).isEqualTo(EMAIL);
        assertThat(result.getPassword()).isEqualTo(PASSWORD);
        assertThat(result.getLanguage()).isEqualTo(LANGUAGE);
    }
}