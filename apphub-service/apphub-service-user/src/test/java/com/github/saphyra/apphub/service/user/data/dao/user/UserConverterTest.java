package com.github.saphyra.apphub.service.user.data.dao.user;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;
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
    private static final LocalDateTime CURRENT_DATE = LocalDateTime.now();
    private static final Integer LOGIN_FAILURE_COUNT = 25;
    private static final LocalDateTime LOCKED_UNTIL = LocalDateTime.now().plusSeconds(1);

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
            .markedForDeletion(true)
            .markedForDeletionAt(CURRENT_DATE)
            .passwordFailureCount(LOGIN_FAILURE_COUNT)
            .lockedUntil(LOCKED_UNTIL)
            .build();
        given(uuidConverter.convertEntity(USER_ID_STRING)).willReturn(USER_ID);

        User result = underTest.convertEntity(entity);

        assertThat(result.getUserId()).isEqualTo(USER_ID);
        assertThat(result.getUsername()).isEqualTo(USERNAME);
        assertThat(result.getEmail()).isEqualTo(EMAIL);
        assertThat(result.getPassword()).isEqualTo(PASSWORD);
        assertThat(result.getLanguage()).isEqualTo(LANGUAGE);
        assertThat(result.isMarkedForDeletion()).isTrue();
        assertThat(result.getMarkedForDeletionAt()).isEqualTo(CURRENT_DATE);
        assertThat(result.getPasswordFailureCount()).isEqualTo(LOGIN_FAILURE_COUNT);
        assertThat(result.getLockedUntil()).isEqualTo(LOCKED_UNTIL);
    }

    @Test
    public void convertEntity_nullMarkedForDeletion() {
        UserEntity entity = UserEntity.builder()
            .userId(USER_ID_STRING)
            .username(USERNAME)
            .email(EMAIL)
            .password(PASSWORD)
            .language(LANGUAGE)
            .markedForDeletion(null)
            .markedForDeletionAt(CURRENT_DATE)
            .passwordFailureCount(LOGIN_FAILURE_COUNT)
            .lockedUntil(LOCKED_UNTIL)
            .build();
        given(uuidConverter.convertEntity(USER_ID_STRING)).willReturn(USER_ID);

        User result = underTest.convertEntity(entity);

        assertThat(result.getUserId()).isEqualTo(USER_ID);
        assertThat(result.getUsername()).isEqualTo(USERNAME);
        assertThat(result.getEmail()).isEqualTo(EMAIL);
        assertThat(result.getPassword()).isEqualTo(PASSWORD);
        assertThat(result.getLanguage()).isEqualTo(LANGUAGE);
        assertThat(result.isMarkedForDeletion()).isFalse();
        assertThat(result.getMarkedForDeletionAt()).isEqualTo(CURRENT_DATE);
        assertThat(result.getPasswordFailureCount()).isEqualTo(LOGIN_FAILURE_COUNT);
        assertThat(result.getLockedUntil()).isEqualTo(LOCKED_UNTIL);
    }

    @Test
    public void convertDomain() {
        User user = User.builder()
            .userId(USER_ID)
            .username(USERNAME)
            .email(EMAIL)
            .password(PASSWORD)
            .language(LANGUAGE)
            .markedForDeletion(true)
            .markedForDeletionAt(CURRENT_DATE)
            .passwordFailureCount(LOGIN_FAILURE_COUNT)
            .lockedUntil(LOCKED_UNTIL)
            .build();
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);

        UserEntity result = underTest.convertDomain(user);

        assertThat(result.getUserId()).isEqualTo(USER_ID_STRING);
        assertThat(result.getUsername()).isEqualTo(USERNAME);
        assertThat(result.getEmail()).isEqualTo(EMAIL);
        assertThat(result.getPassword()).isEqualTo(PASSWORD);
        assertThat(result.getLanguage()).isEqualTo(LANGUAGE);
        assertThat(result.getMarkedForDeletion()).isTrue();
        assertThat(result.getMarkedForDeletionAt()).isEqualTo(CURRENT_DATE);
        assertThat(result.getPasswordFailureCount()).isEqualTo(LOGIN_FAILURE_COUNT);
        assertThat(result.getLockedUntil()).isEqualTo(LOCKED_UNTIL);
    }
}